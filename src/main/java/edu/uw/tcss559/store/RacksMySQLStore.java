package edu.uw.tcss559.store;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss559.structures.Rack;

import static edu.uw.tcss559.common.Constants.*;

public class RacksMySQLStore {
	
	public static final String MYSQL_TABLE_NAME = "BOOKS";
	public static final String MYSQL_CONNECTOR = "jdbc:mysql://" + MYSQL_INSTANCE_IP
									+ ":3306/" + MYSQL_DB_NAME 
									+ "?user=" + MYSQL_USERNAME
									+ "&password=" + MYSQL_PASSWORD;
	
	public RacksMySQLStore() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Finds all the misplaced books in the library
	 * @return
	 */
	public List<Rack> listAllMisplacedBooks() {
		final List<Rack> records = new ArrayList<>();
        
		try (final Connection connection = DriverManager
        		.getConnection(MYSQL_CONNECTOR)) {
    		final Statement sqlStatement = connection.createStatement();
    		String sql = "select bk.id as book_id, bk.rackId as correct_rack_position, bcl.rack_id as current_rack_position"
    				+ " from book_current_location bcl"
    				+ " inner join books bk"
    				+ " on bcl.book_id = bk.id"
    				+ " and bcl.rack_id != bk.rackId";
    		final ResultSet resultSet = sqlStatement.executeQuery(sql);
            while (resultSet.next() ) {
            	final Rack rack = Rack.builder()
            			.bookID(resultSet.getInt("book_id"))
            			.correctRackLocation(resultSet.getString("correct_rack_position"))
            			.currentRackLocation(resultSet.getString("current_rack_position"))
            			.build();
            	
            	records.add(rack);
            }
        } catch (final SQLException e) {
            System.out.println(e);
        }
        
		return records;
	}
	
	/**
	 * Finds misplaced books in a particular rack
	 * @return
	 */
	public List<Rack> listRackMisplacedBooks(String rackID) {
		final List<Rack> records = new ArrayList<>();
        
		try (final Connection connection = DriverManager
        		.getConnection(MYSQL_CONNECTOR)) {
    		final Statement sqlStatement = connection.createStatement();
    		String sql = "select bk.id as book_id, bk.rackId as correct_rack_position, bcl.rack_id as current_rack_position"
    				+ " from book_current_location bcl"
    				+ " inner join books bk"
    				+ " on bcl.book_id = bk.id"
    				+ " and bcl.rack_id != bk.rackId"
    				+ " and bk.rackId = '" + rackID + "'";
    		final ResultSet resultSet = sqlStatement.executeQuery(sql);
            while (resultSet.next() ) {
            	final Rack rack = Rack.builder()
            			.bookID(resultSet.getInt("book_id"))
            			.correctRackLocation(resultSet.getString("correct_rack_position"))
            			.currentRackLocation(resultSet.getString("current_rack_position"))
            			.build();
            	
            	records.add(rack);
            }
        } catch (final SQLException e) {
            System.out.println(e);
        }
        
		return records;
	}
	
	/**
	 * Checks if a given book is placed at the correct rack
	 * @return
	 */
	public List<Rack> findRackMisplacedBook(String rackID, int bookID) {
		final List<Rack> records = new ArrayList<>();
        
		try (final Connection connection = DriverManager
        		.getConnection(MYSQL_CONNECTOR)) {
    		final Statement sqlStatement = connection.createStatement();
    		String sql = "select * from books b where id = '" + bookID + "'";
    		final ResultSet resultSet = sqlStatement.executeQuery(sql);
           
    		while (resultSet.next() ) {
    			
    			if (!rackID.equalsIgnoreCase(resultSet.getString("rackId"))) {
    				final Rack rack = Rack.builder()
                			.bookID(resultSet.getInt("id"))
                			.correctRackLocation(resultSet.getString("rackId"))
                			.build();
                	
                	records.add(rack);
    			}
            }
        } catch (final SQLException e) {
            System.out.println(e);
        }
        
		return records;
	}
	

}
