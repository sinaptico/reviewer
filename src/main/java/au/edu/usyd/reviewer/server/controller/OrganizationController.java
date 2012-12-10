package au.edu.usyd.reviewer.server.controller;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.OrganizationProperty;
import au.edu.usyd.reviewer.client.core.ReviewTemplate;
import au.edu.usyd.reviewer.client.core.ReviewerProperty;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.util.ObjectConverter;

/**
 * Controller for organizations. It has to retrieve, create, update and delete organizations  
 * @author mdagraca
 */

@Controller
@RequestMapping("/")

public class OrganizationController extends ReviewerController {

	
	
	/**
	 * Return all the organizations
	 * @param request HttpServletRequest used to initialize the controller
	 * @return List<Organization> list of organizations
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="organizations", method = RequestMethod.GET)
	public @ResponseBody  List getOrganizations(HttpServletRequest request, 
			@RequestParam(value="page", required=false) Integer page,
			@RequestParam(value="limit", required=false) Integer limit, 
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="include", required=false) String include) throws MessageException {
		MessageException me = null;
		try{
			initialize(request);
			if (isSuperAdmin()){
				List<Organization> organizations = organizationManager.getOrganizations(page,limit,name);
				for(Organization anOrganization: organizations){
					for(OrganizationProperty organizationProperty : anOrganization.getOrganizationProperties()){
						if (organizationProperty != null){
							organizationProperty.setOrganization(null);
							ReviewerProperty reviewerProperty = organizationProperty.getProperty();
							reviewerProperty.setOrganizationProperties(new HashSet<OrganizationProperty>());
						}
					}
				}
				List organizationsList = ObjectConverter.convertCollectiontInList(organizations, include,"",0);
				return organizationsList;
			} else {
				me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
				me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
				throw me;
			}
		} catch( Exception e){
			if (e instanceof MessageException){
				me = (MessageException) e;
			} else {
				e.printStackTrace();
				me = new MessageException(Constants.EXCEPTION_GET_ORGANIZATIONS);
			}
			if (me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}
	
	
	
	/**
	 * This method return the organization with id equals to id. 
	 * @param request HttpServletRequest used to initialize the controller
	 * @param id id of the organization to return
	 * @param String included if included is equals to all returns all then the organization will include all its properties
	 * @return Organization organization with id equals to id
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="organizations/{id}", method = RequestMethod.GET)
	public @ResponseBody  Map<String,Object> getOrganization(HttpServletRequest request, @PathVariable Long id,
		   @RequestParam(value="include", required=false) String include) throws MessageException {
		Organization anOrganization = null;
		MessageException me = null;
		Map<String,Object> organizationMap = new HashMap<String,Object>();
		try{
			initialize(request);
			if (isSuperAdmin()){
				if (id != null) {
					anOrganization = organizationManager.getOrganization(id);
					if (anOrganization == null) {
						me = new MessageException(Constants.EXCEPTION_ORGANIZATION_NOT_FOUND);
						me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
						throw me;
					} 
				}
			} else if (isGuestOrAdmin() && organization.getId().equals(id)){
				anOrganization = organization.clone();
			} else {
				me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
				me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
				throw me;
			}
			
			for(OrganizationProperty organizationProperty : anOrganization.getOrganizationProperties()){
				if (organizationProperty != null){
					organizationProperty.setOrganization(null);
					ReviewerProperty reviewerProperty = organizationProperty.getProperty();
					reviewerProperty.setOrganizationProperties(new HashSet<OrganizationProperty>());
				}
			}
			organizationMap = ObjectConverter.convertObjectInMap(anOrganization,include,"",0);

		} catch( Exception e){
			if (e instanceof MessageException){
				me = (MessageException) e;
			} else {
				e.printStackTrace();
				me = new MessageException(Constants.EXCEPTION_GET_ORGANIZATION);
			}
			if (me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
		return organizationMap;
	}
	
	/**
	 * This method create or update the organization received as parameter into the database
	 * @param request HttpServletRequest used to initialize the controller 
	 * @param anOrganization organization to save
	 * @return Organization saved
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="organizations",  method = RequestMethod.PUT)
	public @ResponseBody Map<String,Object> saveCourse(HttpServletRequest request, @RequestBody Organization anOrganization) throws MessageException {
		MessageException me = null;
		try{
			initialize(request);
			if (isSuperAdmin()){
				anOrganization = organizationManager.saveOrganization(anOrganization, false);
				Map<String,Object> organizationMap = ObjectConverter.convertObjectInMap(anOrganization,"","",0);
				return organizationMap;
			} else {
				me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
				me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
				throw me;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof MessageException){
				me = (MessageException) e;
			} else {
				me = new MessageException(Constants.EXCEPTION_SAVE_ORGANIZATION);
			}
			if (me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}
	
	
	/**
	 * Delete the organization received as parameter from the database
	 * @param request HttpServletRequest to initialize the controller
	 * @param Long id of the organization to delete
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="organizations/{id}", method = RequestMethod.DELETE)
	public @ResponseBody void deleteOrganization(HttpServletRequest request,@PathVariable Long id) throws MessageException {
		MessageException me = null;
		try{
			initialize(request);
			if (isSuperAdmin()) {
				if (id == null){
					me = new MessageException(Constants.EXCEPTION_ORGANIZATION_NOT_FOUND);
					me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
					throw me;
				} else {
					Organization anOrganization = organizationManager.getOrganization(id);
					if (anOrganization != null){
						organizationManager.deleteOrganization(anOrganization);
					} else {
						me = new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
						me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
						throw me;
					}
				}
			} else {
				me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
				me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
				throw me;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof MessageException){
				me = (MessageException) e;
			} else {
				me = new MessageException(Constants.EXCEPTION_DELETE_COURSE);
			}
			if (me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}
	
	/**
	 * This method returns the users belong to the organization with id equals to {id}
	 * @param request HttpServletRequest to initialize the controller 
	 * @param id Long id of the organization
	 * @param page page to show, used in pagination
	 * @param limit quantity of users per page
	 * @param include if it's equals to all returns all the relationships of the users
	 * @param roles can be lecturers, students, tutors or all. It's used to indicate the role of the returned users
	 * @param relationships indicates which relationships or the user must be included 
	 * @param assigned used to indicate that the method must return only the users assigned to a course
	 * @return List of users
	 * @throws MessageException message to the logged user
	 */
	@RequestMapping(value="organizations/{id}/users", method = RequestMethod.GET)
	public @ResponseBody  List<Object> getOrganizationUsers(HttpServletRequest request, @PathVariable Long id,
												@RequestParam(value="page", required=false) Integer page,
												@RequestParam(value="limit", required=false) Integer limit,
												@RequestParam(value="include", required=false) String include,
												@RequestParam(value="roles", required=false) String roles,
												@RequestParam(value="relationships", required=false) String relationships,
												@RequestParam(value="assigned", required=false) boolean assigned) throws MessageException {
		Organization anOrganization = null;
		MessageException me = null;
		List<Object> usersList = new ArrayList<Object>();
		try{
			initialize(request);
			if (isSuperAdmin() ){
				if (id != null) {
					anOrganization = organizationManager.getOrganization(id);
					if (anOrganization == null) {
						me = new MessageException(Constants.EXCEPTION_ORGANIZATION_NOT_FOUND);
						me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
						throw me;
					} 
				}
			} else if (isAdmin() && organization.getId().equals(id)){
				anOrganization = organization.clone();
			} else {
				me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
				me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
				throw me;
			}
			List<User> users = organizationManager.getUsers(anOrganization, page, limit, roles,assigned);
			usersList = ObjectConverter.convertCollectiontInList(users, include, relationships, 0);
			return usersList;
		} catch( Exception e){
			if (e instanceof MessageException){
				me = (MessageException) e;
			} else {
				e.printStackTrace();
				me = new MessageException(Constants.EXCEPTION_GET_ORGANIZATION);
			}
			if (me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}
	
	/**
	 * This method returns a list of review template belong to the organization whose id is equals to the organizationId received as paramter
	 * @param request HttpServletRequest to initialize the controller
	 * @param organizationId id of the organization which the review template belong to
	 * @return List<ReviewTemplate> list of review template
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="organizations/{id}/templates", method = RequestMethod.GET)
	public @ResponseBody List<Object> getReviewTemplates(HttpServletRequest request, @PathVariable Long id,
															@RequestParam(value="page", required=false) Integer page,
															@RequestParam(value="limit", required=false) Integer limit,
															@RequestParam(value="include", required=false) String include,
															@RequestParam(value="relationships", required=false) String relationships
														) throws MessageException {
		
		MessageException me = null;
		try{
			initialize(request);	
			Organization organizationSelected = null;
			if (id == null) {
				me = new MessageException(Constants.EXCEPTION_ORGANIZATION_NOT_FOUND);
				me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
				throw me;	
			} else {
				if ( isSuperAdmin() || (isAdmin() && organization.getId().equals(id))){
					if (organization.getId().equals(id)){
						organizationSelected = organization.clone();
					} else {
						organizationSelected = organizationManager.getOrganization(id);
					}
					if (organizationSelected != null){
						List<ReviewTemplate> reviewTemplates = new ArrayList<ReviewTemplate>();
						reviewTemplates =  assignmentManager.loadReviewTemplates(organizationSelected, page, limit);
						List<Object> reviewTemplatesList = ObjectConverter.convertCollectiontInList(reviewTemplates, include, relationships, 0);
						return reviewTemplatesList;
					} else {
						me = new MessageException(Constants.EXCEPTION_ORGANIZATION_NOT_FOUND);
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
				throw (MessageException) e;
			} else {

				throw new MessageException(Constants.EXCEPTION_GET_REVIEW_TEMPLATES);
			}
		}
	}

}
