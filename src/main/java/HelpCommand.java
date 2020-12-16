import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Collection;

public class HelpCommand implements ICommand{

    String longDesc = "Zeigt diese Nachricht. !help <command> für weitere Informationen";
    String shortDesc = "Zeigt diese Nachricht. !help <command> für weitere Informationen";
    String command = "!help";


    @Override
    public String run(String args, MessageReceivedEvent event) {

        if(args.equals("")) {
            StringBuilder builder = new StringBuilder("Verfügbare Kommandos: \n");
            Collection<ICommand> commandCollection =  Main.commands.values();
            ICommand[] commands=  commandCollection.toArray(new ICommand[0]);
            for (int i = 0; i< commands.length; i++){
                ICommand currCommand = commands[i];
                builder.append(currCommand.getCommand());
                builder.append(": ");
                builder.append(currCommand.getShortDesc());
                builder.append("\n");
            }
            return builder.toString();
        }
        else {
            ICommand currCommand = Main.commands.get("!" + args);
            if(currCommand != null) {
                StringBuilder builder = new StringBuilder(currCommand.getCommand());
                builder.append(": ");
                builder.append(currCommand.getLongDesc());
                return builder.toString();
            }
            else {
                return "Command nicht gefunden. Ohne das \"!\" eingeben";
            }
        }
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
