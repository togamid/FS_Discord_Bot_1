import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;


public class EventListeners extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String msgContent = event.getMessage().getContentRaw();
        String mention;
        if(event.getMember() != null){
           mention = event.getMember().getAsMention();

        } else {
            mention = event.getAuthor().getAsMention();
        }

        //TODO This is only temporary and should be changed
        if(event.isFromGuild() && !event.getAuthor().isBot()){
           VoiceChannel[] channels = event.getGuild().getVoiceChannels().toArray(new VoiceChannel[0]);
            for (VoiceChannel channel : channels ){
                if(channel.getName().startsWith("[TEMP]") && channel.getMembers().size() == 0){
                   event.getGuild().getGuildChannelById(channel.getId()).delete().queue();
                }
            }
        }

        if(!event.getAuthor().isBot() && event.getChannel().getName().equals("bot-commands")){
            String command;
            String args;
            int posSpace = msgContent.indexOf(' ');
            if(posSpace != -1 && posSpace+1<msgContent.length()) {
                command = msgContent.substring(0, posSpace);
                args = msgContent.substring(posSpace+1);
            }
            else {
                command = msgContent;
                args="";
            }

            ICommand commandObj = Main.commands.get(command);

            if(commandObj != null){
                event.getChannel().sendMessage(mention +" "+  commandObj.run(args, event)).queue();
            }
         }
    }


}
