import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.*;

public class LoadStudentRolesCommand implements ICommand {
    private static final String command = "!loadStudentRole";
    private final String shortDesc = "Lädt die Studentenrollen für den Nutzer. ";
    private final String longDesc = shortDesc + "Nutzung: !loadStudentRole <Nutzname>. Bei falschen Rollen bitte an die Administratoren wenden.";

    private final Hashtable<String, String> env = new Hashtable<>();
    private String[] returnedAtts;
    private String searchBase;
    private SearchResult searchResult;

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
        env.put(Context.PROVIDER_URL, config.config.get("LdapProviderUrl"));
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, config.config.get("LdapSecurityAuthentication"));
        returnedAtts = config.config.get("LdapReturnedAttributes").split(",");
        searchBase = config.config.get("LdapSearchBase");
    }

    public String run(String args, MessageReceivedEvent event){
        HashMap<String,String> results = new HashMap<>();
        if(!event.isFromGuild()){
            return "Dieser command darf nur in Guilds verwendet werden!";
        }
        Guild guild = event.getGuild();
        Member member = event.getMember();

        if(member.getRoles().stream().filter(role -> role.getName().equalsIgnoreCase("Studi")).count() <1){
            return "Um diesen Befehl nutzen zu können, musst du die Rolle \"Student\" haben.";
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
        //TODO: allow adding of roles in a private channel


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
}
