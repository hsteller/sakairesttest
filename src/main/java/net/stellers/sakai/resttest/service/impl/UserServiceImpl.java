package net.stellers.sakai.resttest.service.impl;

import java.io.IOException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.stellers.sakai.resttest.service.UserService;

@Service
@Slf4j
public class UserServiceImpl extends BaseServiceImpl implements UserService {

	private final String ENDPOINT="/user";

	
	@Override
	public JsonNode getCurrentUser() {		
		WebTarget t = rsClient.target(baseUrl+ENDPOINT+"/current.json");
		t.register(authFilter);		
		JsonNode node = t.request(MediaType.APPLICATION_JSON).buildGet().invoke(JsonNode.class);		
		return node;
	}



	@Override
	public JsonNode getUser(String sakaiUserId) {		
		WebTarget t = rsClient.target(baseUrl+ENDPOINT+"/"+sakaiUserId+".json");
		t.register(authFilter);
		Response r = t.request(MediaType.APPLICATION_JSON).buildGet().invoke();
		JsonNode node = r.readEntity(JsonNode.class);
		return node;
		/*
		  // This works, too:
		  String jsonString = r.readEntity(String.class);
		  ObjectMapper mapper = new ObjectMapper();		
			try {
				node = mapper.readTree(jsonString);
				return node;
			}
			catch (IOException e) {
				log.error(e.getMessage(),e);
				return null;
			}
		*/
	}
	
	@Override
	public void updateUser( JsonNode user){
		String userId = user.get("id").asText(); 		
		WebTarget t = rsClient.target(baseUrl+ENDPOINT+"/"+userId);
		t.register(authFilter);		
		Entity<JsonNode>ent = Entity.entity(user, MediaType.APPLICATION_JSON);
		Invocation i = t.request(MediaType.APPLICATION_JSON).buildPut(ent);		
		Response r = i.invoke();
		String jsonString = r.readEntity(String.class);
		if (r.getStatus()!=204) {
			log.error("updateUser(): response status: "+r.getStatus()+"\nresponse body: "+jsonString);			
		}

		/* This works, too:
		WebTarget t = rsClient.target(baseUrl+ENDPOINT+"/"+userId);
		t.register(authFilter);		
		Entity ent = Entity.entity("{\"id\"=\""+userId+"\",\"lastName\"=\"barfuss\"}", 
				MediaType.APPLICATION_JSON);			
		Invocation i = t.request(MediaType.APPLICATION_JSON).buildPut(ent);		
		Response r = i.invoke();
		String jsonString = r.readEntity(String.class);
		log.debug("response: "+r.getStatus()+":\n"+jsonString);
		*/
		
	}
	
}
