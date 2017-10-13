package net.stellers.sakai.resttest.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Configuration
@Getter
@Setter
@ToString
public class Credentials {
	
	@Value ("${auth.username}")
	private String username;
	@Value ("${auth.password}")
	private String password;
	@Value ("${auth.sessionCookieValue}")
	private String sessionCookieValue;	
	@Value ("${auth.sessionCookieName}")
	private String sessionCookieName;
	
}
