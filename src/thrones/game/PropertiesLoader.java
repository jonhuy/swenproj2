package thrones.game;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    public static Properties loadPropertiesFile(String pathname){
        String fullPathname = "/properties/" + pathname;
        Properties properties = new Properties();
        try (InputStream propertiesInput = new FileInputStream(fullPathname);){
            properties.load(propertiesInput);
        } catch (IOException ioException){

        }
        return properties;
    }
}
