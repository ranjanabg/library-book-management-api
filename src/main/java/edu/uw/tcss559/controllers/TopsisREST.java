package edu.uw.tcss559.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import edu.uw.tcss559.common.Constants;
import edu.uw.tcss559.common.Transformer;
import edu.uw.tcss559.mcda.CriticCore;
import edu.uw.tcss559.mcda.TopsisCore;
import edu.uw.tcss559.store.BooksMySQLStore;
import edu.uw.tcss559.structures.Book;
import edu.uw.tcss559.structures.mcda.Alternative;
import edu.uw.tcss559.structures.mcda.Attribute;
import edu.uw.tcss559.structures.mcda.DecisionInput;
import edu.uw.tcss559.structures.mcda.TopsisScores;

@Path("/topsis")
public class TopsisREST extends AbstractREST {
	
	private BooksMySQLStore booksStore;
	
	public TopsisREST() {
		this.booksStore = new BooksMySQLStore();
	}
	
	/**
	 * Gets the TOPSIS performance scores for all the given books
	 * @return
	 */
	@Path("")
	@GET
	@Produces("application/json")
	public Response topsisScoresHandler(
			@Context final HttpHeaders headers) {
    	return topsisScores(headers, null);
	}

	/**
	 * Gets the TOPSIS performance score of given book Id
	 * @param bookId
	 * @param headers
	 * @return
	 */
	@Path("/{book_id}")
	@GET
	@Produces("application/json")
	public Response bookTopsisScoreHandler(
			@PathParam("book_id") final String bookId,
			@Context final HttpHeaders headers) {
    	return topsisScores(headers, bookId);
	}
	
	private Response topsisScores(final HttpHeaders headers,
			final String bookId) {

    	List<Book> books = null;
		
		if (headers.getRequestHeader("book_ids") != null) {
			
			final List<String> bookIds = Arrays.asList(
	    			headers.getRequestHeader("book_ids").get(0).split(","));
	    	
	    	books = bookIds.stream()
	    			.map(this.booksStore::readBook)
	    			.collect(Collectors.toList());
		}
		
		else {
			books = this.booksStore.listBooks();
		}
    	

		final List<Alternative> alternatives = Transformer
				.buildAlternatives(books);
		
		final List<Attribute> attributes = Transformer
				.updateAttributeWeights(
						Constants.getAttributes(), 
						CriticCore.computeWeights(alternatives));
		
		final DecisionInput input = new DecisionInput(attributes, alternatives);
		
		final TopsisScores topsisScores = TopsisCore.compute(input);
		
		if (bookId != null) {
			topsisScores.getAlternativeResults().removeIf(alternative ->
				!alternative.getBook().getTitle().equalsIgnoreCase(bookId));
		}
		
		return buildJsonResponse(topsisScores);
	}

}
