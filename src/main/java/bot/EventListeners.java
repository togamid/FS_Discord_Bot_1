package bot;

import bot.commands.AddVoiceChannelCommand;
import bot.commands.ICommand;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;


public class EventListeners extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(Bot.guild == null && event.isFromGuild()){
            Bot.guild = event.getGuild();
        }
        String msgContent = event.getMessage().getContentRaw();
        String mention;
        if(event.getMember() != null){
           mention = event.getMember().getAsMention();
        } else {
            mention = event.getAuthor().getAsMention();
        }

        if(!event.getAuthor().isBot()
                && (event.getChannel().getName().equals("bot-commands") || event.isFromType(ChannelType.PRIVATE))
                && msgContent.startsWith(Bot.getSignif())){
            String command;
            String args;
            int posSpace = msgContent.indexOf(' ');
            if(posSpace != -1 && posSpace+1<msgContent.length()) {
                command = msgContent.substring(Bot.getSignif().length(), posSpace);
                args = msgContent.substring(posSpace+1);
            }
            else {
                command = msgContent.substring(Bot.getSignif().length());
                args="";
            }

            ICommand commandObj = Bot.commands.get(command);

            if(commandObj != null){
                String response = commandObj.run(args, event);
                response = response.replace("@", "(at)");
                event.getChannel().sendMessage(mention +" "+  response).queue();
            }
         }
    }
    // TODO: eigentlich war das nur beim verlassen (GuildVoiceLeaveEvent), aber das scheint es irgendiwe nicht mehr zu geben
    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event){
        for(VoiceChannel channel : event.getGuild().getVoiceChannels()){
            if(AddVoiceChannelCommand.checkChannel(channel)){
                event.getGuild().getGuildChannelById(channel.getId()).delete().queue();
            }
        }
    }


}
