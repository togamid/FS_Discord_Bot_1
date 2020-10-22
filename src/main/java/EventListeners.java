import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class EventListeners extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        String msgContent = msg.getContentRaw();
        Member member = event.getMember();
        System.out.println(msg);
        if(event.isFromGuild() && msgContent.startsWith("!role")){
            Guild guild = event.getGuild();
            MessageChannel channel = event.getChannel();
            String roleName = msgContent.substring(msgContent.indexOf(' ')+1);
            if(roleName.isEmpty()){
                channel.sendMessage("Syntax: !role <Rollenname>").queue();
                return;
            }
            List<Role> roles = guild.getRolesByName( roleName, true);
            if(roles.isEmpty()){
                channel.sendMessage("Rolle nicht gefunden!").queue();
                return;
            }
            try {
                if(member.getRoles().contains(roles.get(0))){
                    guild.removeRoleFromMember(member, roles.get(0)).queue();
                    channel.sendMessage("Role removed successfully").queue();
                } else {
                    guild.addRoleToMember(member, roles.get(0)).queue();
                    channel.sendMessage("Role added successfully").queue();
                }
            } catch (HierarchyException e) {
                channel.sendMessage("Du darfst dir diese Rolle nicht zuweisen!").queue();
            }
        }
    }
}
