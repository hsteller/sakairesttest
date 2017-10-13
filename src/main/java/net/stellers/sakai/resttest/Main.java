package net.stellers.sakai.resttest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;
import net.stellers.sakai.resttest.service.UserService;
import net.stellers.sakai.resttest.util.FormattingUtils;

@Component
@Slf4j
public class Main {
	
	@Autowired
	UserService userService;
	
	
	private void doYourStuffHere(String [] args){		
		final String USER_ID="a0ad79f6-3987-4b38-93b5-68212180287e";
		final String PROP_NAME= "lastName";
		final String NEW_PROP_VALUE="hendrikwashere2";
		
		JsonNode currentUser = userService.getCurrentUser();		
		log.info("Who am I?(Current User):\n"+FormattingUtils.prettyPrint(currentUser));									
		
		JsonNode node = userService.getUser(USER_ID);
		if (log.isDebugEnabled()) {
			log.debug("The other one:\n"+FormattingUtils.prettyPrint(node));
		}
		
		log.info(PROP_NAME+" before: "+node.get(PROP_NAME));						
		ObjectNode editable = (ObjectNode) node; 
		userService.updateUser(editable.put(PROP_NAME,NEW_PROP_VALUE));		
		JsonNode node2 = userService.getUser(USER_ID);		
		log.info(PROP_NAME+" after: "+node2.get("lastName"));
	}
	
	
	public static void main (String [] args){
		AbstractApplicationContext ctx = initContext();        
		Main itsMe = ctx.getBean(Main.class);
		itsMe.doYourStuffHere(args);
		ctx.close();
	}
	
	private static AbstractApplicationContext initContext(){
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.scan("net.stellers.sakai.resttest");		
		ctx.refresh();
		ctx.registerShutdownHook();
		return ctx;
		
	}
	
}