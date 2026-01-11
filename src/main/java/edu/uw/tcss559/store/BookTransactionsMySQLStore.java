package edu.uw.tcss559.store;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss559.structures.Book;
import edu.uw.tcss559.structures.CheckoutVerify;

import static edu.uw.tcss559.common.Constants.MYSQL_CONNECTOR;

public class BookTransactionsMySQLStore extends AbstractMySQLStore {

	public static final String MYSQL_TABLE_NAME = "BOOK_TRANSACTIONS";

	public BookTransactionsMySQLStore() {
		super(MYSQL_TABLE_NAME);

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check if the requested book is already issued to a user
	 * 
	 * @return
	 */
	public CheckoutVerify checkAlreadyIssued(int bookID, int memberID) {
		try (final Connection connection = DriverManager.getConnection(MYSQL_CONNECTOR)) {

			final Statement sqlStatement = connection.createStatement();
			String sql = "select count(*) as count_issue, memberId from book_transactions where bookId = '" + bookID
					+ "' and type = 'CHECKOUT' and returnDate is null";

			final ResultSet resultSet = sqlStatement.executeQuery(sql);

			while (resultSet.next()) {
				final CheckoutVerify alreadyIssued = CheckoutVerify.builder()
						.countIssued(resultSet.getInt("count_issue")).memberID(resultSet.getInt("memberId")).build();
				return alreadyIssued;
			}
			return null;
		} catch (final SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Check various constraints like dues should be paid, no of copies already
	 * checked out etc..
	 * 
	 * @return
	 */
	public CheckoutVerify verifyCheckoutConstraints(int bookID, String memberRftagID) {

		try (final Connection connection = DriverManager.getConnection(MYSQL_CONNECTOR)) {

			final Statement sqlStatement = connection.createStatement();
			String sql = "select coalesce(o.amountDue,0) as amount_due, coalesce(m.booksIssued,0) as books_issued , coalesce(m.booksLimit ,7) as books_limit, m.type, m.uniquePin , m.id"
					+ "  from members m" + "  left join overdue_fees o" + "  on m.id = o.memberId and o.bookId = '"
					+ bookID + "' and o.amountPaid is null" + "  where m.rfid  = '" + memberRftagID + "'";

			System.out.println(sql);
			final ResultSet resultSet = sqlStatement.executeQuery(sql);
			while (resultSet.next()) {
				final CheckoutVerify checkoutConstraints = CheckoutVerify.builder()
						.amountDue(resultSet.getInt("amount_due")).booksIssued(resultSet.getInt("books_issued"))
						.booksLimit(resultSet.getInt("books_limit")).memberType(resultSet.getString("type"))
						.memberPin(resultSet.getInt("uniquePin")).memberID(resultSet.getInt("id")).build();

				return checkoutConstraints;
			}

			return null;
		} catch (final SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Map the book against the user
	 * 
	 * @return
	 */
	public boolean completeCheckoutProcess(int bookId, String bookRfID, int memberId, String memberRfID, int pin) {
		// Increment the count of books issued to the user by 1
		executeSqlStatement("UPDATE MEMBERS SET booksIssued  = booksIssued  + 1 WHERE rfid = '" + memberRfID + "'");
		// Map the checked out book against the user
		return (executeSqlStatement(
				"INSERT INTO book_transactions (bookId, memberId, type, issuedDate, dueDate, noOfTimesRenewed )\r\n"
						+ "values('" + bookId + "','" + memberId + "', 'CHECKOUT' , CURDATE(), CURDATE()+7, 0)"));
	}

	/**
	 * Check if return date exceeds the due date while returning the book
	 * 
	 * @return
	 */
	public CheckoutVerify returnExceedsDue(int bookID) {
		try (final Connection connection = DriverManager.getConnection(MYSQL_CONNECTOR)) {

			final Statement sqlStatement = connection.createStatement();
			String sql = "select bs.dueDate , curdate() as currDate, bs.memberId , bs.id as bookStatusId"
					+ " from book_transactions bs " + " inner join books b" + " on bs.bookId = b.id" + " where b.id = '"
					+ bookID + "' and type = 'CHECKOUT' and returnDate is NULL";

			final ResultSet resultSet = sqlStatement.executeQuery(sql);

			while (resultSet.next()) {
				final CheckoutVerify returnDuesCheck = CheckoutVerify.builder().dueDate(resultSet.getDate("dueDate"))
						.currDate(resultSet.getDate("currDate")).memberID(resultSet.getInt("memberId"))
						.bookStatusId(resultSet.getInt("bookStatusId")).build();

				return returnDuesCheck;
			}
			return null;
		} catch (final SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Mark the book as returned, reduce the number of books taken by the user
	 * 
	 * @return
	 */
	public boolean completeReturnProcess(int bookId, int memberId) {
		// Reduce the count of books issued to the user by 1
		executeSqlStatement("UPDATE MEMBERS SET booksIssued  = booksIssued-1 WHERE id = '" + memberId + "'");

		// Map the checked out book against the user
		return (executeSqlStatement(
				"UPDATE book_transactions SET type  = 'RETURN', returnDate = curdate() where bookId  = '" + bookId
						+ "' and type = 'CHECKOUT' and returnDate is NULL"));
	}

	/**
	 * Calculate the applicable dues, if book is returned after the due date
	 * 
	 * @return
	 */
	public boolean calculatePendingDues(int bookStatusId) {
		try (final Connection connection = DriverManager.getConnection(MYSQL_CONNECTOR)) {

			final Statement sqlStatement = connection.createStatement();
			String sql = "select bookId, memberId , (datediff(returnDate, dueDate ) * 2) as dueCalculated from book_transactions where id = '"
					+ bookStatusId + "'";

			final ResultSet resultSet = sqlStatement.executeQuery(sql);
			
			System.out.println(sql);
			
			while (resultSet.next()) {

				int bookID = resultSet.getInt("bookId");
				int memberID = resultSet.getInt("memberId");
				double dueCalculated = resultSet.getInt("dueCalculated");

				if (dueCalculated > 0) {
					String sql_update = "insert into overdue_fees (memberId, bookId, amountDue) " + " values ('"
							+ memberID + "','" + bookID + "','" + dueCalculated + "')";
					System.out.println(sql_update);
					return (executeSqlStatement(sql_update));
				}
			}
			return false;
		} catch (final SQLException ex) {
			ex.printStackTrace();
			return false;
		}

	}

	/**
	 * Check if the book to be renewed is checked out
	 * 
	 * @return
	 */
	public CheckoutVerify checkRenewStatus(int bookId) {
		try (final Connection connection = DriverManager.getConnection(MYSQL_CONNECTOR)) {

			final Statement sqlStatement = connection.createStatement();
			String sql = "select bs.id as bookStatusId, bs.memberId, m.rfid from book_transactions bs"
					+ " inner join members m" + " on bs.memberId = m.id" + " where bs.bookId = '" + bookId
					+ "' and bs.type = 'CHECKOUT' and bs.returnDate is null";

			final ResultSet resultSet = sqlStatement.executeQuery(sql);

			while (resultSet.next()) {
				final CheckoutVerify checkStatus = CheckoutVerify.builder().memberID(resultSet.getInt("memberId"))
						.bookStatusId(resultSet.getInt("bookStatusId")).memberRfID(resultSet.getString("rfid")).build();

				return checkStatus;
			}
			return null;
		} catch (final SQLException ex) {
			ex.printStackTrace();
			return null;
		}

	}

	/**
	 * Mark the book as renewed, increase the number of books taken by the user
	 * 
	 * @return
	 */
	public boolean completeRenewProcess(int bookStatusId, int memberId) {
		// Reduce the count of books issued to the user by 1
		executeSqlStatement("UPDATE MEMBERS SET booksIssued = booksIssued + 1 WHERE id = '" + memberId + "'");

		String sql = "UPDATE book_transactions SET type = 'CHECKOUT', returnDate = null, dueDate = curdate() + INTERVAL 7 DAY, noOfTimesRenewed  = noOfTimesRenewed  + 1 where id = '"
				+ bookStatusId + "'";

		// Map the renewed book against the user
		return (executeSqlStatement(sql));
	}

	public int getBookIdFromRFID(String rfid) {
		try (final Connection connection = DriverManager.getConnection(MYSQL_CONNECTOR)) {
			final Statement sqlStatement = connection.createStatement();
			String fetchSQL = "select id from books where rfid = '" + rfid + "'";
			final ResultSet fetchResultSet = sqlStatement.executeQuery(fetchSQL);
			int id = -1;
			
			while (fetchResultSet.next()) {
				 id = fetchResultSet.getInt("id");
			}
			return id == -1 ? null : id;
		} catch (final SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Get the list of books that the given member has checked out
	 * @param memberId
	 * @return
	 */
	public List<Book> memberCheckedOutBooks(final String memberId) {
		final List<Book> books = new ArrayList<>();

		try (final Connection connection = DriverManager
        		.getConnection(MYSQL_CONNECTOR)) {
    		final Statement sqlStatement = connection.createStatement();
    		final String sqlQuery = "SELECT BOOKS.id, BOOKS.isbn, BOOKS.rfid,"
    				+ " BOOKS.title, BOOKS.description, BOOKS.authorName,"
    				+ " BOOKS.publisherName, BOOKS.owner, BOOKS.price,"
    				+ " BOOKS.pages, BOOKS.readCount, BOOKS.readersRating,"
    				+ " BOOKS.criticsRating, BOOKS.rackId, BOOKS.maxIssueDays,"
    				+ " BOOK_TRANSACTIONS.issuedDate, BOOK_TRANSACTIONS.dueDate,"
    				+ " BOOK_TRANSACTIONS.noOfTimesRenewed FROM BOOKS"
    				+ " INNER JOIN BOOK_TRANSACTIONS ON"
    				+ " BOOKS.id = BOOK_TRANSACTIONS.bookId"
    				+ " WHERE BOOK_TRANSACTIONS.memberId = '" + memberId + "' and BOOK_TRANSACTIONS.type = 'CHECKOUT'";
    		final ResultSet resultSet = sqlStatement.executeQuery(sqlQuery);
            
    		while (resultSet.next()) {
    			books.add(Book.builder()
    	    			.id(resultSet.getInt("id"))
    	    			.isbn(resultSet.getString("isbn"))
    	    			.rfid(resultSet.getString("rfid"))
    	    			.title(resultSet.getString("title"))
    	    			.description(resultSet.getString("description"))
    	    			.authorName(resultSet.getString("authorName"))
    	    			.publisherName(resultSet.getString("publisherName"))
    	    			.owner(resultSet.getString("owner"))
    	    			.price(resultSet.getFloat("price"))
    	    			
    	    			.pages(resultSet.getInt("pages"))
    	    			.readCount(resultSet.getInt("readCount"))
    	    			.readersRating(resultSet.getFloat("readersRating"))
    	    			.criticsRating(resultSet.getFloat("criticsRating"))
    	    			.rackId(resultSet.getString("rackId"))
    	    			.maxIssueDays(resultSet.getInt("maxIssueDays"))
    	    			.issuedDate(resultSet.getDate("issuedDate"))
    	    			.dueDate(resultSet.getDate("dueDate"))
    	    			.noOfTimesRenewed(resultSet.getInt("noOfTimesRenewed"))
    	    			.build());
            }
        } catch (final SQLException ex) {
        	ex.printStackTrace();
        }
		
		return books;
	}
	
}