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
import edu.uw.tcss559.store.BooksMySQLStore;
import edu.uw.tcss559.structures.Book;
import edu.uw.tcss559.structures.mcda.Alternative;
import edu.uw.tcss559.structures.mcda.AttributesResponse;

@Path("/critic")
public class CriticREST extends AbstractREST {
	
	private BooksMySQLStore booksStore;
	
	public CriticREST() {
		this.booksStore = new BooksMySQLStore();
	}
	
	/**
	 * Gets the weights of all attributes estimated using Critic method. 
	 * @param headers
	 * @return
	 */
	@Path("")
	@GET
	@Produces("application/json")
	public Response criticWeightsHandler(
			@Context final HttpHeaders headers) {
		return criticWeights(headers, null);
	}

	/**
	 * Gets the weight of the specified attribute estimated using Critic method.
	 * @param attributeName
	 * @param headers
	 * @return
	 */
	@Path("/{attribute_name}")
	@GET
	@Produces("application/json")
	public Response attributeCriticWeightsHandler(
			@PathParam("attribute_name") final String attributeName,
			@Context final HttpHeaders headers) {
		return criticWeights(headers, attributeName);
	}
	
	private Response criticWeights(final HttpHeaders headers,
			final String attributeName) {
    	if (headers.getRequestHeader("book_ids") == null) {
    		return Response
    				.status(Status.BAD_REQUEST)
    				.entity("book_ids is not passed in request header")
    				.build();
    	}
    	
    	final List<String> bookIds = Arrays.asList(
    			headers.getRequestHeader("book_ids").get(0).split(","));
    	
    	final List<Book> books = bookIds.stream()
    			.map(this.booksStore::readBook)
    			.collect(Collectors.toList());
    	
		final List<Alternative> alternatives = Transformer
				.buildAlternatives(books);	
		
		final AttributesResponse attributes = Transformer
				.buildAttributesResponse(
						Constants.getAttributes(),
						CriticCore.computeWeights(alternatives));
		
		if (attributeName != null) {
			attributes.getAttributes().removeIf(attribute -> 
				!attribute.getName().equalsIgnoreCase(attributeName));
		}
		
		return buildJsonResponse(attributes);
	}
	
}
