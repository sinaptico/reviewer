package au.edu.usyd.reviewer.server.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.realm.RealmBase;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import au.edu.usyd.reviewer.client.core.User;

public class UserControllerClient extends ControllerClient{

//	private void login(){
//		try{
//			String email = "admin@demo-sinaptico.com";
//			String password = RealmBase.Digest("PONER EL PASSWORD", "MD5",null);
//			String url = "http://127.0.0.1:8888/v1/User/login";
//			
//			HttpHeaders requestHeaders = new HttpHeaders();
//			requestHeaders.setAccept(Collections.singletonList(new MediaType("application","json")));
//	
//			// Create the request body as a MultiValueMap
//			MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();     
//			body.add("email", email);
//			body.add("password", password);
//	
//			// Note the body object as first parameter!
//			HttpEntity<?> httpEntity = new HttpEntity<Object>(body, requestHeaders);
//	
//			ResponseEntity<User> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, User.class);
//			User user = responseEntity.getBody();
//			if (user != null){
//				System.out.println("User " + user.getFirstname() + " " + user.getLastname() + " id " + user.getId());
//			}
//		} catch(Exception e){
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//		}
//	}
//	
//	
//	private void logout(){
//		try{
//			
//		} catch(Exception e){
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//		}
//	}

}
