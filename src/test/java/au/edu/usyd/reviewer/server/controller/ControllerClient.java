package au.edu.usyd.reviewer.server.controller;

import java.net.URLConnection;
import java.util.Collections;

import org.apache.catalina.util.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
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

		HttpClient client = new HttpClient();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("admin@demo-sinaptico.com","reviewer");
		client.getState().setCredentials(new AuthScope("127.0.0.1", 8888, AuthScope.ANY_REALM), credentials);
		CommonsClientHttpRequestFactory commons = new CommonsClientHttpRequestFactory(client);
		restTemplate = new RestTemplate(commons);
		
		// Add the Jackson message converter
		restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
	
	}

}
