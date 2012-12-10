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
import java.util.List;

import org.apache.catalina.util.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.User;

public class OrganizationControllerClient extends ControllerClient {

	
	
	private static List<Organization> getOrganizations(){
		List<Organization> organizations = new ArrayList<Organization>();
		try{

			HttpClient client = new HttpClient();
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("superAdmin@demo-sinaptico.com","reviewer");
			client.getState().setCredentials(new AuthScope("127.0.0.1", 8888, AuthScope.ANY_REALM), credentials);
			CommonsClientHttpRequestFactory commons = new CommonsClientHttpRequestFactory(client);
			restTemplate = new RestTemplate(commons);
			
			String url = "http://127.0.0.1:8888/v1/organizations?name=O&page=1&limit=2";
			ResponseEntity<Organization[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Organization[].class);
			Organization[] organizationArray = responseEntity.getBody();
			organizations = Arrays.asList(organizationArray);
			for(Organization organization : organizations){
				System.out.println("Course " + organization.getName() + " id " + organization.getId());
			}			
		} catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return organizations;
	}

	private static void getOrganization(Long id){
		try{
			if (id != null){
				String url = "http://127.0.0.1:8888/v1/organizations/" + id.toString();
				ResponseEntity<Organization> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Organization.class);
				Organization organization = responseEntity.getBody();
				if (organization != null){
					System.out.println("Course " + organization.getName() + " id " + organization.getId());
				}
			}
		} catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
	}
	
	private static void getOrganizationUsers(Long id){
		try{
			
			if (id != null){
				HttpClient client = new HttpClient();
				UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("superAdmin@demo-sinaptico.com","reviewer");
				client.getState().setCredentials(new AuthScope("127.0.0.1", 8888, AuthScope.ANY_REALM), credentials);
				CommonsClientHttpRequestFactory commons = new CommonsClientHttpRequestFactory(client);
				restTemplate = new RestTemplate(commons);
				 
				String url = "http://127.0.0.1:8888/v1/organizations/" + id.toString() + "/users?roles=lecturers&assigned=true";
				ResponseEntity<User[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, User[].class);
				User usersArray[] = responseEntity.getBody();
				List<User> users = Arrays.asList(usersArray);
				System.out.println("********* ASSIGNED LECTURERS *********");
				for(User user:users){
					System.out.println("User " + user.getLastname() + " " + user.getFirstname() + " id " + user.getId() + " email " + user.getEmail());
				}
				
				url = "http://127.0.0.1:8888/v1/organizations/" + id.toString() + "/users?roles=tutors&assigned=true";
				responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, User[].class);
				usersArray = responseEntity.getBody();
				users = Arrays.asList(usersArray);
				System.out.println("********* ASSIGNED TUTORS *********");
				for(User user:users){
					System.out.println("User " + user.getLastname() + " " + user.getFirstname() + " id " + user.getId() + " email " + user.getEmail());
				}
				

				url = "http://127.0.0.1:8888/v1/organizations/" + id.toString() + "/users?roles=students&assigned=true";
				responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, User[].class);				
				usersArray = responseEntity.getBody();
				users = Arrays.asList(usersArray);
				System.out.println("********* ASSIGNED STUDENTS *********");
				for(User user:users){
					System.out.println("User " + user.getLastname() + " " + user.getFirstname() + " id " + user.getId() + " email " + user.getEmail());
				}
				
				url = "http://127.0.0.1:8888/v1/organizations/" + id.toString() + "/users?roles=lecturers,tutors,students&assigned=true";
				responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, User[].class);
				usersArray = responseEntity.getBody();
				users = Arrays.asList(usersArray);
				System.out.println("********* ASSIGNED LECTURERS, USERS, TUTORS  *********");
				for(User user:users){
					System.out.println("User " + user.getLastname() + " " + user.getFirstname() + " id " + user.getId() + " email " + user.getEmail());
				}
				
				url = "http://127.0.0.1:8888/v1/organizations/" + id.toString() + "/users?roles=all";
				responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, User[].class);
				usersArray = responseEntity.getBody();
				users = Arrays.asList(usersArray);
				System.out.println("********* ALL ORGANIZATION USERS *********");
				for(User user:users){
					System.out.println("User " + user.getLastname() + " " + user.getFirstname() + " id " + user.getId() + " email " + user.getEmail());
				}
				
				
				url = "http://127.0.0.1:8888/v1/organizations/" + id.toString() + "/users?roles=lecturers,tutors&assigned=false";
				responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, User[].class);
				usersArray = responseEntity.getBody();
				users = Arrays.asList(usersArray);
				System.out.println("********* AVAILABLE LECTURERS AND TUTORS *********");
				for(User user:users){
					System.out.println("User " + user.getLastname() + " " + user.getFirstname() + " id " + user.getId() + " email " + user.getEmail());
				}
				
				url = "http://127.0.0.1:8888/v1/organizations/" + id.toString() + "/users?roles=students&assigned=false";
				responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, User[].class);
				usersArray = responseEntity.getBody();
				users = Arrays.asList(usersArray);
				System.out.println("********* AVAILABLE STUDENTS *********");
				for(User user:users){
					System.out.println("User " + user.getLastname() + " " + user.getFirstname() + " id " + user.getId() + " email " + user.getEmail());
				}
				
				
			}
		} catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
	}
	
	private static void deleteOrganization(String url, Long id){
		delete(url + id.toString());
	}
	
	public static void main(String[] args) {
		System.out.println("/******************* START **********************/");
		try{
			initializeRestTemplate();
//			String json="{\"name\":\"Organization TEST 4\",\"organizationProperties\":[],\"properties\":[]}";
//			String userPassword = "superAdmin@demo-sinaptico.com:reviewer";
//			save(new URL("http://127.0.0.1:8888/v1/organizations/"), json, userPassword);
//			List<Organization> organizations = getOrganizations();
//			for(Organization organization: organizations){
//				getOrganization(organization.getId());
//				deleteOrganization("http://127.0.0.1:8888/v1/organizations/",organization.getId());
//			}
			getOrganizationUsers(new Long(1));
		} catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		System.out.println("/******************* END **********************/");
	}

}
