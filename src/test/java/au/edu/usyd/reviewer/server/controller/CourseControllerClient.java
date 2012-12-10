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
	 			
			String json = "[{\"id\":4,\"username\":\"testAPI\",\"email\":\"lecturer@demo-sinaptico.com\",\"wasmuser\":false,\"Organization\":{\"id\":1},\"lastname\":\"For Demo Sinaptico\",\"firstname\":\"Lecturer\",\"role_name\":[\"Admin\"],\"password\":\"7ba917e4e5158c8a9ed6eda08a6ec572\"}]";
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
 			
		String json = "[{\"id\":5,\"username\":\"testAPI\",\"email\":\"tutor@demo-sinaptico.com\",\"wasmuser\":false,\"Organization\":{\"id\":1},\"lastname\":\"For Demo Sinaptico\",\"firstname\":\"Tutor\",\"role_name\":[\"Admin\"],\"password\":\"7ba917e4e5158c8a9ed6eda08a6ec572\"}]";

		
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


private static void addStudent(Long courseId){
	
	URL url;
	try {
		url = new URL("http://127.0.0.1:8888/v1/courses/"+courseId.toString()+"/students/");
		
		String userPassword = "admin@demo-sinaptico.com:reviewer";   
		String encoding = (new Base64()).encode(userPassword.getBytes());   
		URLConnection uc = url.openConnection();   
		uc.setRequestProperty("Authorization", "Basic " + encoding); 
		
		HttpURLConnection conn = (HttpURLConnection) uc;
		conn.setDoOutput(true);
		conn.setRequestMethod("PUT");
		conn.setRequestProperty("Content-Type", "application/json");
 			
		String json = "[{\"id\":11,\"username\":\"testAPI\",\"email\":\"student2@demo-sinaptico.com\",\"wasmuser\":false,\"Organization\":{\"id\":1},\"lastname\":\"For Sinaptico\",\"firstname\":\"student2\",\"role_name\":[\"Admin\"],\"password\":\"7ba917e4e5158c8a9ed6eda08a6ec572\"}]";


		
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
			// create course
//			String json ="{\"name\":\"COURSE TEST 666\",\"year\":2012,\"semester\":3,\"folderId\":null,\"templatesFolderId\":null,\"spreadsheetId\":null,\"domainName\":\"smart-sourcing.com.ar\",\"tutorials\":[\"mon\"],\"lecturers\":[{\"id\":9}],\"tutors\":[],\"supervisors\":[],\"studentGroups\":[],\"writingActivities\":[],\"templates\":[],\"automaticReviewers\":[],\"organization\":{\"id\":1}}";
//			String userPassword = "admin@smart-sourcing.com.ar:reviewer";
////			save(new URL("http://127.0.0.1:8888/v1/courses/"), json, userPassword);
						
			// update course
//			json ="{\"id\":48,\"name\":\"COURSE TEST MODIFIED 130\",\"year\":2012,\"semester\":2,\"folderId\":null,\"templatesFolderId\":null,\"spreadsheetId\":null,\"domainName\":\"smart-sourcing.com.ar\",\"tutorials\":[\"mon\"],\"lecturers\":[{\"id\":9}],\"tutors\":[],\"supervisors\":[],\"studentGroups\":[{\"id\":1}],\"writingActivities\":[{\"id\":28}],\"templates\":[],\"automaticReviewers\":[],\"organization\":{\"id\":1}}";
//			userPassword = "admin@smart-sourcing.com.ar:reviewer";
//			save(new URL("http://127.0.0.1:8888/v1/courses/"), json, userPassword);
	
			addLecturer(new Long(27));
			addTutor(new Long(27));
//			addStudent(new Long(27));
//			userPassword = "superAdmin@demo-sinaptico.com:reviewer";
			// add lecturer
//			json="{\"username\":\"test\",\"email\":\"test@demo-sinaptico.com\",\"wasmuser\":false,\"Organization\":1,\"lastname\":\"Lecturer for API\",\"firstname\":\"test\",\"role_name\":[\"Admin\"],\"password\":null,\"nativeSpeaker\":null}";
//			save(new URL("http://127.0.0.1:8888/v1/courses/27/lecturers"), json, userPassword);
			
//			List<Course> coursesForAdmin = getCoursesForAdmin();
//			List<Course> coursesForSuperAdmin = getCoursesForSuperAdmin();
//			if (coursesForAdmin.size() > 0){
//				getCourse(coursesForAdmin.get(0).getId());
	//			addLecturer(coursesForAdmin.get(0).getId());
	//			addTutor(coursesForAdmin.get(0).getId());
//			}
//			getCourse(new Long(1));
	//		deleteCourses(courses);
			System.out.println("/******************* END **********************/");
		} catch(Exception e){
			e.printStackTrace();
		}
	}

}

