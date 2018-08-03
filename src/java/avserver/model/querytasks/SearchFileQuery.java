/** * In The Name of Allah ** */
package avserver.model.querytasks;

import avserver.model.Database;
import avserver.model.QueryTask;
import avserver.model.Analyse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * QueryTask of fetching SCAN records based on a given CHECKSUM.
 */
public class SearchFileQuery implements QueryTask<ArrayList<Analyse>> {

    private final String CHECKSUM;
    private ArrayList<Analyse> results;

    public SearchFileQuery(String checksum) {
        results = null;
        this.CHECKSUM = checksum;
    }

    @Override
    public void exec(Connection DB) throws SQLException {
        ArrayList<Analyse> scans = new ArrayList<>();
        String sql = "SELECT * FROM Analysis WHERE Checksum = ?"
                + " ORDER BY Time DESC LIMIT 3";
        try (PreparedStatement pst = DB.prepareStatement(sql)) {
            pst.setString(1, CHECKSUM);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    scans.add(new Analyse(
                            rs.getLong("ID"),
                            rs.getTimestamp("Time"),
                            rs.getString("IP"),
                            rs.getString("Country"),
                            rs.getString("File_name"),
                            rs.getInt("File_size"),
                            rs.getString("Checksum")
                    )
                    );
                }
            }
        }
        results = scans;
    }

    @Override
    public ArrayList<Analyse> getResult() {
        return results;
    }

    @Override
    public String identify() {
        return "Database.searchFile(checksum)";
    }

}
