package net.stellers.sakai.resttest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;
import net.stellers.sakai.resttest.model.User;
import net.stellers.sakai.resttest.service.UserService;
import net.stellers.sakai.resttest.util.FormattingUtils;

@Component
@Slf4j
public class Main {
	
	@Autowired
	private UserService userService;
	
	
	/** Pseudo-"main"-method. 
	 *  Called by the real main method after setting up Spring's application context. 
	 */
	private void doYourStuffHere(String [] args){		
		
		// 1. Doing stuff with raw JSON
		doingStuffExampleRaw("admin");
		
		// 2. Doing stuff by modifying and (de)serializing "User"-POJOs 
		doingStuffExample("ExternalIdOfHendriksRestTestTestUser");					
	}
	
	public static void main (String [] args){
		AbstractApplicationContext ctx = initContext();        
		Main itsMe = ctx.getBean(Main.class);
		try {
			itsMe.doYourStuffHere(args);
		}
		finally {
			if (ctx != null) {
				ctx.close();
			}
		}
	}
	
	private static AbstractApplicationContext initContext(){
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.scan("net.stellers.sakai.resttest");		
		ctx.refresh();
		ctx.registerShutdownHook();
		return ctx;
		
	}
	
	
	
	
	/*+
	 * <ol>
	 * 	<li> Creates a test user with the provided E_ID</li>
	 *  <li> Fetches and dumps the test user's data</li>
	 *  <li> Changes the test user's last name</li>
	 *  <li> Fetches and dumps the updated test user</li>
	 *  <li> Deletes the test user again</li>
	 * </ol>
	 * 
	 *  @param USER_EXTERNAL_ID E-ID of the user to be created, modified and deleted again 
	 */
	private void doingStuffExample(final String USER_EXTERNAL_ID) {
		final String CHANGE_LASTNAME_TO="ChangedLastName";		
		final User toBeCreated = User.builder(USER_EXTERNAL_ID)
							.email("sakai@stellers.net")
							.firstName("TestFirstname")		
							.lastName("TestLasstname")
							.type("registered")
							.build();
		
		// Let's be verbose and make some if-else spaghetti
		final String internalID = userService.createUser(toBeCreated);
		if (internalID != null) {
			log.info("Created user with internal ID "+internalID+" for external ID "+USER_EXTERNAL_ID);
			User fetched = userService.getUser(internalID);
			if (log.isDebugEnabled()) {
				log.debug("created user's data:\n"+fetched);
			}
			if (fetched != null) {
				final String LASTNAME_ORIGINAL = fetched.getLastName();
				log.info("fetched lastname: "+LASTNAME_ORIGINAL);				
				log.info("changing lastname to "+CHANGE_LASTNAME_TO);
				fetched.setLastName(CHANGE_LASTNAME_TO);
				userService.updateUser(fetched);
				User changed = userService.getUser(internalID);
				if (changed != null) {
					log.info("user lastname is now: "+changed.getLastName());
					if (log.isDebugEnabled()) {
						log.debug("updted user's data:\n"+fetched);
					}
					log.info("deleting user now");
					boolean success = userService.deleteUser(internalID);
					log.info("Deleting user was "+(success?"":"NOT ")+"successful.");
					User shouldntExist = userService.getUser(internalID);
					if (shouldntExist==null) {
						log.info("User with "+internalID+" appears to be gone again.");
					}
					else {
						log.error("User with "+internalID+" seems still to exist! :-(");
					}					
				}
				else {
					log.error("getUser("+internalID+") failed to retrieve the user we just updated.");
				}
			}
			else {
				log.error("getUser("+internalID+") failed to fetch the user we just created.");
			}						
		}
		else {
			log.error("createUser returned NULL as internal ID of the created user, i.e. something went wrong.");
		}
		
	}
	
	
	
	private static final String PROP_NAME= "lastName";
	private static final String NEW_PROP_VALUE= PROP_NAME+"-hendrikWasHere";
	/**
	 * <ol>
	 * <li> Fetches and dumps the info of the current user</li> 
	 * <li> Fetches and dumps the info of another user</li>
	 * <li> Changes that user's property "{@value #PROP_NAME}" to "{@value #NEW_PROP_VALUE}"</li>
	 * <li> Fetches and dumps the changed data</li>
	 * <li> Changes the property back to its original value</li>
	 * <li> Fetches and dumps the restored data</li>
	 * </ol>
	 * 
	 *  This method differs from {@link #doingStuffExample(String)} by using raw JSON objects for its calls 
	 *  (it also does slightly different things, i.e. no user creation/deletion).
	 * 
	 * @param EXISTING_USER_ID (internal) ID of an already existing test user
	 *  
	 */	
	private final void doingStuffExampleRaw (final String EXISTING_USER_ID) {
		//final String EXISTING_USER_ID="admin";				
		// get current User
		JsonNode currentUser = userService.getCurrentUserRAW();		
		log.info("RAW: Who am I?(Current User):\n"+FormattingUtils.prettyPrint(currentUser));	
			
		// get user with a specific ID
		JsonNode theOtherOne = userService.getUserRAW(EXISTING_USER_ID);
		if (log.isDebugEnabled()) {
			log.debug("RAW: User with id "+EXISTING_USER_ID+":\n"+FormattingUtils.prettyPrint(theOtherOne));
		}
		// change a property (lastName) of that specific user
		final String PROP_VALUE_ORIGINAL = theOtherOne.get(PROP_NAME).asText();
		log.info(PROP_NAME+" of user "+EXISTING_USER_ID+" before changing it: "+PROP_VALUE_ORIGINAL);						
		ObjectNode editable = (ObjectNode) theOtherOne;
		editable = editable.put(PROP_NAME,NEW_PROP_VALUE);
		userService.updateUserRAW(editable);
		// reading that specific user back after the property change
		theOtherOne = userService.getUserRAW(EXISTING_USER_ID);
		final String PROP_VALUE_CHANGED = theOtherOne.get(PROP_NAME).asText();
		log.info(PROP_NAME+" of user "+EXISTING_USER_ID+" after changing it: "+PROP_VALUE_CHANGED);
		// change the property back to its original value:
		editable = (ObjectNode) theOtherOne;
		editable = editable.put(PROP_NAME,PROP_VALUE_ORIGINAL);
		userService.updateUserRAW(editable);
		// reading that specific user back after changing the property to its original value
		theOtherOne = userService.getUserRAW(EXISTING_USER_ID);
		final String PROP_VALUE_RESTORED = theOtherOne.get(PROP_NAME).asText();
		log.info(PROP_NAME+" of user "+EXISTING_USER_ID+" after restoring it: "+PROP_VALUE_RESTORED);
		if (log.isDebugEnabled()) {
			log.debug("RAW: User state at then end for "+EXISTING_USER_ID+":\n"+FormattingUtils.prettyPrint(theOtherOne));
		}
	}
	
	

	
}