package edu.uw.tcss559.store;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.uw.tcss559.structures.Book;
import edu.uw.tcss559.structures.BooksStatistics;

import static edu.uw.tcss559.common.Constants.MYSQL_CONNECTOR;

public class BooksMySQLStore extends AbstractMySQLStore {

	public static final String MYSQL_TABLE_NAME = "BOOKS";
	public static final Integer DEFAULT_MAX_ISSUE_DAYS = 30;

	public BooksMySQLStore() {
		super(MYSQL_TABLE_NAME);
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a record in the database table based on the given book details
	 * @param book
	 * @return
	 */
	public boolean createBook(final Book book) {
		return executeSqlStatement(String.format(
				"INSERT INTO %S "
				+ "(`isbn`, `rfid`, `title`, `description`,"
				+ " `authorName`, `publisherName`, `owner`,"
				+ " `price`, `pages`, `readCount`, `readersRating`,"
				+ " `criticsRating`, `rackId`, `maxIssueDays`) VALUES ("
				+ " '%s', '%s', '%s', '%s', '%s', '%s', '%s',"
				+ " '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
				MYSQL_TABLE_NAME,
    			book.getIsbn(),
    			book.getRfid(),
    			book.getTitle(),
    			book.getDescription(),
    			book.getAuthorName(),
    			book.getPublisherName(),
    			book.getOwner(),
    			book.getPrice(),
    			book.getPages(),
    			book.getReadCount(),
    			book.getReadersRating(),
    			book.getCriticsRating(),
    			book.getRackId(),
    			book.getMaxIssueDays() == null ?
    					DEFAULT_MAX_ISSUE_DAYS : book.getMaxIssueDays()
    			));	
	}
	
	/**
	 * Retrieves the book details of the given bookId
	 * @param bookId
	 * @return
	 */
	public Book readBook(final String bookId) {
		try (final Connection connection = DriverManager
        		.getConnection(MYSQL_CONNECTOR)) {
    		final Statement sqlStatement = connection.createStatement();
    		final ResultSet resultSet = sqlStatement.executeQuery(
    				"SELECT * FROM " + MYSQL_TABLE_NAME + 
    				" WHERE id = '" + bookId + "'");
            while (resultSet.next() ) {
            	return buildBook(resultSet);
            }
        } catch (final SQLException ex) {
        	ex.printStackTrace();
        }

		return null;
	}
	
	/**
	 * Reads all the records from MySQL database
	 * @return
	 */
	public List<Book> listBooks() {
		final List<Book> records = new ArrayList<>();
        
		try (final Connection connection = DriverManager
        		.getConnection(MYSQL_CONNECTOR)) {
    		final Statement sqlStatement = connection.createStatement();
    		final ResultSet resultSet = sqlStatement.executeQuery(
    				"SELECT * FROM " + MYSQL_TABLE_NAME);
            while (resultSet.next() ) {
            	records.add(buildBook(resultSet));
            }
        } catch (final SQLException e) {
            System.out.println(e);
        }
        
		return records;
	}
	
	/**
	 * Updates the records based on given bookId and 
	 * map of key values that are to be updated
	 * @param bookId
	 * @return
	 */
	public boolean updateBook(
			final String bookId,
			final Map<String, String> bookInfoMap) {
		final StringBuilder updateSqlStatement = new StringBuilder();
		
		updateSqlStatement.append("UPDATE " + MYSQL_TABLE_NAME + " SET ");
		bookInfoMap.forEach((key, value) -> 
				updateSqlStatement.append(key + " = '" + value + "', "));
		
		updateSqlStatement.replace(updateSqlStatement.length() - 2,
				updateSqlStatement.length(), " ");
		
		updateSqlStatement.append("WHERE id = '" + bookId + "'");
		
		return executeSqlStatement(updateSqlStatement.toString());
	}
	
	/**
	 * Deletes the record based on given book Id
	 * @param bookId
	 * @return
	 */
	public boolean deleteBook(final String bookId) {
		return executeSqlStatement(String.format(
				"DELETE FROM %s WHERE id = '%s'",
				MYSQL_TABLE_NAME, bookId));
	}
	
	/**
	 * Analyze Books Statistics
	 * @return
	 */
	public BooksStatistics getBooksStatistics() {
		try (final Connection connection = DriverManager
        		.getConnection(MYSQL_CONNECTOR)) {
    		final Statement sqlStatement = connection.createStatement();
    		final ResultSet resultSet = sqlStatement.executeQuery(
    				"SELECT COUNT(*) as totalNumberOfBooks,"
    				+ " SUM(price) as totalCostOfBooks FROM "
					+ MYSQL_TABLE_NAME);
            while (resultSet.next() ) {
            	return buildBooksStatistics(resultSet);
            }
        } catch (final SQLException ex) {
        	ex.printStackTrace();
        }

		return null;	
	}
	
	/**
	 * Builds the book POJO object from MySQL statement result
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	private Book buildBook(final ResultSet resultSet) throws SQLException {
    	return Book.builder()
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
    			.build();
	}
	
	/**
	 * Builds the BooksStatisics POJO object from MySQL statement result
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	private BooksStatistics buildBooksStatistics(final ResultSet resultSet) throws SQLException {
		return BooksStatistics.builder()
				.totalNumberOfBooks(resultSet.getInt("totalNumberOfBooks"))
				.totalCostOfBooks(resultSet.getInt("totalCostOfBooks"))
				.build();
	}
	
}