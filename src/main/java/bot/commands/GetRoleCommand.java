package bot.commands;

import bot.Bot;
import bot.Config;
import bot.Utility;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;

import java.util.Arrays;
import java.util.List;

public class GetRoleCommand implements ICommand {

    private static final String command = "role";
    private final String shortDesc = "Toggelt die entsprechende Gaming Rolle. ";
    private final String longDesc = shortDesc + "Nutzung: !role <Rollenname>. Zum Entfernen den gleichen Command nochmal verwenden.";
    String[] allowedRoles = {};
    private String allowedRolesMessage;

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
        allowedRoles = config.getSelfAssignableRoles();
        StringBuilder builder = new StringBuilder(" Verfügbare Rollen: ");
        for(String role : allowedRoles){
            builder.append(role);
            builder.append(", ");
        }
        allowedRolesMessage = builder.toString();
    }

    public GetRoleCommand(){}
    public GetRoleCommand(Config config){
        init(config);
    }

    public String run(String roleName, MessageReceivedEvent event){
        Guild guild = Utility.getGuild(event, Bot.serverName);
        if(guild == null){
            return "Das Laden des betreffenden Servers ist fehlgeschlagen.";
        }
        Member member = guild.getMember(event.getAuthor());

        if(roleName.isEmpty() || roleName.equals("!role")){

            return "Syntax: !role <Rollenname>." + allowedRolesMessage;
        }

        List<Role> roles = guild.getRolesByName( roleName, true);


        if(roles.isEmpty()){
            return  "Rolle \""+ roleName + "\" nicht gefunden!";
        }


        if(Arrays.stream(allowedRoles).noneMatch((allowedRole) -> roleName.equalsIgnoreCase(allowedRole))) {
            return "Du darfst diese Rolle nicht ändern. Bitte frage einen Administrator." + roles;
        }

        try {
            if(member.getRoles().contains(roles.get(0))){
                guild.removeRoleFromMember(member, roles.get(0)).queue();
                return "Rolle \"" + roles.get(0).getName() + "\" erfolgreich entfernt!";
            } else {
                guild.addRoleToMember(member, roles.get(0)).queue();
                return "Rolle \"" + roles.get(0).getName() + "\" erfolgreich hinzugefügt!";
            }
        } catch (HierarchyException e) {
            return "Du darfst diese Rolle nicht ändern!";
        }
    }
}
