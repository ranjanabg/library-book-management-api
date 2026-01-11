package edu.uw.tcss559.controllers;

import javax.jws.WebMethod;
import javax.jws.WebService;

import edu.uw.tcss559.store.BooksMySQLStore;
import edu.uw.tcss559.store.MembersMySQLStore;
import edu.uw.tcss559.structures.BooksStatistics;
import edu.uw.tcss559.structures.MembersStatistics;

@WebService(targetNamespace = "http://controllers.tcss559.uw.edu/",
			portName = "AnalyticsSOAPPort",
			serviceName = "AnalyticsSOAPService")
public class AnalyticsSOAP {

	private BooksMySQLStore booksStore;
	private MembersMySQLStore membersStore;
	
	public AnalyticsSOAP() {
		this.booksStore = new BooksMySQLStore();
		this.membersStore = new MembersMySQLStore();
	}
	
	/**
	 * Analyzes the total number of books and total cost of all the books
	 * @return
	 */
	@WebMethod(operationName = "getBooksStatistics",
			   action = "GetBooksStatistics")
	public BooksStatistics getBooksStatistics() {
		return this.booksStore.getBooksStatistics();
	}
	
	/**
	 * Analyzes the total number registered members,
	 * active members, reader members, librarian members
	 * and number of books issued till now.
	 * @return
	 */
	@WebMethod(operationName = "getMembersStatistics",
			   action = "GetMembersStatistics")
	public MembersStatistics getMembersStatistics() {
		return this.membersStore.getMembersStatistics();
	}
	
}
