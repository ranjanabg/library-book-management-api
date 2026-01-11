package edu.uw.tcss559.controllers;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.uw.tcss559.common.Serializer;

public abstract class AbstractREST {

	/**
	 * Builds the JAX-RS Response with serializing 
	 * the given input to JSON object
	 * 
	 * @param <T> Type of input
	 * @param input
	 * @return
	 */
	protected <T> Response buildJsonResponse(final T input) {
	    try {
			return Response.ok()
					.entity(Serializer.buildAsString(input))
					.build();
		} catch (final JsonProcessingException ex) {
			ex.printStackTrace();
			return Response.serverError().entity(ex).build();
		}	
	}
	
}
