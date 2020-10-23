import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class EventListeners extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String msgContent = event.getMessage().getContentRaw();
        if(event.isFromGuild() && event.getChannel().getName().equals("bot-commands") && !event.getAuthor().isBot()
                && msgContent.startsWith("!role")){
            Guild guild = event.getGuild();
            MessageChannel channel = event.getChannel();
            Member member = event.getMember();
            String mention = member.getUser().getAsMention();
            String roleName = msgContent.substring(msgContent.indexOf(' ')+1);
            if(roleName.isEmpty() || roleName.equals("!role")){
                channel.sendMessage( mention + "Syntax: !role <Rollenname>").queue();
                return;
            }
            List<Role> roles = guild.getRolesByName( roleName, true);
            if(roles.isEmpty()){
                channel.sendMessage( mention +  " Rolle nicht gefunden!").queue();
                return;
            }
            try {
                if(member.getRoles().contains(roles.get(0))){
                    guild.removeRoleFromMember(member, roles.get(0)).queue();
                    channel.sendMessage( mention + " Rolle \"" + roles.get(0).getName() + "\" erfolgreich entfernt!").queue();
                } else {
                    guild.addRoleToMember(member, roles.get(0)).queue();
                    channel.sendMessage(mention + " Rolle \"" + roles.get(0).getName() + "\" erfolgreich hinzugefügt!").queue();
                }
            } catch (HierarchyException e) {
                channel.sendMessage(mention + " Du darfst diese Rolle nicht ändern!").queue();
            }
        }
    }

}
