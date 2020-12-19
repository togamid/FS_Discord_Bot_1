import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AddVoiceChannelCommand implements ICommand{


    String shortDesc = "Fügt einen neuen temporären Voicechannel hinzu.";
    String longDesc = shortDesc + " Nutzen mit !tmpvoice <Kanalname>. Der Kanal wird nach einiger Zeit wieder gelöscht.";
    String command = "!tmpvoice";
    String categoryName = "voice-channel";

    @Override
    public String run(String args, MessageReceivedEvent event) {
        if(!event.isFromGuild()){
            return "Dieser Befehl kann leider nur auf Servern genutzt werden";
        }

        if(args.equals(""))
            return longDesc;

        Guild guild = event.getGuild();
        Category category = guild.getCategoriesByName(categoryName, true).get(0);
        guild.createVoiceChannel("[TEMP] " + args, category).queue();
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
}
