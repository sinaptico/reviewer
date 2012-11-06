package au.edu.usyd.reviewer.server.controller;

import java.util.ArrayList;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
			
			ReviewTemplate reviewTemplate1 = new ReviewTemplate();
			reviewTemplate1.setDescription("Description Review Template for Test 1");
			reviewTemplate1.setName("Review Template 1");
			
			// Sections
			List<Section> sections = new ArrayList<Section>();
			Section section1 = new Section();
			section1.setNumber(1);
			section1.setText("Text for section 1");
			section1.setTool("structure");
			section1.setType(0);
			sections.add(section1);
			
			Section section2 = new Section();
			section2.setNumber(2);
			section2.setText("Text for section 2");
			section2.setTool("flow");
			section2.setType(1);
			List<Choice> choices = new ArrayList<Choice>();
			Choice choice1 = new Choice();
			choice1.setNumber(1);
			choice1.setText("Option 1 for section 2");
			choices.add(choice1);
			Choice choice2 = new Choice();
			choice2.setNumber(2);
			choice2.setText("Option 2 for section 2");
			choices.add(choice2);
			section2.setChoices(choices);
			sections.add(section2);
			
			Section section3 = new Section();
			section3.setNumber(3);
			section3.setText("Text for section 2");
			section3.setTool("participation");
			section3.setType(2);
			List<Choice> choices1 = new ArrayList<Choice>();
			Choice choice3 = new Choice();
			choice3.setNumber(1);
			choice3.setText("Scale Choice #1");
			choices1.add(choice3);
			Choice choice4 = new Choice();
			choice4.setNumber(2);
			choice4.setText("Scale Choice #2");
			choices1.add(choice4);
			Choice choice5 = new Choice();
			choice5.setNumber(3);
			choice5.setText("Scale Choice #3");
			choices1.add(choice5);
			section3.setChoices(choices1);
			sections.add(section3);
			reviewTemplate1.setSections(sections);
			
			// Use put method
			restTemplate.put("http://127.0.0.1:8888/v1/ReviewTemplate/", reviewTemplate1);
				
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
	
	/**
	 * Call ReviewTemplateController to get all the ReviewTemplates belong to organization with id equals to 2
	 * @return List of ReviewTemplates
	 */
	private static List<ReviewTemplate> getReviewTemplates(){
		List<ReviewTemplate> ReviewTemplates = new ArrayList<ReviewTemplate>();
		try{

			String url = "http://127.0.0.1:8888/v1/ReviewTemplate/2";
			ResponseEntity<ReviewTemplate[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, ReviewTemplate[].class);
			ReviewTemplate[] reviewTemplatesArray = responseEntity.getBody();
			ReviewTemplates = Arrays.asList(reviewTemplatesArray);
			for(ReviewTemplate reviewTemplate: ReviewTemplates){
				System.out.println("ReviewTemplate " + reviewTemplate.getName() + " id " + reviewTemplate.getId());
			}			
		} catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return ReviewTemplates;
	}
	
	/**
	 * Delete all the review templates received as parameter
	 * @param reviewTemplates review templates to remove
	 */
	private static void deleteReviewTemplates(List<ReviewTemplate> reviewTemplates){
		try{		
			for(ReviewTemplate reviewTemplate : reviewTemplates){
				restTemplate.delete("http://127.0.0.1:8888/v1/ReviewTemplate/{reviewTemplateId}", reviewTemplate.getId().toString());
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
		saveReviewTemplates();
		List<ReviewTemplate> reviewTemplates = getReviewTemplates();
		deleteReviewTemplates(reviewTemplates);
	}
}
