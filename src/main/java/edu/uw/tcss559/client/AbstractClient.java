package edu.uw.tcss559.client;

import java.net.HttpURLConnection;
import java.net.URL;

import static edu.uw.tcss559.common.Constants.ROOT_SERVICE_URL;
import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class AbstractClient {
	
    /**
     * Builds the GET HTTP request
     * @param urlPath
     * @return
     * @throws Exception
     */
    protected static HttpURLConnection buildGetRequest(final String urlPath)
                    throws Exception {
            final URL serviceEndpoint = new URL(ROOT_SERVICE_URL + "/" + urlPath);
            final HttpURLConnection httpRequestCon = (HttpURLConnection) serviceEndpoint.openConnection();

            httpRequestCon.setRequestProperty("Accept-Charset", UTF_8.name());
            httpRequestCon.setRequestMethod("GET");
            httpRequestCon.setRequestProperty("Accept", "application/json");

            return httpRequestCon;
    }

    /**
     * Builds the POST HTTP request
     * @param urlPath
     * @return
     * @throws Exception
     */
    protected static HttpURLConnection buildPostRequest(final String urlPath)
    		throws Exception {
    	final URL serviceEndpoint = new URL(ROOT_SERVICE_URL + "/" + urlPath);
        final HttpURLConnection httpRequestCon = (HttpURLConnection) serviceEndpoint.openConnection();

        httpRequestCon.setDoOutput(true);
        httpRequestCon.setDoInput(true);
        httpRequestCon.setRequestMethod("POST");
        httpRequestCon.setRequestProperty("Accept-Charset", UTF_8.name());
        httpRequestCon.setRequestProperty("Accept", "application/json");
        httpRequestCon.setRequestProperty("Content-Type", "text/plain");
        httpRequestCon.setRequestProperty("charset", "utf-8");

        return httpRequestCon;
    }
    
    /**
     * Builds the PUT HTTP request
     * @param urlPath
     * @return
     * @throws Exception
     */
    protected static HttpURLConnection buildPutRequest(final String urlPath)
    		throws Exception {
    	final URL serviceEndpoint = new URL(ROOT_SERVICE_URL + "/" + urlPath);
        final HttpURLConnection httpRequestCon = (HttpURLConnection) serviceEndpoint.openConnection();

        httpRequestCon.setDoOutput(true);
        httpRequestCon.setDoInput(true);
        httpRequestCon.setRequestMethod("PUT");
        httpRequestCon.setRequestProperty("Accept-Charset", UTF_8.name());
        httpRequestCon.setRequestProperty("Accept", "application/json");
        httpRequestCon.setRequestProperty("Content-Type", "text/plain");
        httpRequestCon.setRequestProperty("charset", "utf-8");

        return httpRequestCon;
    }
    
}
