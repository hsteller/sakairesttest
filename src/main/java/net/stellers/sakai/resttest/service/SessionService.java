package net.stellers.sakai.resttest.service;

import net.stellers.sakai.resttest.config.Credentials;

public interface SessionService {

	/**
	 * Attempts to log into sakai using username and password.
	 * Will set the session cookie value in the parameter Credentials object if the HTTP response contained
	 * a cookie whose name matches the session cookie name provided by the Credentials.  
	 * 
	 * @param creds login credentals containing username, password and the name of the session cookie whose value will be set
	 * @return the sakai session id
	 */
	public String login (Credentials creds);
	
	
	public void logout(String sakaiSessionId);
}
