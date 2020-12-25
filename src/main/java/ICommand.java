import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface ICommand {
    String run(String args, MessageReceivedEvent event);
    String getShortDesc();
    String getLongDesc();
    String getCommand();
    void init(Config config);
}
