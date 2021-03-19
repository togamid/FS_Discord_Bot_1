package bot.commands;

import bot.Config;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class AddVoiceChannelCommand implements ICommand {
    private static final HashMap<String, LocalDateTime> voicechannelCreationTime = new HashMap<>();


    private final String shortDesc = "Fügt einen neuen temporären Voicechannel hinzu.";
    private final String longDesc = shortDesc + " Nutzen mit !tmpvoice <Kanalname>. Der Kanal wird nach einiger Zeit wieder gelöscht.";
    private final String command = "tmpvoice";
    static final int protectedTimeMinutes = 2;
    static final String categoryName = "voice-channel";

    @Override
    public void init(Config config) {
    }

    public AddVoiceChannelCommand(){ }
    public AddVoiceChannelCommand(Config config){
        init(config);
    }

    @Override
    public String run(String args, MessageReceivedEvent event) {
        if(!event.isFromGuild()){
            return "Dieser Befehl kann leider nur auf Servern genutzt werden";
        }

        if(args.equals(""))
            return longDesc;

        Guild guild = event.getGuild();
        Category category = guild.getCategoriesByName(categoryName, true).get(0);
        guild.createVoiceChannel("[TEMP] " + args, category).queue(channel -> voicechannelCreationTime.put(channel.getId(), LocalDateTime.now()));

        return "Kanal \"" + args + "\" erstellt! Bitte gehe schnell in den Kanal, bevor er gelöscht wird";
    }

    @Override
    public String getShortDesc() {
        return shortDesc;
    }

    @Override
    public String getLongDesc() {
        return longDesc;
    }

    @Override
    public String getCommand() {
        return command;
    }

    /**
     * Determines whether a tmpvoice is expired and should be deleted
     * @param channel the channel which should be checked
     * @return true if the channel should be deleted
     */
    public static boolean checkChannel(VoiceChannel channel){
        if(channel.getName().startsWith("[TEMP]") && channel.getMembers().size() == 0){
            LocalDateTime creationTime = voicechannelCreationTime.get(channel.getId());
                return creationTime == null || Duration.between(creationTime, LocalDateTime.now()).toMinutes() > protectedTimeMinutes;

        }
        return false;
    }

}
