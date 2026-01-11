package edu.uw.tcss559.controllers;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import edu.uw.tcss559.common.Serializer;
import edu.uw.tcss559.store.MembersMySQLStore;
import edu.uw.tcss559.structures.Member;
import edu.uw.tcss559.structures.MemberStatus;
import edu.uw.tcss559.structures.MemberType;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/member")
public class MembersREST extends AbstractREST {
	
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private final List<String> mandatoryFields = Arrays.asList("rfid",
			"firstName", "lastName", "emailId", "password", "mobileNo",
			"dateOfBirth", "uniquePin");
	
	private final List<String> mutableFields = Arrays.asList("rfid",
			"firstName", "lastName", "emailId", "password", "mobileNo",
			"dateOfBirth", "type", "status", "uniquePin", "booksIssued",
			"booksLimit");
	
	private final MembersMySQLStore membersStore;
	
	public MembersREST() {
		this.membersStore = new MembersMySQLStore();
	}

    /**
     * Creates a record with given member details
     * @param headers
     * @return
     */
	@Path("/add")
	@POST
	@Produces("application/json")
	public Response addRecordHandler(@Context final HttpHeaders headers) {
		try {
			for (final String field: mandatoryFields) {
		    	if (headers.getRequestHeader(field) == null) {
		    		return Response
		    				.status(Status.BAD_REQUEST)
		    				.entity("Member '" + field + "' is not passed in the request header")
		    				.build();
		    	}	
			}
			
			final Integer booksIssued = 
					headers.getRequestHeader("booksIssued") == null ? null 
							: Integer.parseInt(headers.getRequestHeader("booksIssued").get(0));
			
			final Integer booksLimit = 
					headers.getRequestHeader("booksLimit") == null ? null 
							: Integer.parseInt(headers.getRequestHeader("booksLimit").get(0));
			
			final Member member = Member.builder()
					.rfid(headers.getRequestHeader("rfid").get(0))
					.firstName(headers.getRequestHeader("firstName").get(0))
					.lastName(headers.getRequestHeader("lastName").get(0))
					.emailId(headers.getRequestHeader("emailId").get(0))
					.password(headers.getRequestHeader("password").get(0))
					.mobileNo(headers.getRequestHeader("mobileNo").get(0))
					.dateOfBirth(DATE_FORMAT.parse(headers.getRequestHeader("dateOfBirth").get(0)))
					.type(MemberType.READER)
					.status(MemberStatus.ACTIVE)
					.uniquePin(Integer.parseInt(headers.getRequestHeader("uniquePin").get(0)))
					.booksIssued(booksIssued)
					.booksLimit(booksLimit)
					.build();

			return this.membersStore
					.createRecord(member) ?
					Response.ok()
						.entity(Serializer.buildAsString(member))
						.build() :
					Response.serverError().build();
		} catch (final Exception ex) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
	}
	
	/**
	 * Gets the details of all the members
	 * @return
	 */
	@Path("/read")
	@GET
	@Produces("application/json")
	public Response readRecordHandler() {
		return buildJsonResponse(this.membersStore.listRecords());
	}
	
	/**
	 * Gets the details of the specific member based on given memberId
	 * @return
	 */
	@Path("/read/{member_id}")
	@GET
	@Produces("application/json")
	public Response readRecordHandler(
			@PathParam("member_id") final String memberId) {
		return buildJsonResponse(this.membersStore.readRecord(memberId));
	}
	
	/**
	 * Updates the details of given member Id
	 * @param bookId
	 * @param headers
	 * @return
	 */
	@Path("/update/{member_id}")
	@PUT
	@Produces("application/json")
	public Response updateRecordHandler(
			@PathParam("member_id") final String memberId,
			@Context final HttpHeaders headers) {
		final Map<String, String> memberInfoMap = new HashMap<>();
		
		for (final String field: mutableFields) {
			if (headers.getRequestHeader(field) != null) {
				memberInfoMap.put(field,
						headers.getRequestHeader(field).get(0));
			}
		}
		
		return this.membersStore.updateRecord(memberId, memberInfoMap) 
				? Response.ok().build() : Response.serverError().build();
	}	
	
	/**
	 * Deletes the member details of given member Id.
	 * @param bookId
	 * @return
	 */
	@Path("/delete/{member_id}")
	@DELETE
	@Produces("application/json")
	public Response deleteRecordHandler(
			@PathParam("member_id") final String memberId) {
		this.membersStore.deleteRecord(memberId);
	    return Response.ok().build();
	}
	
	/**
	 * Deletes all members details
	 * @return
	 */
	@Path("/delete")
	@DELETE
	@Produces("application/json")
	public Response deleteAllHandler() {
		this.membersStore.deleteAll();
	    return Response.ok().build();
	}

}