package edu.uw.tcss559.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.stream.Collectors;

import edu.uw.tcss559.common.Serializer;
import edu.uw.tcss559.structures.Member;

import static edu.uw.tcss559.controllers.MembersREST.DATE_FORMAT;

public class MembersClient extends AbstractClient {

	private MembersClient() {}
	
	/**
	 * Create a new member using Members REST Service
	 * @param member
	 * @return
	 * @throws Exception
	 */
	public static Member addMember(final Member member) throws Exception {
    	final HttpURLConnection httpRequestCon = buildPostRequest("member/add");
    	httpRequestCon.setRequestProperty("Content-Length",
    			Integer.toString(member.toString().getBytes().length));
    	
    	httpRequestCon.setRequestProperty("rfid", member.getRfid());
    	httpRequestCon.setRequestProperty("firstName", member.getFirstName());
    	httpRequestCon.setRequestProperty("lastName", member.getLastName());
    	httpRequestCon.setRequestProperty("emailId", member.getEmailId());
    	httpRequestCon.setRequestProperty("password", member.getPassword());
    	httpRequestCon.setRequestProperty("mobileNo", member.getMobileNo());
    	httpRequestCon.setRequestProperty("dateOfBirth", DATE_FORMAT.format(member.getDateOfBirth()));
    	httpRequestCon.setRequestProperty("type", member.getType().toString());
    	httpRequestCon.setRequestProperty("status", member.getStatus().toString());
    	httpRequestCon.setRequestProperty("uniquePin", String.valueOf(member.getUniquePin()));
    	
    	httpRequestCon.getOutputStream();
    	httpRequestCon.getResponseCode();
    	
        return Serializer.buildMember(
                new BufferedReader(new InputStreamReader((httpRequestCon.getInputStream())))
                        .lines().collect(Collectors.joining()));
	}
	
	/**
	 * Gets the specific member details from Members REST Service
	 * @param memberId
	 * @return
	 * @throws Exception
	 */
	public static Member getMember(final String memberId) throws Exception {
        final HttpURLConnection httpRequestCon = buildGetRequest("member/read/" + memberId);
        if (httpRequestCon.getResponseCode() != 200) {
                throw new Exception("HTTP Error code is: " + httpRequestCon.getResponseCode());
        }
        
        return Serializer.buildMember(
                        new BufferedReader(new InputStreamReader((httpRequestCon.getInputStream())))
                                .lines().collect(Collectors.joining()));
	}

	/**
	 * Updates the specific details of the given member using Members REST Service
	 * @param memberId
	 * @param updateMap
	 * @throws Exception
	 */
	public static void updateMember(final String memberId,
			final Map<String, String> updateMap) throws Exception {
    	final HttpURLConnection httpRequestCon = buildPutRequest("member/update/" + memberId);
    	httpRequestCon.setRequestProperty("Content-Length",
    			Integer.toString(updateMap.toString().getBytes().length));
    	updateMap.forEach((k, v) -> httpRequestCon.setRequestProperty(k, v));
    	httpRequestCon.getOutputStream();
    	httpRequestCon.getResponseCode();
	}
	
}
