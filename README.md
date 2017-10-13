## Synopsis

An example client for messing around with Sakai's web service interface.
It's a Spring-based application which uses JAX-RS with Jersey and Jackson as providers.
The example retrieves users by (internal) ID and updates the last name of one user.

## Howto

Check and adjust a couple of default values (username, password, server URL) in 
[src/main/resources/project.properties](src/main/resources/project.properties)
to match your Sakai installation.

Use Maven to build and run the application:

*mvn clean compile install exec:java*

This call the Main class which will set up the Spring stuff and then call the aptly named
[Main.doYourStuffHere(String [] args)](src/main/java/net/stellers/sakai/resttest/Main.java)
which currently changes a user's last name.
