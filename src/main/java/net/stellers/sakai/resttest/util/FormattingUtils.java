package net.stellers.sakai.resttest.util;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FormattingUtils {

	public static String prettyPrint(JsonNode node) {
	    try {	    	
	        ObjectMapper mapper = new ObjectMapper();	        
	        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter(); 
	        return writer.writeValueAsString(node);
	    } 
	    catch (JsonProcessingException e) {
	    	log.error(e.getMessage(),e);
	    	return "ERROR pretty-printing "+node;
	    }
	}
	
}
