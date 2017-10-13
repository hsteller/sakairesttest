package net.stellers.sakai.resttest.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.stellers.sakai.resttest.config.Credentials;
import net.stellers.sakai.resttest.service.SessionService;


@Slf4j
@Component (value="myAuthFilter")
public class AuthenticationFilter implements ClientRequestFilter, DisposableBean{

	public static final String PROP_NO_SESSION_CREATION="net.stellers.sakai.resttest.authenticationFilter.dontCreateSession";
	
	@Autowired
	private Credentials creds;
	
	@Autowired
	private SessionService loginservice;
	
	private HashSet<String> createdSakaiSessionIds=new HashSet<String>();
	
	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		boolean createSession =requestContext.getProperty(PROP_NO_SESSION_CREATION)==null;
		if (log.isTraceEnabled()){
			log.trace("auth filter called; credentials: "+creds);
			log.trace("doLogin: "+createSession);
		}
		 		
		MultivaluedMap<String, Object> headers = requestContext.getHeaders();
		Map<String, Cookie> cookies = requestContext.getCookies();
		int cookieCount = cookies!=null?cookies.size():0;
		if (cookieCount<1 || !cookies.containsKey(creds.getSessionCookieName())){					
			Cookie newSessionCookie = getSessionCookie(createSession);
			if (newSessionCookie != null){
				ArrayList<Object> newCookies = new ArrayList<>(cookieCount+1);
				if (cookieCount>0){
					log.trace("found "+cookieCount+" old cookies to copy");
					for (Cookie c:cookies.values()){						
						newCookies.add(c.toString());
					}				
				}				
				newCookies.add(newSessionCookie.toString());
				headers.put("Cookie", newCookies);
			}
			else {
				log.warn("new session cookie was null");
			}
		}
		else {
			log.trace("reusing existing session cookie");
		}
			
	}
	
	

	private Cookie getSessionCookie(boolean doLogin){
		String sessionCookie =  creds.getSessionCookieValue();
		if (sessionCookie == null || sessionCookie.trim().length()<1){
			if (doLogin) {
				String sessionId = loginservice.login(creds);
				if (sessionId != null){
					createdSakaiSessionIds.add(sessionId);
				}
				sessionCookie = creds.getSessionCookieValue(); // should have been set by login				
				log.debug("new session cookie value after login: "+sessionCookie);
			}
		}		
		if (sessionCookie != null){
			Cookie c = new Cookie(creds.getSessionCookieName(), sessionCookie);
			return c;
		}
		return null;
	}
	



	@Override
	public void destroy() throws Exception {
		log.info("cleaning up (closing) sessions which I have created");
		for (String sakaiSessionId : createdSakaiSessionIds){
			log.debug("logging out of session: "+sakaiSessionId);
			try {
				loginservice.logout(sakaiSessionId);
			}
			catch (Exception e){
				log.error(e.getMessage(),e);
			}
		}
		log.info("session clean up done");

		
	}
	
	
}
