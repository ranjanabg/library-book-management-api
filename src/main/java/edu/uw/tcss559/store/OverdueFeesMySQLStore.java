package edu.uw.tcss559.store;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.uw.tcss559.structures.OverdueFees;

import static edu.uw.tcss559.common.Constants.MYSQL_CONNECTOR;

public class OverdueFeesMySQLStore extends AbstractMySQLStore {

	public static final String MYSQL_TABLE_NAME = "OVERDUE_FEES";

	public OverdueFeesMySQLStore() {
		super(MYSQL_TABLE_NAME);
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves the overdue fees details of the given memberId
	 * @param bookId
	 * @return
	 */
	public OverdueFees readOverdueFees(final String memberId) {
		try (final Connection connection = DriverManager
        		.getConnection(MYSQL_CONNECTOR)) {
    		final Statement sqlStatement = connection.createStatement();
    		final ResultSet resultSet = sqlStatement.executeQuery(
    				"SELECT * FROM " + MYSQL_TABLE_NAME + 
    				" WHERE datePaid is null and memberId = '" + memberId + "'");
//    				" WHERE memberId = '" + memberId + "'");
            while (resultSet.next() ) {
            	return buildOverdueFees(resultSet);
            }
            
            return null;
        } catch (final SQLException ex) {
        	ex.printStackTrace();
            return null;
        }
	}
	
	/**
	 * Builds the OverdueFees POJO object from MySql statement result
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	private OverdueFees buildOverdueFees(final ResultSet resultSet) throws SQLException {
		return OverdueFees.builder()
				.memberID(resultSet.getInt("memberId"))
				.bookID(resultSet.getInt("bookId"))
				.amountDue(resultSet.getFloat("amountDue"))
				.amountPaid(resultSet.getBoolean("amountPaid"))
				.datePaid(resultSet.getDate("datePaid"))
				.build();
	}
	
}