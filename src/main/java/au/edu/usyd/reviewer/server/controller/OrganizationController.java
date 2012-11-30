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
import au.edu.usyd.reviewer.client.core.ReviewerProperty;
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
	public @ResponseBody  Map<String,Object> getOrganizations(HttpServletRequest request, @PathVariable Long id,
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
}
