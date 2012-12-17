package au.edu.usyd.reviewer.server.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.catalina.util.Base64;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;


import au.edu.usyd.reviewer.client.core.Choice;
import au.edu.usyd.reviewer.client.core.ReviewTemplate;
import au.edu.usyd.reviewer.client.core.Section;

public class ReviewTemplateControllerClient extends ControllerClient{

	/**
	 * Call ReviewTemplateController to save one review template with 3 sections, one per each type of section. 
	 */
	private static void saveReviewTemplates(){
		try{
			
			URL url;
			try {
				url = new URL("http://127.0.0.1:8888/v1/reviewtemplates/");
				
				String userPassword = "admin@demo-sinaptico.com:reviewer";   
				String encoding = (new Base64()).encode(userPassword.getBytes());   
				URLConnection uc = url.openConnection();   
				uc.setRequestProperty("Authorization", "Basic " + encoding); 
				
				HttpURLConnection conn = (HttpURLConnection) uc;
				conn.setDoOutput(true);
				conn.setRequestMethod("PUT");
				conn.setRequestProperty("Content-Type", "application/json");
					
				String json = "{\"description\":\"Description Review Template TEST PUT\",\"name\":\"TEST PUT\",\"sections\":[{\"text\":\"Test PUT Review Template\",\"tool\":\"structure\",\"choices\":[],\"number\":1,\"type\":0}]}";
				
				OutputStream os = conn.getOutputStream();
				os.write(json.getBytes());
				os.flush();
				
				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					System.out.println("Failed : HTTP error code : " + conn.getResponseCode());
					System.out.println(conn.getResponseMessage());
				} else {
				
					BufferedReader br = new BufferedReader(new InputStreamReader(
							(conn.getInputStream())));
			 
					String output;
					System.out.println("Output from Server .... \n");
					while ((output = br.readLine()) != null) {
						System.out.println(output);
					}
				}
				conn.disconnect();
		 
			} catch (MalformedURLException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
				
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
		
	/**
	 * Delete all the review templates received as parameter
	 * @param reviewTemplates review templates to remove
	 */
	private static void deleteReviewTemplates(){
		try{		
			restTemplate.delete("http://127.0.0.1:8888/v1/reviewtemplates/{id}", "14");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		initializeRestTemplate();
		saveReviewTemplates();
		deleteReviewTemplates();
	}
}
