/*** In The Name of Allah ***/
package avserver.model;

/**
 * POJO object-relation mapping for records of the 'reports' table.
 */
public class Detection {
	
	public static final byte FAILED = (byte) 9;
	
	public final byte ID;
	public final String DEFINITION;

	public Detection(byte id, String definition) {
		this.ID = id;
		this.DEFINITION = definition;
	}
	
}
