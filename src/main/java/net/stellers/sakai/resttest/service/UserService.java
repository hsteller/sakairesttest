package net.stellers.sakai.resttest.service;

import com.fasterxml.jackson.databind.JsonNode;

import net.stellers.sakai.resttest.model.User;

public interface UserService {

	public JsonNode getCurrentUserRAW();
	public User getCurrentUser();

	public JsonNode getUserRAW(String sakaiUserId);
	public User getUser(String sakaiUserId);
	
	public void updateUserRAW( JsonNode user);
	public void updateUser( User user);
	
	public String createUser(User user);
	
	public boolean deleteUser(String sakaiUserId);

	
}
