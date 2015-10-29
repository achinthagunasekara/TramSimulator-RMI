/*
 *
 * @author Archie Gunasekara
 * @date 2014
 * 
 */

package tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class ConfigFileReader {
 
	private String configFile = "appConfig.properties";
    private Properties prop;
    private static ConfigFileReader instance;
    
    public static ConfigFileReader getConfigFileReaderInstance() {
        
        if(instance == null) {
        	
            instance = new ConfigFileReader();
        }
        
        return instance;
    }
    
    //make sure there is only one instance of this object per run
    private ConfigFileReader() {
        
        readFile();
    }
    
    //read file
    private void readFile() {
        
        prop = new Properties();

        try {
            //load a properties file
            prop.load(new FileInputStream(configFile));
 
        } catch (IOException ex) {
            
            System.out.println("Error in Config File Reader - " + ex.toString());
        }
    }
    
    public String getPropertyVal(String property) throws Exception {
        
        String val = prop.getProperty(property);
        
        if(val == null) {
            
            throw new Exception (property + " not found in application configuration!");
        }
        
        return prop.getProperty(property);
    }
    
    //split comma seperated values
    public String[] splitVals(String s) {
        
        return s.split(",");
    }
}
