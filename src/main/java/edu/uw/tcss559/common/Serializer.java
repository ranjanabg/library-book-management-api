package edu.uw.tcss559.common;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uw.tcss559.structures.Member;

public class Serializer {
	
	/**
	 * Converts java POJO object Quiz Response to JSON string
	 * @param <T>
	 * @param result quiz response instance
	 * @return
	 * @throws JsonProcessingException
	 */
	public static <T> String buildAsString(final T input)
			throws JsonProcessingException {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // provides proper indentation for the output
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // to hide null values
		
		return objectMapper.writeValueAsString(input);
	}
	
    /**
     * Builds Member java POJO object
     * @param input
     * @return
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    public static Member buildMember(final String input)
                    throws JsonMappingException, JsonProcessingException {
            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            return objectMapper.readValue(input, Member.class);
    }

    /**
     * 
     * @param error
     * @return
     */
	public static String getJsonOutput(String error) {
		Map<String, String> responseMap = new HashMap<String, String>();
		try {
			if (error == null) {
				responseMap.put("status", Boolean.TRUE.toString());
			} else {
				responseMap.put("status", Boolean.FALSE.toString());
				responseMap.put("error", error);
			}
			return new ObjectMapper().writeValueAsString(responseMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return "{\"error\": \"Unknown Error\"}";
	}

}
