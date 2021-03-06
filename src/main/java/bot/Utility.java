package bot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Utility {
    public static Guild getGuild(MessageReceivedEvent event, String servername){
        if(event.isFromGuild()){
            return event.getGuild();
        } else{
           Object[] guilds = event.getAuthor().getMutualGuilds().stream().filter(guild -> guild.getName().equalsIgnoreCase(servername)).toArray();
           if(guilds.length == 1){
               return (Guild) guilds[0];
           } else if(Bot.guild != null) {
               return Bot.guild;
           }
           else{
               System.out.println("Error: couldn't find guild!");
               return null;
           }
        }
    }
}
