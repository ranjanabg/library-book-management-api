package edu.uw.tcss559.controllers;

import static edu.uw.tcss559.common.Constants.RAPID_API_KEY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.uw.tcss559.common.Serializer;
import edu.uw.tcss559.structures.notification.Email;
import edu.uw.tcss559.structures.notification.EmailContent;
import edu.uw.tcss559.structures.notification.EmailDestination;
import edu.uw.tcss559.structures.notification.EmailRequest;
import edu.uw.tcss559.structures.MemberType;
import edu.uw.tcss559.store.MembersMySQLStore;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

/*
 * Sends the Email notifications using SendGrid from RAPID API hub
 * Ref: https://rapidapi.com/sendgrid/api/sendgrid/
 */
@Path("/notification")
public class NotificationREST {

	private static final String RAPID_API_HOST = "rapidprod-sendgrid-v1.p.rapidapi.com";
	private static final String URL_FORMAT = "https://" + RAPID_API_HOST + "/mail/send";
	private static final String SOURCE_EMAIL_ADDRESS = "ranjana@uw.edu";

	private final List<String> mandatoryFields = Arrays.asList(
			"subject", "message");
	
	public MembersMySQLStore membersStore;
	
	public NotificationREST() {
		this.membersStore = new MembersMySQLStore();
	}
	
	/**
	 * Sends the the notification to all the administrators
	 * @return
	 */
    @Path("")
    @POST
    @Produces("application/json")
    public Response postNotificationAllHandler(
    		@Context final HttpHeaders headers) {
    	return notify(headers,
    			this.membersStore.listRecords().stream()
        			.filter(member -> member.getType().equals(MemberType.ADMIN))
        			.map(member -> member.getEmailId())
        			.collect(Collectors.toList())
        			);
    }

    /**
     * Sends the the notification to given member Id
     * @param memberId
     * @return
     */
    @Path("/{member_id}")
    @POST
    @Produces("application/json")
    public Response postNotificationHandler(
    		@PathParam("member_id") final String memberId,
    		@Context final HttpHeaders headers) {
    	return notify(headers, Arrays.asList(
    			this.membersStore.readRecord(memberId).getEmailId()));
    }
    
    /**
     * Extracts the subject and message from the header and 
     * Sends the notification to given list of emailIds
     * @param headers
     * @param emailId
     * @return
     */
    private Response notify(final HttpHeaders headers,
    						final List<String> emailIds) {
    	try {
    		for (final String field: mandatoryFields) {
    			if (headers.getRequestHeader(field) == null) {
    		   		return Response
    		   				.status(Status.BAD_REQUEST)
    		   				.entity("'" + field + "' is not passed in request header")
    		    			.build();
    			}	
    		}

    		final String subject = headers.getRequestHeader("subject").get(0);
    		final String message = headers.getRequestHeader("message").get(0);
        	
        	final HttpResponse<String> response = sendEmail(subject, message, emailIds);;
        	
    		return Response.ok().entity(response.getBody()).build();		
    	} catch (final Exception ex) {
    		ex.printStackTrace();
			return Response.serverError().entity(ex).build();
    	}
    }
    
    /**
     * 
     * @param subject
     * @param message
     * @param emailIds
     * @return
     */
    private HttpResponse<String> sendEmail(final String subject,
    		final String message, final List<String> emailIds)
    				throws JsonProcessingException {
		final List<Email> to = new ArrayList<>();
		emailIds.forEach(emailId -> to.add(Email.builder().email(emailId).build()));
		
		final List<EmailDestination> destinations = new ArrayList<>();
		destinations.add(EmailDestination.builder()
				.to(to)
				.subject(subject)
				.build());
		
		final Email from = Email.builder().email(SOURCE_EMAIL_ADDRESS).build();
		
		final List<EmailContent> content = new ArrayList<>();
		content.add(EmailContent.builder().type("text/plain").value(message).build());
		
		final HttpResponse<String> response = Unirest.post(URL_FORMAT)
				.header("content-type", "application/json")
				.header("x-rapidapi-host", RAPID_API_HOST)
				.header("x-rapidapi-key", RAPID_API_KEY)
				.body(Serializer.buildAsString(
						EmailRequest.builder()
							.personalizations(destinations)
							.from(from)
							.content(content)
							.build()))
				.asString();
		return response;	
    }
    
    /**
     * Send Notification via email with given subject, message and emailId 
     * @param subject
     * @param message
     * @param emailId
     * @return
     * @throws JsonProcessingException
     */
    public HttpResponse<String> sendEmail(final String subject,
    		final String message, final String emailId)
    				throws JsonProcessingException {
    	return sendEmail(subject, message, Arrays.asList(emailId));
	}
    
}
