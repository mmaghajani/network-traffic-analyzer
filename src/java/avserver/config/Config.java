/*** In The Name of Allah ***/
package avserver.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Class to load and hold configuration properties of the system.
 */
public class Config {
	
	public static final String USER = System.getProperty("user.name");
	
	public static String CONFIG_FILE_PATH = "/home/$USER/avserver/config.properties";
	
	// Global configuration properties
	public static String LOG_DIR;
	public static String KEYS_DIR;
	public static String SCAN_SCRIPT;
	
	// File Upload configuration properties
	public static long MAX_FILE_SIZE;
	public static String UPLOAD_DIR;
	public static String TEMP_DIR;
	
	// Database configuration properties
	public static String DB_DRIVER;
	public static String DB_URL;
	public static String DB_NAME;
	public static String DB_USER;
	public static String DB_PASS;
	public static int DB_POOL_SIZE;
	
	/**
	 * Load system configuration properties from a config file.
	 * This method needs to be executed at system startup.
	 */
	public static void loadProperties() throws IOException {
		
		CONFIG_FILE_PATH = CONFIG_FILE_PATH.replace("$USER", USER);
		System.out.println("CONFIG_FILE_PATH = " + CONFIG_FILE_PATH);
		Properties props = new Properties();
		
		try (FileInputStream cin = new FileInputStream(CONFIG_FILE_PATH)) {
			props.load(cin);
			// Global params
			LOG_DIR = props.getProperty("LOG_DIR");
			LOG_DIR = LOG_DIR.replace("$USER", USER);
			if (!LOG_DIR.endsWith(File.separator))
				LOG_DIR += File.separator;
			//
			SCAN_SCRIPT = props.getProperty("SCAN_SCRIPT");
			SCAN_SCRIPT = SCAN_SCRIPT.replace("$USER", USER);
			
			//
			KEYS_DIR = props.getProperty("KEYS_DIR");
			KEYS_DIR = KEYS_DIR.replace("$USER", USER);
			
			// File upload params
			MAX_FILE_SIZE = Long.parseLong(props.getProperty("MAX_FILE_SIZE"));
			//
			TEMP_DIR = props.getProperty("TEMP_DIR");
			TEMP_DIR = TEMP_DIR.replace("$USER", USER);
			if (!TEMP_DIR.endsWith(File.separator))
				TEMP_DIR += File.separator;
			//
			UPLOAD_DIR = props.getProperty("UPLOAD_DIR");
			UPLOAD_DIR = UPLOAD_DIR.replace("$USER", USER);
			if (!UPLOAD_DIR.endsWith(File.separator))
				UPLOAD_DIR += File.separator;
			
			// Database params
			DB_DRIVER = props.getProperty("DB_DRIVER");
			DB_URL = props.getProperty("DB_URL");
			DB_NAME = props.getProperty("DB_NAME");
			DB_USER = props.getProperty("DB_USER");
			DB_PASS = props.getProperty("DB_PASS");
			DB_POOL_SIZE = Integer.parseInt(props.getProperty("DB_POOL_SIZE"));
			
		} catch (FileNotFoundException ex) {
			
			System.out.println("CONFIG FILE NOT FOUND.\n"
							 + "CREATING DEFAULT CONFIG FILE ...");
			// Global params
			LOG_DIR = "/home/$USER/avserver/logs/";
			props.setProperty("LOG_DIR", LOG_DIR);
			LOG_DIR = LOG_DIR.replace("$USER", USER);
			//
			KEYS_DIR = "/home/$USER/avserver/keys/";
			props.setProperty("KEYS_DIR", KEYS_DIR);
			KEYS_DIR = KEYS_DIR.replace("$USER", USER);
			//
			SCAN_SCRIPT = "/home/$USER/avserver/scan.sh";
			props.setProperty("SCAN_SCRIPT", SCAN_SCRIPT);
			SCAN_SCRIPT = SCAN_SCRIPT.replace("$USER", USER);

			// File upload params
			MAX_FILE_SIZE = 10 * 1024 * 1024;
			props.setProperty("MAX_FILE_SIZE", Long.toString(MAX_FILE_SIZE));
			//
			TEMP_DIR = System.getProperty("java.io.tmpdir");
			props.setProperty("TEMP_DIR", TEMP_DIR);
			if (!TEMP_DIR.endsWith(File.separator))
				TEMP_DIR += File.separator;
			//
			UPLOAD_DIR = "/home/$USER/avserver/uploads/";
			props.setProperty("UPLOAD_DIR", UPLOAD_DIR);
			UPLOAD_DIR = UPLOAD_DIR.replace("$USER", USER);

			// Database params
			DB_DRIVER = "org.mariadb.jdbc.Driver";
			props.setProperty("DB_DRIVER", DB_DRIVER);
			DB_URL = "jdbc:mariadb://192.168.56.101:3306/";
			props.setProperty("DB_URL", DB_URL);
			DB_NAME = "av_server_db";
			props.setProperty("DB_NAME", DB_NAME);
			DB_USER = "avuser";
			props.setProperty("DB_USER", DB_USER);
			DB_PASS = "password";
			props.setProperty("DB_PASS", DB_PASS);
			DB_POOL_SIZE = 16;
			props.setProperty("DB_POOL_SIZE", Integer.toString(DB_POOL_SIZE));
			
			File programDir = new File("/home/" + USER + "/avserver/");
			if (!programDir.exists())
				programDir.mkdirs();
			try(FileOutputStream cout = new FileOutputStream(CONFIG_FILE_PATH)) {
				props.store(cout, "DEFAULT CONFIG FILE");
				System.out.println("DONE.");
			}
		}
	}
	
	
	public static String getCurrentConfig() {
		StringBuilder str = new StringBuilder();
		str.append("CONFIG_FILE_PATH = ").append(CONFIG_FILE_PATH);
		str.append("\n  LOG_DIR = ").append(LOG_DIR);
		str.append("\n  KEYS_DIR = ").append(KEYS_DIR);
		str.append("\n  SCAN_SCRIPT = ").append(SCAN_SCRIPT);
		str.append("\n  MAX_FILE_SIZE = ").append(MAX_FILE_SIZE);
		str.append("\n  UPLOAD_DIR = ").append(UPLOAD_DIR);
		str.append("\n  DB_DRIVER = ").append(DB_DRIVER);
		str.append("\n  DB_URL = ").append(DB_URL);
		str.append("\n  DB_NAME = ").append(DB_NAME);
		str.append("\n  DB_USER = ").append(DB_USER);
		str.append("\n  DB_PASS = ").append(DB_PASS);
		str.append("\n  DB_POOL_SIZE = ").append(DB_POOL_SIZE);
		return str.toString();
	}

	
}
