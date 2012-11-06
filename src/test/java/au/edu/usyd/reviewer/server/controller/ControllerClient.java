package au.edu.usyd.reviewer.server.controller;

import java.util.Collections;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class ControllerClient {

	protected static RestTemplate restTemplate = new RestTemplate();
	protected static HttpEntity<?> requestEntity;
	
	/**
	 * Initialize rest template to work with json
	 */
	protected static void initializeRestTemplate(){
		// Set the Accept header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept(Collections.singletonList(new MediaType("application","json")));
		requestEntity = new HttpEntity<Object>(requestHeaders);

		// Create a new RestTemplate instance
		RestTemplate restTemplate = new RestTemplate();

		// Add the Jackson message converter
		restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
	}

}
