package edu.uw.tcss559.client;

import java.net.HttpURLConnection;

public class NotificationClient extends AbstractClient {

    private NotificationClient() {}
    
    public static void postNotification(final String subject,
    		final String message) throws Exception {
    	final HttpURLConnection httpRequestCon = buildPostRequest("notification");
    	httpRequestCon.setRequestProperty("Content-Length",
    			Integer.toString(message.getBytes().length));
    	httpRequestCon.setRequestProperty("subject", subject);
    	httpRequestCon.setRequestProperty("message", message);
    	httpRequestCon.getOutputStream();
    	httpRequestCon.getResponseCode();
    }

    /**
     * Calls the Notification REST API to send message to provided member.
     * @param memberId
     * @param subject
     * @param message
     * @throws Exception
     */
    public static void postNotification(final String memberId,
    		final String subject, final String message) throws Exception {
    	final HttpURLConnection httpRequestCon = buildPostRequest("notification/" + memberId);
    	httpRequestCon.setRequestProperty("Content-Length", 
    			Integer.toString(message.getBytes().length));
    	httpRequestCon.setRequestProperty("subject", subject);
    	httpRequestCon.setRequestProperty("message", message);
    	httpRequestCon.getOutputStream();
    	httpRequestCon.getResponseCode();
    }

}
