/*** In The Name of Allah ***/
package avserver.model;

import avserver.config.Config;
import avserver.model.querytasks.*;
import avserver.utils.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Database access proxy class.
 */
public class Database {
	
	private static DatabaseConnectionPool DBC_POOL = null;
	
	/**
	 * Initialize database connection.
	 * This method creates a connection-pool for accessing the database.
	 * This method should be executed only once at the start of the application lifecycle.
	 * 
	 * @return true if connection to database succeeds, otherwise false.
	 */
	public static boolean init() {
		try {
			Class.forName(Config.DB_DRIVER);
		} catch (ClassNotFoundException ex) {
			Logger.error("DATABASE: Driver class not found!");
			Logger.error(ex);
			return false;
		}
		
		DBC_POOL = new DatabaseConnectionPool(Config.DB_POOL_SIZE, 
											 Config.DB_URL + Config.DB_NAME,
											 Config.DB_USER, Config.DB_PASS);
		
		return DBC_POOL.getCapacity() > 0;
	}

	/**
	 * Close database connections.
	 * This method clear the connection-pool.
	 */
	public static void close() throws SQLException {
		DBC_POOL.close();
	}
	
	/**
	 * Attempts a given QueryTask for a pre-defined number of times.
	 */
	private static void attempt(QueryTask queryTask) throws SQLException {
		try {
			SQLException sqlException = null;
			Connection dbcon = DBC_POOL.take();
			try {
				int attempts = 0;
				do {
					sqlException = null;
					++attempts;
					try {
						queryTask.exec(dbcon);
					} catch (SQLException ex) {
						sqlException = ex;
						Logger.warn(ex.toString() + " while attempting " + queryTask.identify());
						dbcon = repairConnection(dbcon);
					}
				} while (sqlException != null && attempts < 3);
				if (sqlException != null)
					throw sqlException;
			} finally {
				DBC_POOL.put(dbcon);
			}
		} catch(InterruptedException ex) {
			// NOP
		}		
	}
	
	/**
	 * Select all scan records based on the given SHA256 checksum. 
	 * 
	 * This is done using the following parameterized query:
	 * 
	 * <code>SELECT * FROM scans WHERE checksum = ?</code>
	 * 
	 * If no record with such checksum exists, then an empty list is returned.
	 */
	public static ArrayList<Analyse> searchFile(String checksum) throws SQLException {
		SearchFileQuery query = new SearchFileQuery(checksum);
		attempt(query);
		return query.getResult();
	}
	
	
	/**
	 * Select all scan reports based on the given SCAN_ID.
	 * 
	 * This is done using the following parameterized query:
	 * 
	 * <code>SELECT * FROM reports WHERE scan_id = ?</code>
	 * 
	 * If no record with such SCAN_ID exists, then an empty list is returned.
	 */
	public static ArrayList<Report> getReports(Analyse analyse) throws SQLException {
		GetReportsQuery query = new GetReportsQuery(analyse);
		attempt(query);
		return query.getResult();
	}
	
	
	/**
	 * Select a detection record based on the given DET_ID.
	 * 
	 * This is done using the following parameterized query:
	 * 
	 * <code>SELECT * FROM detection WHERE det_id = ?</code>
	 * 
	 * If no record with such DET_ID exists, then null is returned.
	 */
	public static Detection getDetection(byte detID) throws SQLException {
		GetDetectionQuery query = new GetDetectionQuery(detID);
		attempt(query);
		return query.getResult();
	}

	/**
	 * Insert the given scan record into the database.
	 * 
	 * This is done using the following parameterized query:
	 * 
	 * <code>INSERT INTO scans (scan_time, ip, country, file_name, file_size, checksum) VALUES (?, ?, ?, ?, ?, ?)</code>
	 * 
	 * @return the auto-generated SCAN_ID value if insert query succeeds, otherwise -1;
	 */
	public static long insertAnalyse(Analyse scan) throws SQLException {
		InsertAnalyseQuery query = new InsertAnalyseQuery(scan);
		attempt(query);
		return query.getResult();
	}
	
	/**
	 * Insert the given report record into the database.
	 * 
	 * This is done using the following parameterized query:
	 * 
	 * <code>INSERT INTO reports (scan_id, av_id, det_id, description) VALUES (?, ?, ?, ?)</code>
	 * 
	 * @return true if insert query succeeds, otherwise false.
	 */
	public static boolean insertReport(Report report) throws SQLException {
		InsertReportQuery query = new InsertReportQuery(report);
		attempt(query);
		return query.getResult();
	}
	
	/**
	 * It the given connection is valid, it will be returned.
	 * Otherwise, an attempt is made to establish a new 
	 * connection and return it instead of the broken connection.
	 */
	private static Connection repairConnection(Connection dbcon) {
		try {
			if (!dbcon.isValid(1)) {
				Logger.warn("DB connection invalidated!  Attempting connection repair ...");
				dbcon = DriverManager.getConnection(Config.DB_URL + Config.DB_NAME, Config.DB_USER, Config.DB_PASS);
				Logger.info("DB connection repair successfull.");
			}
		} catch (SQLException ex) {
			Logger.error("Database connection repair failed! " + ex.toString());
		}
		return dbcon;
	}
}
