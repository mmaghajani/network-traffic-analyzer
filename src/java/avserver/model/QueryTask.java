/*** In The Name of Allah ***/
package avserver.model;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface of a database query execution task.
 */
public interface QueryTask<T> {
	
	public abstract void exec(Connection DB) throws SQLException;
	
	public abstract T getResult();
	
	public abstract String identify();
	
}
