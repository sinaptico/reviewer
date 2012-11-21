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



import au.edu.usyd.reviewer.client.core.Course;

public class CourseControllerClient extends ControllerClient {

	
	/**
	 * Call CourseController to save a course 
	 */	
	private static void saveCourse(){
		
		URL url;
		try {
			url = new URL("http://127.0.0.1:8888/v1/courses/");
			
			String userPassword = "admin@demo-sinaptico.com:reviewer";   
			String encoding = (new Base64()).encode(userPassword.getBytes());   
			URLConnection uc = url.openConnection();   
			uc.setRequestProperty("Authorization", "Basic " + encoding); 
			
			HttpURLConnection conn = (HttpURLConnection) uc;
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");
	 
			String json = "{\"name\":\"TEST\",\"year\":2012,\"semester\":3,\"folderId\":null,\"templatesFolderId\":null,\"spreadsheetId\":null,\"domainName\":\"demo-sinaptico.com\",\"tutorials\":[],\"lecturers\":[],\"tutors\":[],\"supervisors\":[],\"studentGroups\":[],\"writingActivities\":[],\"templates\":[],\"automaticReviewers\":[],\"organization\":null}";
			
			OutputStream os = conn.getOutputStream();
			os.write(json.getBytes());
			os.flush();
	 
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
	 
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

	/**
	 * Call CourseController to get the course with id equals to courseId
	 * @param course Course to get
	 */
	private static void getCourse(Long courseId){
		try{
			if (courseId != null){
				String url = "http://127.0.0.1:8888/v1/courses/" + courseId.toString();
				ResponseEntity<Course> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Course.class);
				Course course = responseEntity.getBody();
				if (course != null){
					System.out.println("Course " + course.getName() + " id " + course.getId());
				}
			}
		} catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Call CourseController to get all the courses with semester equals to 3, year equals to 2012 and belong to organization with id equals to 2
	 * @return List of courses
	 */
	private static List<Course> getCoursesForAdmin(){
		List<Course> courses = new ArrayList<Course>();
		try{

			String url = "http://127.0.0.1:8888/v1/courses?semester=3&year=2012&organizationId=1&page=1&limit=2;";
			ResponseEntity<Course[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Course[].class);
			Course[] coursesArray = responseEntity.getBody();
			courses = Arrays.asList(coursesArray);
			for(Course course: courses){
				System.out.println("Course " + course.getName() + " id " + course.getId());
			}			
		} catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return courses;
	}

	
	private static List<Course> getCoursesForSuperAdmin(){
		List<Course> courses = new ArrayList<Course>();
		try{

			HttpClient client = new HttpClient();
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("superAdmin@demo-sinaptico.com","reviewer");
			client.getState().setCredentials(new AuthScope("127.0.0.1", 8888, AuthScope.ANY_REALM), credentials);
			CommonsClientHttpRequestFactory commons = new CommonsClientHttpRequestFactory(client);
			restTemplate = new RestTemplate(commons);
			
			String url = "http://127.0.0.1:8888/v1/courses?semester=3&year=2012&page=1&limit=2;";
			ResponseEntity<Course[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Course[].class);
			Course[] coursesArray = responseEntity.getBody();
			courses = Arrays.asList(coursesArray);
			for(Course course: courses){
				System.out.println("Course " + course.getName() + " id " + course.getId());
			}			
		} catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return courses;
	}

	/**
	 * Delete all the courses received as parameter
	 * @param courses courses to remove
	 */
	private static void deleteCourses(List<Course> courses){
		try{		
			for(Course course : courses){
				restTemplate.delete("http://127.0.0.1:8888/v1/courses/{courseId}", course.getId().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	
	
private static void addLecturer(Long courseId){
		
		URL url;
		try {
			url = new URL("http://127.0.0.1:8888/v1/courses/"+courseId.toString()+"/lecturers/");
			
			String userPassword = "admin@demo-sinaptico.com:reviewer";   
			String encoding = (new Base64()).encode(userPassword.getBytes());   
			URLConnection uc = url.openConnection();   
			uc.setRequestProperty("Authorization", "Basic " + encoding); 
			
			HttpURLConnection conn = (HttpURLConnection) uc;
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");
	 			
			String json = "{\"id\":null,\"email\":\"lecturerTest@demo-sinaptico.com\",\"firstname\":\"Lecturer\",\"lastname\":\"Test Json\",\"nativeSpeaker\":null,\"wasmuser\":false,\"password\":\"7ba917e4e5158c8a9ed6eda08a6ec572\",\"role_name\":[\"Admin\"],\"organization\":{\"id\":1,\"name\":\"Demo Sinaptico\",\"organizationProperties\":[],\"properties\":[]},\"username\":\"lecturerTest\",\"domain\":\"demo-sinaptico.com\",\"superAdmin\":false,\"admin\":true,\"guest\":false}";
			
			OutputStream os = conn.getOutputStream();
			os.write(json.getBytes());
			os.flush();
			
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("Failed : HTTP error code : " + conn.getResponseCode());
				System.out.println(conn.getResponseMessage());
//				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
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
				
	}


private static void addTutor(Long courseId){
	
	URL url;
	try {
		url = new URL("http://127.0.0.1:8888/v1/courses/"+courseId.toString()+"/tutors/");
		
		String userPassword = "admin@demo-sinaptico.com:reviewer";   
		String encoding = (new Base64()).encode(userPassword.getBytes());   
		URLConnection uc = url.openConnection();   
		uc.setRequestProperty("Authorization", "Basic " + encoding); 
		
		HttpURLConnection conn = (HttpURLConnection) uc;
		conn.setDoOutput(true);
		conn.setRequestMethod("PUT");
		conn.setRequestProperty("Content-Type", "application/json");
 			
		String json = "{\"id\":null,\"email\":\"tutorTest@demo-sinaptico.com\",\"firstname\":\"Lecturer\",\"lastname\":\"Test Json\",\"nativeSpeaker\":null,\"wasmuser\":false,\"password\":\"7ba917e4e5158c8a9ed6eda08a6ec572\",\"role_name\":[\"Admin\"],\"organization\":{\"id\":1,\"name\":\"Demo Sinaptico\",\"organizationProperties\":[],\"properties\":[]},\"username\":\"tutorTest\",\"domain\":\"demo-sinaptico.com\",\"superAdmin\":false,\"admin\":true,\"guest\":false}";
		
		OutputStream os = conn.getOutputStream();
		os.write(json.getBytes());
		os.flush();
		
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			System.out.println("Failed : HTTP error code : " + conn.getResponseCode());
			System.out.println(conn.getResponseMessage());
//			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
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
			
}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("/******************* START **********************/");
		initializeRestTemplate();
		saveCourse();
		List<Course> coursesForAdmin = getCoursesForAdmin();
		List<Course> coursesForSuperAdmin = getCoursesForSuperAdmin();
//		if (courses.size() > 0){
//			getCourse(courses.get(0).getId());
//			addLecturer(courses.get(0).getId());
//			addTutor(courses.get(0).getId());
//		}
//		deleteCourses(courses);
		System.out.println("/******************* END **********************/");
	}

}

