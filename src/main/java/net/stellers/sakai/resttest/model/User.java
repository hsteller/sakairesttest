package net.stellers.sakai.resttest.model;

import java.util.Date;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor // kinda breaks the builder pattern, but needed for jackson
@AllArgsConstructor // needed by @Builder if a NoArgsConstructor is present (which jackson needs)
@Builder (builderMethodName="privateBuilder")
public class User {
	// user properties + names were obtained from the returned "raw" JSON data
    private String id;
    private String eid;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String displayName;
    private String type;
    private String owner;
    private long lastModified;
    public Map<String, String> props;//=new HashMap<>();
    
    
    private Date createdDate;
    private SakaiTime createdTime;
    private String displayId;
    private Date modifiedDate;
    private SakaiTime modifiedTime;
    private String reference;
    private String sortName;
    private String url;
    private String entityReference;
    private String entityURL;
    private String entityId;
    private String entityTitle;    
    

    
    public static UserBuilder builder(String eid){
        return privateBuilder().eid(eid);
    }

    
}
