import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class main {
    public static JDA jda;

    public static void main( String[] args) throws LoginException, InterruptedException {
        jda = JDABuilder.createDefault("Token")
                .addEventListeners(new EventListeners())
                .build();
        jda.awaitReady();
    }
}
