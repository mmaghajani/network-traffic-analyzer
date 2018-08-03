/*** In The Name of Allah ***/
package avserver.config;

import avserver.model.Database;
import avserver.utils.Logger;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Web application lifecycle listener.
 */
public class InitialConfig implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// Set Prefer IPv4 system property
		System.setProperty("java.net.preferIPv4Stack", "true");
		
		// Load configuration properties
		try {
			Config.loadProperties();
		} catch (IOException ex) {
			System.out.println("FAILED TO LOAD CONFIG PROPERTIES -- " + ex);
			//System.out.println("ABORT");
			//System.exit(1);
		}
		
		// Initialize logger
		File logDir = new File(Config.LOG_DIR);
		if (!logDir.exists()) 
			logDir.mkdirs();
		try {
			System.out.println("LOGGER INIT('" + Config.LOG_DIR + "av-server.log')");
			Logger.init(logDir.getPath() + "/av-server.log");
			System.out.println("REDIRECTING STD-ERR ...");
			Logger.redirectStandardError(Config.LOG_DIR + "std.err");
			System.out.println("DONE! :)");
		} catch (IOException ex) {
			System.out.println("FAILED to initalize LOGGER -- " + ex);
		}
		
		// Initialize database connection
		if (Database.init())
			Logger.info("Database Connection Initialized :)");
		else
			Logger.error("Database Connection FAILED xP");
		
		Logger.info("Servlet Context Initialized.");
		Logger.log(Config.getCurrentConfig());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		try {
			Database.close();
		} catch (SQLException ex) {
			Logger.error(ex);
		}
		Logger.info("Servlet Context Destroyed.\nSYSTEM SHUTTING DOWN ...");
		Logger.close();
	}
}
