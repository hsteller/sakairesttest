package net.stellers.sakai.resttest.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface UserService {

	public JsonNode getCurrentUser();

	public JsonNode getUser(String sakaiUserId);
	
	public void updateUser( JsonNode user);

}
