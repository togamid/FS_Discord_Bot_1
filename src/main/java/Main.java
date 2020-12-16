
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.util.HashMap;

public class Main {
    public static JDA jda;
    public static HashMap<String, ICommand> commands;

    public static void main( String[] args) throws LoginException, InterruptedException {
        jda = JDABuilder.createDefault("Token")
                .addEventListeners(new EventListeners())
                .build();
        jda.awaitReady();

        commands = new HashMap<>();
        commands.put("!role", new GetRoleCommand());
        commands.put("!help", new HelpCommand());
    }
}
