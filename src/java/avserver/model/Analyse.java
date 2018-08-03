/*** In The Name of Allah ***/
package avserver.model;

import java.sql.Timestamp;

/**
 * POJO object-relation mapping for records of the 'scans' table.
 */
public class Analyse {
	
	public final long ID;
	public final Timestamp TIME;
	public final String IP;
	public final String COUNTRY;
	public final String FILE_NAME;
	public final int FILE_SIZE;
	public final String CHECKSUM;

	public Analyse(long id, Timestamp time, String ip, 
				String country, String fileName, int fileSize, String checksum) {
		this.ID = id;
		this.TIME = time;
		this.IP = ip;
		this.COUNTRY = country;
		this.FILE_NAME = fileName;
		this.FILE_SIZE = fileSize;
		this.CHECKSUM = checksum;
	}
	
	/**
	 * Returns a new Scan instance with the given ID.
	 * All other fields will remain the same.
	 * Note that this does not change the current object state;
	 * since Scan is an immutable class.
	 */
	public Analyse updateID(long newID) {
		return new Analyse(newID, TIME, IP, COUNTRY, FILE_NAME, FILE_SIZE, CHECKSUM);
	}
}
