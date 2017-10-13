package net.stellers.sakai.resttest.service.impl;


import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.stellers.sakai.resttest.config.Credentials;
import net.stellers.sakai.resttest.service.SessionService;
import net.stellers.sakai.resttest.util.AuthenticationFilter;

@Service
@Slf4j
@NoArgsConstructor
public class SessionServiceImpl extends BaseServiceImpl implements SessionService {

	
	private final String ENDPOINT="/session";
	
	private static final String PARAM_USER="_username";
	private static final String PARAM_PASS="_password";
	
	
	@Override
	public String login(Credentials creds) {				
		Client myLocalClientWithoutAuthFilter = rsClient; // ClientBuilder.newClient(); 
		String url = baseUrl+ENDPOINT;		
		WebTarget wt = myLocalClientWithoutAuthFilter.target(url);
		Form form = new Form();
		form.param(PARAM_USER, creds.getUsername());
		form.param(PARAM_PASS, creds.getPassword());
		Response resp = wt.request().
		post(Entity.form(form));
		Map<String,NewCookie> cookies = resp.getCookies();
		if (cookies != null) {
			if (log.isTraceEnabled()) {
				log.debug("new cookies upon login: ");
				for (NewCookie c:cookies.values()){
					log.debug("\t"+c.toString());
				}
			}
			NewCookie cookie = cookies.get(creds.getSessionCookieName());
			if (cookie != null) {
				String value = cookie.getValue();
				creds.setSessionCookieValue(value);
				log.debug("updating session cookie value: "+value);
			}
		}
		if (log.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("response headers:\n");
			MultivaluedMap<String, Object> headers = resp.getHeaders();
			if (headers != null){
				for (Map.Entry<String, List<Object>>h:headers.entrySet()){					
					sb.append("\n").append(h.getKey()+"->");
					List<Object> values = h.getValue();
					if (values != null){
						for (Object v:values){
							sb.append(v).append("\n\t");
						}
					}
					else {
						sb.append("<<NULL>>");
					}
					log.trace(sb.toString());
				}	
				
			}
		}
		
		int status = resp.getStatus();				
		
		String sessionId = resp.readEntity(String.class);				
		if (log.isDebugEnabled()){
			log.debug("login response status: "+status);
			log.debug("login response body:\n"+sessionId);
		}
				
		if (status==201) {
			return sessionId;
		}
		else {
			throw new RuntimeException("something went wrong; response code: "+status+"\nresponse body: "+sessionId);
		}
	}


	@Override
	public void logout(String sakaiSessionId) {
		log.debug("logout for session "+sakaiSessionId);
		String url = baseUrl+ENDPOINT;				
		WebTarget wt = rsClient.target(url+"/"+sakaiSessionId);
		wt.register(authFilter);		
		Response resp = wt.request().buildDelete().property(AuthenticationFilter.PROP_NO_SESSION_CREATION, "foobar").invoke();		
		int status = resp.getStatus();
		String text = resp.readEntity(String.class);
		if (status != 204){			
			log.warn("logout response status: "+resp.getStatus());	
			log.warn("logout response text: "+text);
		}				
	}
	
	
	
	
	
	
	

}
