/*** In The Name of Allah ***/
package avserver.model.querytasks;

import avserver.model.Database;
import avserver.model.Detection;
import avserver.model.QueryTask;
import avserver.model.Report;
import avserver.model.Analyse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * QueryTask of fetching REPORT records based on a given SCAN.
 */
public class GetReportsQuery implements QueryTask<ArrayList<Report>> {
	
	private final Analyse ANALYSE;
	private ArrayList<Report> results;
	
	public GetReportsQuery(Analyse scan) {
		this.ANALYSE = scan;
	}

	@Override
	public void exec(Connection DB) throws SQLException {
		ArrayList<Report> reports = new ArrayList<>();
		String sql = "SELECT * FROM Reports WHERE analyse_id = ?";
		try (PreparedStatement pst = DB.prepareStatement(sql)) {
			pst.setLong(1, ANALYSE.ID);
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					Detection det = Database.getDetection(rs.getByte("det_id"));
					reports.add(new Report(ANALYSE, det, rs.getInt("data_index"),
                                                rs.getDouble("score")));
				}
			}
		}
		results = reports;
	}

	@Override
	public ArrayList<Report> getResult() {
		return results;
	}

	@Override
	public String identify() {
		return "Database.getReports(scan)";
	}
	
}
