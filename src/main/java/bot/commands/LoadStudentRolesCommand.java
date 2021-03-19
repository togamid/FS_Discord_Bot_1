package bot.commands;

import bot.Bot;
import bot.Config;
import bot.Utility;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

public class LoadStudentRolesCommand implements ICommand {
    //Config parameters
    private static final String command = "updateStudentRoles";
    private final String shortDesc = "Updated die Studentenrollen für den Nutzer. Wenn noch keine vorhanden sind, werden sie neu hinzugefügt. ";
    private final String longDesc = shortDesc + "Nutzung: !loadStudentRole <Nutzname>. Bei falschen Rollen bitte an die Administratoren wenden. Die Rolle kann nur von Studenten verwendet werden";
    private final String privilegedRole="Studi";

    private final Hashtable<String, String> env = new Hashtable<>();
    private String[] returnedAtts;
    private String searchBase;

    public String getShortDesc(){
        return shortDesc;
    }

    public String getLongDesc(){
        return longDesc;
    }

    public String getCommand(){
        return command;
    }

    public void init(Config config){
        env.put(Context.PROVIDER_URL, config.get("LdapProviderUrl"));
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, config.get("LdapSecurityAuthentication"));
        returnedAtts = config.get("LdapReturnedAttributes").split(",");
        searchBase = config.get("LdapSearchBase");
    }

    public LoadStudentRolesCommand(){}
    public LoadStudentRolesCommand(Config config){
        init(config);
    }

    public String run(String args, MessageReceivedEvent event){
        HashMap<String,String> results = new HashMap<>();
        Guild guild = Utility.getGuild(event, Bot.serverName);
        if(guild == null){
            return "Das Laden des betreffenden Servers ist fehlgeschlagen.";
        }
        Member member;
        if(event.isFromGuild()){
            member = event.getMember();
        } else{
            member = guild.retrieveMember(event.getAuthor()).complete();
        }


        if(member.getRoles().stream().filter(role -> role.getName().equalsIgnoreCase(privilegedRole)).count() <1){
            //TODO: this should be its own command and is only temporary
            event.getAuthor().openPrivateChannel().queue(channel -> channel.sendMessage("Wenn du die Rolle \"" + privilegedRole+ "\" haben " +
                    "willst, schreib bitte eine Nachricht mit deiner Hochschul-Email an gaisserto80670@th-nuernberg.de mit dem Betreff \"Discord Authentifizierung\" und deinem Discord-Namen. " +
                    "Du wirst dann so bald wie möglich freigeschalten.").queue());
            return "Um diesen Befehl nutzen zu können, musst du die Rolle \""+ privilegedRole+"\" haben.";
        }
        //validate Input
        if(!args.matches("^[a-z]+[0-9]{5}$")){
            return "Ungültiger Nutzername!";
        }
        try {
            // Opening the connection
            DirContext ldapContext = new InitialDirContext(env);
            SearchControls searchCtls = new SearchControls();
            searchCtls.setReturningAttributes(returnedAtts);
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String searchFilter = "uid="+args;
            SearchResult searchResult;
            NamingEnumeration<SearchResult> answer = ldapContext.search(searchBase, searchFilter, searchCtls);
            if(answer.hasMoreElements()){
                searchResult = answer.next();
            } else {
                return "Nutzername nicht gefunden.";
            }
            Attributes attributes = searchResult.getAttributes();
            for(int i = 0; i<returnedAtts.length; i++){
                results.put(returnedAtts[i], (String) attributes.get(returnedAtts[i]).get());
            }

        } catch (NamingException e) {
            System.out.println("Problem occurs during context initialization !");
            e.printStackTrace();
        }
        //Remove old roles. Master and "Studiert schon zu lange" are missing, as they probably don't change or are used for other purposes
        //TODO: there might be a race condition with readding these roles
        member.getRoles().stream().filter(role -> role.getName().matches("[1-9]+. Semester") || role.getName().matches("[MW]?IN")).forEach(role -> guild.removeRoleFromMember(member, role).queue());

        switch (results.get("employeetype").split(";")[0]){
            case "ST@B-IN":
                addRole(member, guild, "IN");
                break;
            case "ST@B-MIN":
                addRole(member, guild, "MIN");
                break;
            case "ST@B-WIN":
                addRole(member, guild, "WIN");
                break;
            case "ST@M-IN":
                addRole(member, guild, "Master");
                break;
            default:
                addRole(member, guild, "Gast");

        }
        int terms = getNumberOfTerms(results.get("orclactivestartdate"));
        if(terms < 8){
            addRole(member, guild, terms+". Semester");
        } else {
            addRole(member, guild, "Studiert schon zu lange");
        }


        return "Rollen hinzugefügt. Bitte überprüfe mit Klick auf den Namen, ob die Rollen stimmen.";
    }

    public boolean addRole(Member member,Guild guild, String roleString){
        List<Role> roles = guild.getRolesByName(roleString, true);
        if(roles.size() == 1) {
            guild.addRoleToMember(member, roles.get(0)).queue();
            return true;
        } else {
            System.out.println("Did not find the right amount of roles!");
            return false;
        }
    }

    private int getNumberOfTerms(String ldapDate){
        int year = Integer.parseInt(ldapDate.substring(0,4));
        int month = Integer.parseInt(ldapDate.substring(4,6));
        int day = Integer.parseInt(ldapDate.substring(6,8));
        LocalDate date = LocalDate.of(year, month, day);
        LocalDate now = LocalDate.now();
        long months = Period.between(date, now).toTotalMonths();
        return (int) (months/6)+1;


    }
}
