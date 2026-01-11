package edu.uw.tcss559.controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import edu.uw.tcss559.store.BooksMySQLStore;
import edu.uw.tcss559.structures.Book;

@Path("/book")
public class BooksREST extends AbstractREST {
	
	private final List<String> mandatoryFields = Arrays.asList("isbn", "rfid",
			"title", "description", "authorName", "publisherName", "owner",
			"price", "pages", "readCount", "readersRating", "criticsRating",
			"rackId");	
	
	private final List<String> mutableFields = Arrays.asList("isbn", "rfid",
			"title", "description", "authorName", "publisherName", "owner",
			"price", "pages", "readCount", "readersRating", "criticsRating",
			"rackId", "maxIssueDays");
	
	private BooksMySQLStore booksStore;
	
	public BooksREST() {
		this.booksStore = new BooksMySQLStore();
	}

    /**
     * Creates a book record with given details
     * @param headers
     * @return
     */
	@Path("/add")
	@POST
	@Produces("application/json")
	public Response addHandler(@Context final HttpHeaders headers) {
		for (final String field: mandatoryFields) {
	    	if (headers.getRequestHeader(field) == null) {
	    		return Response
	    				.status(Status.BAD_REQUEST)
	    				.entity("Book '" + field + "' is not passed in the request header")
	    				.build();
	    	}	
		}
		
		final Integer maxIssueDays = 
				headers.getRequestHeader("maxIssueDays") == null ? null 
						: Integer.parseInt(headers.getRequestHeader("maxIssueDays").get(0));
		
		return this.booksStore
				.createBook(Book.builder()
            			.isbn(headers.getRequestHeader("isbn").get(0))
            			.rfid(headers.getRequestHeader("rfid").get(0))
            			.title(headers.getRequestHeader("title").get(0))
            			.description(headers.getRequestHeader("description").get(0))
            			.authorName(headers.getRequestHeader("authorName").get(0))
            			.publisherName(headers.getRequestHeader("publisherName").get(0))
            			.owner(headers.getRequestHeader("owner").get(0))
            			.price(Float.parseFloat(headers.getRequestHeader("price").get(0)))
            			.pages(Integer.parseInt(headers.getRequestHeader("pages").get(0)))
            			.readCount(Integer.parseInt(headers.getRequestHeader("readCount").get(0)))
            			.readersRating(Float.parseFloat(headers.getRequestHeader("readersRating").get(0)))
            			.criticsRating(Float.parseFloat(headers.getRequestHeader("criticsRating").get(0)))
            			.rackId(headers.getRequestHeader("rackId").get(0))
            			.maxIssueDays(maxIssueDays)
            			.build()) ?
				Response.ok().build() : Response.serverError().build();
	}
	
	/**
	 * Gets the details of all the books
	 * @return
	 */
	@Path("/read")
	@GET
	@Produces("application/json")
	public Response readAllHandler() {
		return buildJsonResponse(this.booksStore.listBooks());
	}
	
	/**
	 * Gets the details of the specific book based on given bookId
	 * @return
	 */
	@Path("/read/{book_id}")
	@GET
	@Produces("application/json")
	public Response readHandler(@PathParam("book_id") final String bookId) {
		return buildJsonResponse(this.booksStore.readBook(bookId));
	}
	
	/**
	 * Updates the details of given book Id
	 * @param bookId
	 * @param headers
	 * @return
	 */
	@Path("/update/{book_id}")
	@PUT
	@Produces("application/json")
	public Response updateHandler(
			@PathParam("book_id") final String bookId,
			@Context final HttpHeaders headers) {
		final Map<String, String> bookInfoMap = new HashMap<>();
		
		for (final String field: mutableFields) {
			if (headers.getRequestHeader(field) != null) {
				bookInfoMap.put(field,
						headers.getRequestHeader(field).get(0));
			}
		}
		
		return this.booksStore.updateBook(bookId, bookInfoMap) 
				? Response.ok().build() : Response.serverError().build();
	}	
	
	/**
	 * Deletes the book details of given book Id.
	 * @param bookId
	 * @return
	 */
	@Path("/delete/{book_id}")
	@DELETE
	@Produces("application/json")
	public Response deleteHandler(
			@PathParam("book_id") final String bookId) {
		this.booksStore.deleteBook(bookId);
	    return Response.ok().build();
	}
	
	/**
	 * Deletes all books details
	 * @return
	 */
	@Path("/delete")
	@DELETE
	@Produces("application/json")
	public Response deleteAllHandler() {
		this.booksStore.deleteAll();
	    return Response.ok().build();
	}
	
}