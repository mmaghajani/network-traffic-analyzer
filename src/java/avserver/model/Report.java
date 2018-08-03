/*** In The Name of Allah ***/
package avserver.model;

/**
 * POJO object-relation mapping for records of the 'reports' table.
 */
public class Report {
	
	public final Analyse ANALYSE;
	public final Detection DETECTION;
	public final int DATA_INDEX;

	public Report(Analyse scan, Detection detection, int data_index) {
		this.ANALYSE = scan;
		this.DETECTION = detection;
		this.DATA_INDEX = data_index;
	}
	
}
