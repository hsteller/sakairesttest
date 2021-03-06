## Synopsis

An example client for messing around with Sakai's web service interface.
It's a Spring-based application which uses JAX-RS with Jersey and Jackson as providers.
The example shows two different ways to perform basic CRUD operations with Sakai's user API.

## Howto

Check and adjust a couple of default values (username, password, server URL) in 
[src/main/resources/project.properties](src/main/resources/project.properties)
to match your Sakai installation.

Use Maven to build and run the application:

*mvn clean compile install exec:java*

This calls the Main class, which will then set up the Spring stuff and call the aptly named method
[Main.doYourStuffHere(String [] args)](src/main/java/net/stellers/sakai/resttest/Main.java)
which currently 

1. Changes the "admin" user's lastName to something else and back to its original value.
2. Creates as new user with E-ID "ExternalIdOfHendriksRestTestTestUser", changes its lastName and deletes that user again.

