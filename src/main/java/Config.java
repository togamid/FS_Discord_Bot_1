import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Config {
    public HashMap<String, String > config = new HashMap<>();

    public void loadConfig(String path){
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String entry = myReader.nextLine();
                System.out.println(entry);
                String key = entry.substring(0,entry.indexOf(':'));
                String value = entry.substring(entry.indexOf(':')+1);
                config.put(key,value);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("The config file could not be loaded. Did your remember to create it?");
            e.printStackTrace();
        }
    }
}
