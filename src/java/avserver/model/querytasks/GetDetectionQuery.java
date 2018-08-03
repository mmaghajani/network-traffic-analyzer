/*** In The Name of Allah ***/
package avserver.model.querytasks;

import avserver.model.Detection;
import avserver.model.QueryTask;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * QueryTask of fetching an DETECTION record based on a given DET_ID.
 */
public class GetDetectionQuery implements QueryTask<Detection> {
	
	private Detection result;
	private final byte DET_ID;
	
	public GetDetectionQuery(byte detID) {
		this.DET_ID = detID;
	}

	@Override
	public void exec(Connection DB) throws SQLException {
		Detection det = null;
		String sql = "SELECT * FROM Detection WHERE detection_id = ?";
		try(PreparedStatement pst = DB.prepareStatement(sql)) {
			pst.setByte(1, DET_ID);
			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) 
					det = new Detection(DET_ID, rs.getString("name"));
			}
		}
		result = det;
	}

	@Override
	public Detection getResult() {
		return result;
	}

	@Override
	public String identify() {
		return "Database.getDetection(detID)";
	}
	
}
