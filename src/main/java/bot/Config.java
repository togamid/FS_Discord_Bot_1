package bot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Config {
    private final HashMap<String, String > config = new HashMap<>();

    private final String[] selfAssignableRoles;


    public Config(String path){
        //set defaults
        config.put("BotSignifier", "!");
        config.put("SelfAssignableRoles", "");
        //load actual values if available
        if(path != null && !path.isEmpty() ){
            loadConfig(path);
        }

        String[] roles = config.get("SelfAssignableRoles").split(",");
        for(int i = 0; i<roles.length; i++){
            roles[i] = roles[i].trim();
        }
        selfAssignableRoles = roles;
    }

    public String[] getSelfAssignableRoles() {
        return selfAssignableRoles;
    }

    public String get(String key){
        return config.get(key);
    }


    public void loadConfig(String path){
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String entry = myReader.nextLine();
                if(!entry.startsWith("#") && !entry.isEmpty()) {
                    String key = entry.substring(0, entry.indexOf(':')).trim();
                    String value = entry.substring(entry.indexOf(':') + 1).trim();
                    config.put(key, value);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("The config file could not be loaded. Did your remember to create it?");
            e.printStackTrace();
        }
    }
}
