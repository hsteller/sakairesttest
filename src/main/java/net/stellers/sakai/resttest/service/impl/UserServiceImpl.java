package net.stellers.sakai.resttest.service.impl;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.stellers.sakai.resttest.model.User;
import net.stellers.sakai.resttest.service.UserService;

@Service
@Slf4j
public class UserServiceImpl extends BaseServiceImpl implements UserService {

	private final String ENDPOINT="/user";


	private final WebTarget prepareCall(String endpoint) {
		WebTarget t = rsClient.target(baseUrl+ENDPOINT+endpoint);
		t.register(authFilter); // takes care of authentication via the credentials in "project.properties"
		return t;
	}
	
	@Override
	public JsonNode getCurrentUserRAW() {					
		return getCurrentUser(JsonNode.class);		
	}
	
	@Override
	public User getCurrentUser() {		
		return getCurrentUser(User.class);
	}

	
	private final <T> T  getCurrentUser(Class<T> clazz) {		
		WebTarget t = prepareCall("/current.json");
		T data = t.request(MediaType.APPLICATION_JSON).buildGet().invoke(clazz);
		return data;
	}
	

	@Override
	public JsonNode getUserRAW(String sakaiUserId) {		
		return getUser(JsonNode.class, sakaiUserId);
	}
	@Override
	public User getUser(String sakaiUserId) {
		return getUser(User.class,sakaiUserId);
	}
	
	private final <T> T  getUser(Class<T> clazz, String sakaiUserId) {
		WebTarget t = prepareCall("/"+sakaiUserId+".json");
		Response r = t.request(MediaType.APPLICATION_JSON).buildGet().invoke();
		if (r.getStatus()==200) {
			return  r.readEntity(clazz);
		}
		else {
			log.warn("getUser(); received error(?) status of "+r.getStatus());
			log.debug("getUser() will return null");
			return null;
		}
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
	public void updateUser(User user) {
		String userId = user.getId(); 		
		updateUser(userId, user);
	}
	
	@Override
	public void updateUserRAW( JsonNode user){
		String userId = user.get("id").asText(); 		
		updateUser(userId, user);
	}
	

	
	private final<T> void updateUser( String userId, T data){ 		
		WebTarget t = prepareCall("/"+userId);
		Entity<T>ent = Entity.entity(data, MediaType.APPLICATION_JSON);
		Invocation i = t.request(MediaType.APPLICATION_JSON).buildPut(ent);		
		Response r = i.invoke();
		String jsonString = r.readEntity(String.class);
		log.debug("updateUser got response: "+jsonString);
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
	
	
	
	
	@Override
	public String createUser(User user){
		if (user == null || user.getEid()==null) {
			throw new IllegalArgumentException("The Sakai REST API's create method says that you must at least provide an eid for the user to be ceated.");
		}			
		String json;
		try {
			json = new ObjectMapper().writeValueAsString(user);
		}
		catch (JsonProcessingException e) { // shouldn't happen (famous last words)..
			log.error(e.getMessage(),e);
			throw new RuntimeException(e);
		}						
		WebTarget t = prepareCall(""); // no specific endpoint; being a POST is what decides what is being done
		Entity<String> ent = Entity.entity (json, MediaType.APPLICATION_JSON);
		Invocation i = t.request(MediaType.APPLICATION_JSON).buildPost(ent);	
		Response r = i.invoke();
		String jsonString = r.readEntity(String.class);
		if (r.getStatus()!=201) {
			log.error("createUser(): response status: "+r.getStatus()+"\nresponse body: "+jsonString);
			log.error("request payload was:\n\t"+json);
			return null;
		}
		return jsonString;
		
	}





	@Override
	public boolean deleteUser(String sakaiUserId) {		 	
		WebTarget t = prepareCall("/"+sakaiUserId);
		Invocation i = t.request(MediaType.APPLICATION_JSON).buildDelete();		
		Response r = i.invoke();
		String jsonString = r.readEntity(String.class);
		if (r.getStatus()!=204) {
			log.error("deleteUser(): response status: "+r.getStatus()+"\nresponse body: "+jsonString);
			return false;
		}
		else {
			return true;
		}
	}


	
}
