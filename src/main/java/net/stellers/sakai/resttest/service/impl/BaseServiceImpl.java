package net.stellers.sakai.resttest.service.impl;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import net.stellers.sakai.resttest.util.AuthenticationFilter;

@Service
public class BaseServiceImpl {

	@Value("${app.sakaiBaseURL}")
	protected final String baseUrl=null;
	
	@Autowired
	protected AuthenticationFilter authFilter;
	
	
	protected Client rsClient=ClientBuilder.newClient().register(JacksonJsonProvider.class);
	
	
		
	
}
