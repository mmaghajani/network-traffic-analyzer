/*** In The Name of Allah ***/
package avserver.model.querytasks;

import avserver.model.QueryTask;
import avserver.model.Report;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * QueryTask of inserting a REPORT record.
 */
public class InsertReportQuery implements QueryTask<Boolean> {
	
	private boolean result;
	private final Report report;
	
	public InsertReportQuery(Report report) {
		this.report = report;
		result = false;
	}

	@Override
	public void exec(Connection DB) throws SQLException {
		String sql = "INSERT INTO Reports (analyse_id, data_index, det_id,"
                        + " score1, score2, score3, score4) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try(PreparedStatement pst = DB.prepareStatement(sql)) {
			pst.setLong(1, report.ANALYSE.ID);
			pst.setLong(2, report.DATA_INDEX);
			pst.setLong(3, report.DETECTION.ID);
			pst.setDouble(4, report.SCORE1);
			pst.setDouble(5, report.SCORE2);
			pst.setDouble(6, report.SCORE3);
			pst.setDouble(7, report.SCORE4);
			result = pst.executeUpdate() == 1;
		}
	}

	@Override
	public Boolean getResult() {
		return result;
	}

	@Override
	public String identify() {
		return "Database.insertReport(report)";
	}
	
}
