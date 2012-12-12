package au.edu.usyd.reviewer.server.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.ReviewTemplate;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.OrganizationDao;
import au.edu.usyd.reviewer.server.util.ObjectConverter;

@Controller
@RequestMapping("/")
public class ReviewTemplateController extends ReviewerController{


	/**
	 * It returns the review template with id equals to {id}
	 * @param request HttpServletRequest used to initialize the controller
	 * @param id long id of the review template to look for
	 * @param include if it's equal to all then return all the relationships of the review template
	 * @param relationships the relationships can be sections and organization
	 * @return Map with review template information
	 * @throws MessageException message to the logged user
	 */
	@RequestMapping(value="reviewtemplates/{id}", method = RequestMethod.GET)
	public @ResponseBody Map getReviewTemplate(HttpServletRequest request, 
												@PathVariable Long id,
												@RequestParam(value="include", required=false) String include, 
												@RequestParam(value="relationships", required=false) String relationships) throws MessageException {
				
		MessageException me = null;
		try{
			initialize(request);
			if (id == null){
				me = new MessageException(Constants.EXCEPTION_REVIEW_TEMPLATE_NOT_FOUND);
				me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
				throw me;
			} else {
				if ( isSuperAdmin() || (isAdmin() && organization.getId().equals(id))){
					ReviewTemplate reviewTemplate = assignmentManager.loadReviewTemplate(id);
					if (reviewTemplate != null) {
						Map reviewTemplateMap = ObjectConverter.convertObjectInMap(reviewTemplate, include,relationships,0);
						return reviewTemplateMap;
					} else {
						me = new MessageException(Constants.EXCEPTION_REVIEW_TEMPLATE_NOT_FOUND);
						me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
						throw me;					
					}
				} else {
					me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
					me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
					throw me;
				}
			}
		} catch( Exception e){
			e.printStackTrace();
			if (e instanceof MessageException){
				me = (MessageException)e;
			} else {
				me = new MessageException(Constants.EXCEPTION_GET_REVIEW_TEMPLATE);; 
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}
	
		
	/**
	 * Delete the review template with id equals to reviewTemplateId
	 * @param request HttpServletRequest to initialize the controller
	 * @param id Long id of the review template to delete
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="reviewtemplates/{id}", method = RequestMethod.DELETE)
	public @ResponseBody void deleteReviewTemplate(HttpServletRequest request,@PathVariable Long id) throws MessageException {
		MessageException me = null;
		try{
			initialize(request);
			if (id == null){
				me = new MessageException(Constants.EXCEPTION_REVIEW_TEMPLATE_NOT_FOUND);
				me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
				throw me;
			} else {
				if ( isSuperAdmin() || (isAdmin() && organization.getId().equals(id))){
					ReviewTemplate reviewTemplate = assignmentManager.loadReviewTemplate(id);
					if (reviewTemplate != null) {
						assignmentManager.deleteReviewTemplate(reviewTemplate);
					} else {
						me = new MessageException(Constants.EXCEPTION_REVIEW_TEMPLATE_NOT_FOUND);
						me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
						throw me;
					}
				} else {
					me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
					me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
					throw me;
				}
			}
		} catch(Exception e){
			e.printStackTrace();
			if (e instanceof MessageException){
				me = (MessageException)e;
			} else {
				me = new MessageException(Constants.EXCEPTION_DELETE_REVIEW_TEMPLATE);; 
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}

	/**
	 * It returns the review template with id equals to {id}
	 * @param request HttpServletRequest used to initialize the controller
	 * @param id long id of the review template to look for
	 * @param include if it's equal to all then return all the relationships of the review template
	 * @param relationships the relationships can be sections and organization
	 * @return Map with review template information
	 * @throws MessageException message to the logged user
	 */
	@RequestMapping(value="reviewtemplates/{id}/sections", method = RequestMethod.GET)
	public @ResponseBody List<Object> getReviewTemplateSections(HttpServletRequest request, 
												@PathVariable Long id,
												@RequestParam(value="include", required=false) String include, 
												@RequestParam(value="relationships", required=false) String relationships) throws MessageException {
				
		MessageException me = null;
		try{
			initialize(request);
			if (id == null){
				me = new MessageException(Constants.EXCEPTION_REVIEW_TEMPLATE_NOT_FOUND);
				me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
				throw me;
			} else {
				if ( isSuperAdmin() || (isAdmin() && organization.getId().equals(id))){
					ReviewTemplate reviewTemplate = assignmentManager.loadReviewTemplate(id);
					if (reviewTemplate != null) {
						List<Object> sections = ObjectConverter.convertCollectiontInList(reviewTemplate.getSections(), include, relationships, 0);
						return sections;
					} else {
						me = new MessageException(Constants.EXCEPTION_REVIEW_TEMPLATE_NOT_FOUND);
						me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
						throw me;					
					}
				} else {
					me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
					me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
					throw me;
				}
			}
		} catch( Exception e){
			e.printStackTrace();
			if (e instanceof MessageException){
				me = (MessageException)e;
			} else {
				me = new MessageException(Constants.EXCEPTION_GET_REVIEW_TEMPLATE);; 
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}

	/**
	 * This method creates or update the review template received as parameter into the database
	 * @param request HttpServletRequest to initialize the controller
	 * @param reviewTemplate review template to save
	 * @return ReviewTemplate saved or updated review template
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="/reviewtemplates", method = RequestMethod.PUT)
	public @ResponseBody Map<String,Object> saveReviewTemplate(HttpServletRequest request, @RequestBody ReviewTemplate reviewTemplate) throws MessageException {
		MessageException me = null;
		try{
			initialize(request);
			
			if (isAdminOrSuperAdmin() ){
				reviewTemplate = assignmentManager.loadReviewTemplateRelationships(reviewTemplate,organization);
				if (isAdmin() && !organization.getId().equals(reviewTemplate.getOrganization().getId())){
					me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
					me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
					throw me;
				} else {
					// Before save the course set its organization
					reviewTemplate = assignmentManager.saveReviewTemplate(reviewTemplate);
					Map<String,Object> reviewTemplateMap = ObjectConverter.convertObjectInMap(reviewTemplate, "", "",0);
					return reviewTemplateMap;
				}
			} else {
				me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
				me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
				throw me;
			}
		}catch(Exception e){
			e.printStackTrace();
			if (e instanceof MessageException){
				me = (MessageException)e;
			} else {
				me = new MessageException(Constants.EXCEPTION_SAVE_REVIEW_TEMPLATE);; 
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}



}
