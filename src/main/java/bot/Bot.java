package bot;

import bot.commands.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;

import javax.security.auth.login.LoginException;
import java.util.HashMap;

public class Bot {
    public static JDA jda;
    public static HashMap<String, ICommand> commands = new HashMap<>();
    public static final Config config = new Config("config.txt");
    private static final String botSignifier = config.get("BotSignifier");
    private static final ICommand[] commandArray = {new GetRoleCommand(config),
            new AddVoiceChannelCommand(config),
            new LoadStudentRolesCommand(config),
            new HelpCommand()};
    public static final String serverName = "Fachschaft Informatik";
    public static Guild guild;


    public void run() throws LoginException, InterruptedException {
        jda = JDABuilder.createDefault(config.get("Token"))
                .addEventListeners(new EventListeners())
                .build();
        jda.awaitReady();

        for (ICommand command : commandArray) {
            commands.put(command.getCommand(), command);
        }
    }
    public static String getSignif(){
        return botSignifier;
    }
}
