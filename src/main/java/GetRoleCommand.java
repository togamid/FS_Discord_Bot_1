import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;

import java.util.List;

public class GetRoleCommand implements ICommand {

    private final String command = "!role";
    private final String shortDesc = "Toggelt die entsprechende Gaming Rolle.";
    private final String longDesc = "Toggelt die entsprechende Gaming Rolle. Nutzung: !role <Rollenname>. Zum entfernen den gleichen Command nochmal verwenden.";

    public String getShortDesc(){
        return shortDesc;
    }

    public String getLongDesc(){
        return longDesc;
    }

    public String getCommand(){
        return command;
    }
    public String run(String args, MessageReceivedEvent event){
        if(!event.isFromGuild()){
            return "Dieser Befehl kann leider nur auf Servern genutzt werden";
        }

        Guild guild = event.getGuild();
        Member member = event.getMember();
        String roleName = args;

        if(roleName.isEmpty() || roleName.equals("!role")){
            return "Syntax: !role <Rollenname>";
        }

        List<Role> roles = guild.getRolesByName( roleName, true);

        if(roles.isEmpty()){
            return  "Rolle \""+ roleName + "\" nicht gefunden!";
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
