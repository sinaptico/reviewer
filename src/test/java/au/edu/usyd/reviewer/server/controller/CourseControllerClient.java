package au.edu.usyd.reviewer.server.controller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import au.edu.usyd.reviewer.client.core.Course;

public class CourseControllerClient extends ControllerClient {

	
	/**
	 * Call CourseController to save two courses 
	 */
	private static void saveCourses(){
		try{
			
			Course course1 = new Course();
			course1.setName("Rest Example Course 1");
			course1.setSemester(3);
			course1.setYear(2012);
			// tutorials
			Set<String> tutorials = new HashSet<String>();
			tutorials.add("mon");
			course1.setTutorials(tutorials);
			course1.setDomainName("test.controller.com");
			
			// Use put method
			restTemplate.put("http://127.0.0.1:8888/v1/Course/", course1);
			
			Course course2 = new Course();
			course2.setName("Rest Example Course 2");
			course2.setSemester(3);
			course2.setYear(2012);
			tutorials = new HashSet<String>();
			tutorials.add("fri");
			course2.setTutorials(tutorials);
			restTemplate.put("http://127.0.0.1:8888/v1/Course/", course2);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	

	/**
	 * Call CourseController to get the course with id equals to courseId
	 * @param course Course to get
	 */
	private static void getCourse(Long courseId){
		try{
			if (courseId != null){
				String url = "http://127.0.0.1:8888/v1/Course/" + courseId.toString();
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
	private static List<Course> getCourses(){
		List<Course> courses = new ArrayList<Course>();
		try{

			String url = "http://127.0.0.1:8888/v1/Course/3/2012/2";
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
				restTemplate.delete("http://127.0.0.1:8888/v1/Course/{courseId}", course.getId().toString());
			}
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
		saveCourses();
		List<Course> courses = getCourses();
		if (courses.size() > 0){
			getCourse(courses.get(0).getId());
		}
		deleteCourses(courses);
	}

}

