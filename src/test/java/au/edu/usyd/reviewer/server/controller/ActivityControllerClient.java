package au.edu.usyd.reviewer.server.controller;


import java.net.URL;

import org.apache.catalina.util.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import au.edu.usyd.reviewer.client.core.WritingActivity;

public class ActivityControllerClient extends ControllerClient {
	
		private static void getWritingActivity(Long id){
			try{
				if (id != null){
					String url = "http://127.0.0.1:8888/v1/activities/" + id.toString();
					ResponseEntity<WritingActivity> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, WritingActivity.class);
					WritingActivity activity = responseEntity.getBody();
					if (activity != null){
						System.out.println("Course " + activity.getName() + " id " + activity.getId());
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
				String json="{\"genre\":\"proposal\",\"startDate\":\"2012-12-04\",\"earlySubmit\":true,\"documentType\":\"document\",\"status\":1,\"tutorial\":\"mon\",\"trackReviews\":true,\"emailStudents\":true,\"folderId\":null,\"deadlines\":[{\"status\":0,\"name\":\"Final\",\"finishDate\":\"2012-12-07\",\"maxGrade\":100.0}],\"glosserSite\":2,\"name\":\"TEST Activity\",\"showStats\":false,\"entries\":[],\"documentTemplate\":\"none\",\"excludeEmptyDocsInReviews\":true,\"grades\":[],\"groups\":false,\"reviewingActivities\":[]}";
				String userPassword = "admin@smart-sourcing.com.ar:reviewer";
				save(new URL("http://127.0.0.1:8888/v1/courses/48/activities"), json, userPassword);
				Long id = new Long(11);
//				getWritingActivity(id);
//				deleteOrganization("http://127.0.0.1:8888/v1/activities/",id);
			} catch(Exception e){
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
			System.out.println("/******************* END **********************/");
		}
}
