/*** In The Name of Allah ***/
package avserver.model;

/**
 * POJO object-relation mapping for records of the 'reports' table.
 */
public class Report {
	
	public final Analyse ANALYSE;
	public final Detection DETECTION;
	public final int DATA_INDEX;
	public final double SCORE;
        

	public Report(Analyse scan, Detection detection, int data_index, double score) {
		this.ANALYSE = scan;
		this.DETECTION = detection;
		this.DATA_INDEX = data_index;
		this.SCORE = score;
	}
	
}
