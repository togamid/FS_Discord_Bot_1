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
                String data = myReader.nextLine();
                System.out.println(data);
                String[] entry = data.split(":");
                config.put(entry[0], entry[1]);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("The config file could not be loaded. Did your remember to create it?");
            e.printStackTrace();
        }
    }
}
