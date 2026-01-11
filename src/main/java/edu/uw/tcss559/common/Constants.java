package edu.uw.tcss559.common;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss559.structures.mcda.Attribute;

public class Constants {
	
	private Constants() {}
	
	public static final String ROOT_SERVICE_URL = "http://localhost:8080/api";

	/**
	 * MySQL database Credentials
	 */
	public static final String MYSQL_INSTANCE_IP = "localhost";
	public static final String MYSQL_INSTANCE_PORT = "3306";
	public static final String MYSQL_DB_NAME = "library_management";
	public static final String MYSQL_USERNAME = "root";
	public static final String MYSQL_PASSWORD = "root";
	public static final String MYSQL_CONNECTOR = String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s",
			MYSQL_INSTANCE_IP, MYSQL_INSTANCE_PORT, MYSQL_DB_NAME, MYSQL_USERNAME, MYSQL_PASSWORD);
			
	/**
	 * RAPID API Credentials
	 */
	public static final String RAPID_API_KEY = "916208bd7bmshee4a9d7b3dcd7d1p11f3aejsna332a8cd221b";

	/**
	 * Gets the list of attributes that are to be used
	 * in calculating the MCDA performance scores
	 * @return
	 */
    public static List<Attribute> getAttributes() {
        final List<Attribute> attributes = new ArrayList<>();

        attributes.add(new Attribute("price", null));
        attributes.add(new Attribute("pages", null));
        attributes.add(new Attribute("readCount", null));
        attributes.add(new Attribute("readersRating", null));
        attributes.add(new Attribute("criticsRating", null));

        return attributes;
    }
    
}
