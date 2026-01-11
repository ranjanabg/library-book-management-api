package edu.uw.tcss559.store;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.uw.tcss559.structures.Member;
import edu.uw.tcss559.structures.MemberStatus;
import edu.uw.tcss559.structures.MemberType;
import edu.uw.tcss559.structures.MembersStatistics;

import static edu.uw.tcss559.common.Constants.MYSQL_CONNECTOR;

public class MembersMySQLStore extends AbstractMySQLStore {

	public static final String MYSQL_TABLE_NAME = "MEMBERS";
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final Integer DEFAULT_BOOKS_ISSUED = 0;
	public static final Integer DEFAULT_BOOKS_LIMIT = 7;

	public MembersMySQLStore() {
		super(MYSQL_TABLE_NAME);
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (final ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Creates a record in the database table based on the given Member details
	 * @param Member
	 * @return
	 */
	public boolean createRecord(final Member member) {
		addGeneratedKeys(member);

		final String insertQuery = String.format(
				"INSERT INTO %S "
				+ "(`rfid`, `firstName`, `lastName`, `emailId`, `password`,"
				+ " `mobileNo`, `dateOfBirth`, `type`, `status`, `createdOn`,"
				+ " `uniquePin`, `booksIssued`, `booksLimit`) VALUES ('%s',"
				+ " '%s', '%s', '%s', '%s', '%s', '%s',"
				+ " '%s', '%s', '%s', '%s', '%s', '%s');",
				MYSQL_TABLE_NAME,
				member.getRfid(),
				member.getFirstName(),
				member.getLastName(),
				member.getEmailId(),
				member.getPassword(),
				member.getMobileNo(),
				DATE_FORMAT.format(member.getDateOfBirth()),
				member.getType().toString(),
				member.getStatus().toString(),
				DATE_FORMAT.format(member.getCreatedOn()),
				member.getUniquePin(),
				member.getBooksIssued(),
				member.getBooksLimit());

		final int generatedId = insertRecord(insertQuery);
	
		if (generatedId != 0) {
			member.setId(generatedId);
			return true;
		}

		return false;
	}
	
	/**
	 * Retrieves the given customer name information
	 * @return
	 */
	public Member readRecord(final String memberId) {
		try (final Connection connection = DriverManager
        		.getConnection(MYSQL_CONNECTOR)) {
    		final Statement sqlStatement = connection.createStatement();
    		final ResultSet resultSet = sqlStatement.executeQuery(
    				"SELECT * FROM " + MYSQL_TABLE_NAME + 
    				" WHERE id = '" + memberId + "'");
            while (resultSet.next() ) {
            	return buildMember(resultSet);
            }
            
            return null;
        } catch (final SQLException ex) {
        	ex.printStackTrace();
            return null;
        }
	}
	
	/**
	 * Reads all the records from MySQL database
	 * @return
	 */
	public List<Member> listRecords() {
		final List<Member> records = new ArrayList<>();
        
		try (final Connection connection = DriverManager
        		.getConnection(MYSQL_CONNECTOR)) {
    		final Statement sqlStatement = connection.createStatement();
    		final ResultSet resultSet = sqlStatement.executeQuery(
    				"SELECT * FROM " + MYSQL_TABLE_NAME);
            while (resultSet.next() ) {
            	records.add(buildMember(resultSet));
            }
        } catch (final SQLException e) {
            System.out.println(e);
        }
        
		return records;
	}
	
	/**
	 * Updates the records based on given Member Id and 
	 * map of key values that are to be updated
	 * @param MemberId
	 * @return
	 */
	public boolean updateRecord(
			final String memberId,
			final Map<String, String> customerInfoMap) {
		final StringBuilder updateSqlStatement = new StringBuilder();
		
		updateSqlStatement.append("UPDATE " + MYSQL_TABLE_NAME + " SET ");
		customerInfoMap.forEach((key, value) -> 
				updateSqlStatement.append(key + " = '" + value + "', "));
		
		updateSqlStatement.replace(updateSqlStatement.length() - 2,
				updateSqlStatement.length(), " ");
		
		updateSqlStatement.append("WHERE id = '" + memberId + "'");
		
		return executeSqlStatement(updateSqlStatement.toString());
	}
	
	/**
	 * Deletes the record based on given Member Id
	 * @param MemberId
	 * @return
	 */
	public boolean deleteRecord(final String memberId) {
		return executeSqlStatement(String.format(
				"DELETE FROM %s WHERE id = '%s'",
				MYSQL_TABLE_NAME, memberId));
	}
	
	/**
	 * Analyze Members Statistics
	 * @return
	 */
	public MembersStatistics getMembersStatistics() {
		final MembersStatistics membersStatistics = 
				MembersStatistics.builder().build();
		
		try (final Connection connection = DriverManager
        		.getConnection(MYSQL_CONNECTOR)) {
    		final Statement sqlStatement = connection.createStatement();
    		final ResultSet resultSet = sqlStatement.executeQuery(
    				"SELECT type, COUNT(*) as total FROM "
					+ MYSQL_TABLE_NAME + " GROUP BY type");
    		
    		int total = 0;
            while (resultSet.next() ) {
				final int subTotal = resultSet.getInt("total");
				total += subTotal;
            	if (resultSet.getString("type").equals("READER")) {
            		membersStatistics.setTotalNumberOfReaders(subTotal);
            	} else if (resultSet.getString("type").equals("LIBRARIAN")) {
            		
            		membersStatistics.setTotalNumberOfLibrarians(subTotal);
            	}
            }
            membersStatistics.setTotalNumberOfRegisteredMembers(total);
            
    		final ResultSet resultSet1 = sqlStatement.executeQuery(
    				"SELECT status, COUNT(*) as total FROM "
					+ MYSQL_TABLE_NAME + " GROUP BY status");
            while (resultSet1.next() ) {
				final int subTotal = resultSet1.getInt("total");
            	if (resultSet1.getString("status").equals("ACTIVE")) {
            		membersStatistics.setTotalNumberOfActiveMembers(subTotal);
            	} 
            }
            
    		final ResultSet resultSet2 = sqlStatement.executeQuery(
    				"SELECT SUM(booksIssued) as total FROM " + MYSQL_TABLE_NAME);
            while (resultSet2.next() ) {
            	membersStatistics.setTotalNumberOfBooksIssued(resultSet2.getInt("total"));
            }
        } catch (final SQLException ex) {
        	ex.printStackTrace();
        }

		return membersStatistics;	
	}
	
	/**
	 * Builds the book POJO object from MySql statement result
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	private Member buildMember(final ResultSet resultSet) throws SQLException {
    	return Member.builder()
    			.id(resultSet.getInt("id"))
    			.rfid(resultSet.getString("rfid"))
    			.firstName(resultSet.getString("firstName"))
    			.lastName(resultSet.getString("lastName"))
    			.emailId(resultSet.getString("emailId"))
    			.password(resultSet.getString("password"))
    			.mobileNo(resultSet.getString("mobileNo"))
    			.dateOfBirth(resultSet.getDate("dateOfBirth"))
    			.type(MemberType.valueOf(resultSet.getString("type")))
    			.status(MemberStatus.valueOf(resultSet.getString("status")))
    			.createdOn(resultSet.getDate("createdOn"))
    			.uniquePin(resultSet.getInt("uniquePin"))
    			.booksIssued(resultSet.getInt("booksIssued"))
    			.booksLimit(resultSet.getInt("booksLimit"))
    			.build();
	}
	
	/**
	 * Adds generated keys to member POJO object
	 * @param member
	 */
	private void addGeneratedKeys(final Member member) {
		member.setCreatedOn(new Date());

		if (member.getBooksIssued() == null) {
			member.setBooksIssued(DEFAULT_BOOKS_ISSUED);
		}

		if (member.getBooksLimit() == null) {
			member.setBooksLimit(DEFAULT_BOOKS_LIMIT);
		}
	}
	
	public boolean completeOverduePayment(String memberId, int amountPaid){
		String sql = "update overdue_fees set amountPaid = '" + amountPaid + "', datePaid = curdate() where amountDue = '" + amountPaid + "' and datePaid is null and memberId = '" + memberId + "'";
		System.out.println(sql);
		return executeSqlStatement(String.format(sql));
	}
	

}