/*** In The Name of Allah ***/
package avserver.model.querytasks;

import avserver.model.QueryTask;
import avserver.model.Analyse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * QueryTask of inserting a SCAN record.
 */
public class InsertAnalyseQuery implements QueryTask<Long> {
	
 	private long result;
	private final Analyse analyse;
	
	public InsertAnalyseQuery(Analyse scan) {
		this.analyse = scan;
		result = -1L;
	}

	@Override
	public void exec(Connection DB) throws SQLException {
		String sql = "INSERT INTO Analysis (Checksum, Time, IP, Country, File_name, File_size) "
				   + "VALUES (?, ?, ?, ?, ?, ?)";
		try(PreparedStatement pst = DB.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			pst.setString(1, analyse.CHECKSUM);
			pst.setTimestamp(2, analyse.TIME);
			pst.setString(3, analyse.IP);
			pst.setString(4, analyse.COUNTRY);
			pst.setString(5, analyse.FILE_NAME);
			pst.setInt(6, analyse.FILE_SIZE);
			if (pst.executeUpdate() == 1) {
				ResultSet rs = pst.getGeneratedKeys();
				if (rs.next())
					result = rs.getLong(1);
			}
		}
	}

	@Override
	public Long getResult() {
		return result;
	}

	@Override
	public String identify() {
		return "Database.insertScan(scan)";
	}
	
}
