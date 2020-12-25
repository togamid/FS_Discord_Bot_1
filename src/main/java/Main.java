
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.util.HashMap;

public class Main {
    public static JDA jda;
    public static HashMap<String, ICommand> commands = new HashMap<>();
    private static final ICommand[] commandArray = {new GetRoleCommand(), new HelpCommand(), new AddVoiceChannelCommand(), new LoadStudentRolesCommand()};

    public static void main( String[] args) throws LoginException, InterruptedException {
        Config config = new Config();
        config.loadConfig("config.txt");
        jda = JDABuilder.createDefault(config.config.get("Token"))
                .addEventListeners(new EventListeners())
                .build();
        jda.awaitReady();

        for (ICommand command : commandArray) {
            command.init(config);
            commands.put(command.getCommand(), command);
        }
    }
}
