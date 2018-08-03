/*** In The Name of Allah ***/
package avserver.model;

import avserver.utils.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * An implementation of a simple database connection pool (DBCP).
 */
public class DatabaseConnectionPool {
	
	private final int CAPACITY;
	private final PingThread pingThread;
	private final String URL, USER, PSWD;
	private ArrayBlockingQueue<Connection> connectionQueue = null;
	
	public DatabaseConnectionPool(int capacity, String url, String user, String pswd) {
		if (capacity < 1)
			capacity = 8;
		this.CAPACITY = capacity;
		this.URL = url;
		this.USER = user;
		this.PSWD = pswd;
		// populate the pool
		connectionQueue = new ArrayBlockingQueue<>(capacity);
		for (int i = 0; i < capacity; ++i) {
			try {
				connectionQueue.add(DriverManager.getConnection(url, user, pswd));
			} catch (SQLException ex) {
				Logger.error("DATABASE: Connection to database server failed!");
				Logger.error(ex);
			}
		}
		//
		pingThread = new PingThread();
		new Thread(pingThread).start();
	}
	
	/**
	 * Fetch an idle connection from the pool.
	 * This method will block, if no idle connection is available.
	 */
	public Connection take() throws InterruptedException {
		return connectionQueue.take();
	}
	
	/**
	 * Return a connection to the pool.
	 */
	public void put(Connection conn) {
		try {
			connectionQueue.put(conn);
		} catch (InterruptedException ex) {
			Logger.warn(ex.toString() + " @ DBCP.put(conn)");
		}
	}
	
	/**
	 * Returns the size of this connection pool.
	 */
	public int getCapacity() {
		return CAPACITY;
	}
	
	/**
	 * Closes all connections in this pool and removed all the objects.
	 */
	public void close() throws SQLException {
		pingThread.end();
		if (connectionQueue != null) {
			for (Connection con: connectionQueue) {
				con.close();
			}
			connectionQueue.clear();
		}
	}
	
	private class PingThread implements Runnable {
		
		private boolean finished = false;
		
		
		public void end() {
			finished = true;
		}

		@Override
		public void run() {
			while (!finished) {
				try {
					Thread.sleep(100 * 60 * 1000); // 100 min
				} catch (InterruptedException ex) {
					Logger.warn(ex.toString() + " @ DBCP.PingThread.run()");
				}
				int counter = 0;
				Logger.info("DBCP.PingThread: Iterating through connection pool ...");
				for (Iterator<Connection> it = connectionQueue.iterator(); !finished && it.hasNext(); ) {
					Connection conn = it.next();
					try {
						++counter;
						if (!conn.isValid(1)) {
							Logger.warn("DBCP.PingThread: broken connection found! ...");
							it.remove();
							connectionQueue.add(DriverManager.getConnection(URL, USER, PSWD));
							Logger.info("DBCP.PingThread: repair successful.");
						}
					} catch (SQLException ex) {
						Logger.error("DBCP.PingThread: REPAIR FAILED | " + ex.toString());
					}
				}
				Logger.info("DBCP.PingThread: Iterated through " + counter + " connections.");
			}
		}
		
	}
}