package au.edu.usyd.reviewer.server.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("superAdmin@demo-sinaptico.cm","reviewer");
		client.getState().setCredentials(new AuthScope("127.0.0.1", 8888, AuthScope.ANY_REALM), credentials);
		CommonsClientHttpRequestFactory commons = new CommonsClientHttpRequestFactory(client);
		restTemplate = new RestTemplate(commons);
		
		// Add the Jackson message converter
		restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
	
	}
	
	protected static void save( URL url, String json, String userPassword){
		try {			   
			String encoding = (new Base64()).encode(userPassword.getBytes());   
			URLConnection uc = url.openConnection();   
			uc.setRequestProperty("Authorization", "Basic " + encoding); 
			
			HttpURLConnection conn = (HttpURLConnection) uc;
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");
	
			
			OutputStream os = conn.getOutputStream();
			os.write(json.getBytes());
			os.flush();
	 
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("Failed : HTTP error code : " + conn.getResponseCode());
				System.out.println(conn.getResponseMessage());			
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	 
			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
	 
			conn.disconnect();
	 

		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	protected static void delete(String url){
		try{		
			restTemplate.delete(url);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
}
