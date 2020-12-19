
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.util.HashMap;

public class Main {
    public static JDA jda;
    public static HashMap<String, ICommand> commands;

    public static void main( String[] args) throws LoginException, InterruptedException {
        Config config = new Config();
        config.loadConfig("config.txt");
        jda = JDABuilder.createDefault(config.config.get("Token"))
                .addEventListeners(new EventListeners())
                .build();
        jda.awaitReady();

        commands = new HashMap<>();
        GetRoleCommand roleCommand = new GetRoleCommand();
        roleCommand.init(config);
        commands.put(roleCommand.getCommand(), roleCommand);
        commands.put("!help", new HelpCommand());
        commands.put( new AddVoiceChannelCommand().getCommand(), new AddVoiceChannelCommand());
    }
}
