package edu.uw.tcss559.store;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static edu.uw.tcss559.common.Constants.MYSQL_CONNECTOR;

public abstract class AbstractMySQLStore {

	private String mySqlTableName;
	
	public AbstractMySQLStore(final String mySqlTableName) {
		this.mySqlTableName = mySqlTableName;
	}
	
	/**
	 * Deletes all the records in the database table
	 * @return
	 */
	public boolean deleteAll() {
		return executeSqlStatement("DELETE FROM " + this.mySqlTableName);
	}
	
	/**
	 * Executes the insert query and returns the generated Id
	 * @param insertQuery
	 * @return
	 */
	protected int insertRecord(final String insertQuery) {
		try (final Connection connection = DriverManager
        		.getConnection(MYSQL_CONNECTOR)) {
    		final Statement sqlStatement = connection.createStatement();
    		sqlStatement.executeUpdate(insertQuery, Statement.RETURN_GENERATED_KEYS);

    	    final ResultSet keys = sqlStatement.getGeneratedKeys();
    	    while (keys.next()) {
    	    	return keys.getInt(1);
    	    }

    	    return 0;
        } catch (final SQLException ex) {
        	ex.printStackTrace();
            return 0;
        }
	}
	
	/**
	 * Executes the given SQL statement
	 * @param sql
	 * @return
	 */
	protected boolean executeSqlStatement(final String sql) {
        try (final Connection connection = DriverManager
        		.getConnection(MYSQL_CONNECTOR)) {
    		final Statement sqlStatement = connection.createStatement();
    		sqlStatement.executeUpdate(sql);
    		
    		return true;
        } catch (final SQLException ex) {
        	ex.printStackTrace();
            return false;
        }		
	}
	
}
