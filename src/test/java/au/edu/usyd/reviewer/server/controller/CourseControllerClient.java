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
import java.util.Map;

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
	 * Call CourseController to get the course with id equals to courseId
	 * @param course Course to get
	 */
	private static void getCourse(Long courseId){
		try{
			if (courseId != null){
				String url = "http://127.0.0.1:8888/v1/courses/" + courseId.toString();
				ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class);
				Map courseMap = responseEntity.getBody();
				if (courseMap != null){
					System.out.println("Without relationships");
					System.out.println(courseMap.toString());
				}
				
				url = "http://127.0.0.1:8888/v1/courses/" + courseId.toString()+"?include=all";
				responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class);
				courseMap = responseEntity.getBody();
				if (courseMap != null){
					System.out.println("Include All");
					System.out.println(courseMap.toString());
				}
				
				url = "http://127.0.0.1:8888/v1/courses/" + courseId.toString()+"?relationships=lecturers";
				responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class);
				courseMap = responseEntity.getBody();
				if (courseMap != null){
					System.out.println("Relationships lecturers");
					System.out.println(courseMap.toString());
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

			String url = "http://127.0.0.1:8888/v1/courses?semester=1&year=2012";
			ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class);
			Map courseMap = responseEntity.getBody();
			if (courseMap != null){
				System.out.println("Without relationships");
				System.out.println(responseEntity.getBody().toString());
			}
			
			
			url = "http://127.0.0.1:8888/v1/courses?semester=1&year=2012&include=all";
			responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class);
			courseMap = responseEntity.getBody();
			if (courseMap != null){
				System.out.println("Include All");
				System.out.println(responseEntity.getBody().toString());
			}
			
			url = "http://127.0.0.1:8888/v1/courses?semester=1&year=2012&relationships=activities";
			responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class);
			courseMap = responseEntity.getBody();
			if (courseMap != null){
				System.out.println("Relationships Activities");
				System.out.println(responseEntity.getBody().toString());
			}
			
//			ResponseEntity<Course[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Course.class);
//			Course[] coursesArray = responseEntity.getBody();
//			courses = Arrays.asList(coursesArray);
//			for(Course course: courses){
//				System.out.println("Course " + course.getName() + " id " + course.getId());
//			}			
		} catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return courses;
	}

	
//	private static List<Course> getCoursesForSuperAdmin(){
//		List<Course> courses = new ArrayList<Course>();
//		try{
//
//			HttpClient client = new HttpClient();
//			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("superAdmin@demo-sinaptico.com","reviewer");
//			client.getState().setCredentials(new AuthScope("127.0.0.1", 8888, AuthScope.ANY_REALM), credentials);
//			CommonsClientHttpRequestFactory commons = new CommonsClientHttpRequestFactory(client);
//			restTemplate = new RestTemplate(commons);
//			
//			String url = "http://127.0.0.1:8888/v1/organizations/1/courses?semester=3&year=2012&page=1&limit=2";
//			ResponseEntity<Course[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Course[].class);
//			Course[] coursesArray = responseEntity.getBody();
//			courses = Arrays.asList(coursesArray);
//			for(Course course: courses){
//				System.out.println("Course " + course.getName() + " id " + course.getId());
//			}			
//		} catch(Exception e){
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//		}
//		return courses;
//	}

	/**
	 * Delete all the courses received as parameter
	 * @param courses courses to remove
	 */
	private static void deleteCourses(List<Course> courses){
		for(Course course : courses){
			delete("http://127.0.0.1:8888/v1/courses/" + course.getId().toString());
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
//				
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
		try{
			System.out.println("/******************* START **********************/");
			initializeRestTemplate();
			String json ="{\"name\":\"COURSE TEST\",\"year\":2012,\"semester\":3,\"folderId\":null,\"templatesFolderId\":null,\"spreadsheetId\":null,\"domainName\":\"smart-sourcing.com.ar\",\"tutorials\":[],\"lecturers\":[],\"tutors\":[],\"supervisors\":[],\"studentGroups\":[],\"writingActivities\":[],\"templates\":[],\"automaticReviewers\":[],\"organization\":null}";
//			save(new URL("http://127.0.0.1:8888/v1/courses/"), json);
			List<Course> coursesForAdmin = getCoursesForAdmin();
//			List<Course> coursesForSuperAdmin = getCoursesForSuperAdmin();
//			if (coursesForAdmin.size() > 0){
//				getCourse(coursesForAdmin.get(0).getId());
	//			addLecturer(coursesForAdmin.get(0).getId());
	//			addTutor(coursesForAdmin.get(0).getId());
//			}
			getCourse(new Long(1));
	//		deleteCourses(courses);
			System.out.println("/******************* END **********************/");
		} catch(Exception e){
			e.printStackTrace();
		}
	}

}
