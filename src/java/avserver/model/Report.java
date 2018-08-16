/*** In The Name of Allah ***/
package avserver.model;

/**
 * POJO object-relation mapping for records of the 'reports' table.
 */
public class Report {
	
	public final Analyse ANALYSE;
	public final Detection DETECTION;
	public final int DATA_INDEX;
	public final double SCORE1;
	public final double SCORE2;
	public final double SCORE3;
	public final double SCORE4;
        

	public Report(Analyse scan, Detection detection, int data_index,
                double score1, double score2, double score3, double score4) {
		this.ANALYSE = scan;
		this.DETECTION = detection;
		this.DATA_INDEX = data_index;
		this.SCORE1 = score1;
		this.SCORE2 = score2;
		this.SCORE3 = score3;
		this.SCORE4 = score4;
	}
	
}
