package net.stellers.sakai.resttest.config;


import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
@PropertySources ({
	@PropertySource(value="project.properties"),
    @PropertySource(value = "file:./project.properties", ignoreResourceNotFound = true)
})
public class AppConfig {

	
	
	 @Bean
	 public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	        return new PropertySourcesPlaceholderConfigurer();
	 }
	
	@Bean(name="messageSource")
	public MessageSource getMessageSource(){		
		ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
		ms.addBasenames("messages","errors");
		return ms;
	}
	
	
	
	
}
