package au.edu.usyd.reviewer.server.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.OrganizationProperty;
import au.edu.usyd.reviewer.client.core.ReviewerProperty;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

/**
 * Controller for organizations. It has to retrieve, create, update and delete organizations  
 * @author mdagraca
 */

@Controller
@RequestMapping("/")

public class OrganizationController extends ReviewerController {

//	/**
//	 * Return all the organizations
//	 * @param request HttpServletRequest used to initialize the controller
//	 * @return List<Organization> list of organizations
//	 * @throws MessageException message to the user
//	 */
//	@RequestMapping(value="organizations", method = RequestMethod.GET)
//	public @ResponseBody  List<Organization> getOrganizations(HttpServletRequest request) throws MessageException {
//		List<Organization> organizations = new ArrayList<Organization>();
//		try{
//			initialize(request);
//			if (isSuperAdmin()){
//				organizations = organizationDao.getOrganizations();
//				for(Organization anOrganization: organizations){
//					if(anOrganization != null){
//						anOrganization.setOrganizationProperties(new HashSet<OrganizationProperty>());
//					}
//				}
//			} else {
//				throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
//			}
//		} catch( Exception e){
//			if (e instanceof MessageException){
//				throw (MessageException) e;
//			} else {
//				e.printStackTrace();
//				throw new MessageException(Constants.EXCEPTION_GET_ORGANIZATIONS);
//			}
//		}
//		return organizations;
//	}
//	
//	
//	/** 
//	 * Return all the organization which name starts with the name parameter
//	 * @param request HttpServletRequest used to initialize the controller
//	 * @param name name of the organization 
//	 * @return List<Organization> list of organizations
//	 * @throws MessageException message to the user
//	 */
//	@RequestMapping(value="organizations/{name}", method = RequestMethod.GET)
//	public @ResponseBody  List<Organization> getOrganizations(HttpServletRequest request, @PathVariable String name) throws MessageException {
//		List<Organization> organizations = new ArrayList<Organization>();
//		try{
//			initialize(request);
//			if (isSuperAdmin()){
//				organizations = organizationDao.getOrganizations();
//				for(Organization anOrganization: organizations){
//					if(anOrganization != null){
//						anOrganization.setOrganizationProperties(new HashSet<OrganizationProperty>());
//					}
//				}
//			} else {
//				throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
//			}
//		} catch( Exception e){
//			if (e instanceof MessageException){
//				throw (MessageException) e;
//			} else {
//				e.printStackTrace();
//				throw new MessageException(Constants.EXCEPTION_GET_ORGANIZATIONS);
//			}
//		}
//		return organizations;
//	}
//
//	/**
//	 * Return the organization properties of the organization with id equals to organizationId
//	 * @param request HttpServletRequest used to initialize the controller
//	 * @param organizationId id of the organization owner of the properties
//	 * @return List<OrganizationProperty> list of organization properties
//	 * @throws MessageException message to the user
//	 */
//	@RequestMapping(value="organizations/{organizationId}/properties", method = RequestMethod.GET)
//	public @ResponseBody  List<OrganizationProperty> getOrganizationProperties(HttpServletRequest request, @PathVariable Long organizationId) throws MessageException {
//		List<OrganizationProperty> properties = new ArrayList<OrganizationProperty>();
//		try{
//			initialize(request);
//			if (super.isAdminOrSuperAdminOrGuest()){
//				if (organizationId != null) {
//					Organization organization = organizationDao.load(organizationId);
//					if (organization != null){
//						for(OrganizationProperty property : organization.getOrganizationProperties()){
//							if (property != null){
//								property.setOrganization(null);
//								ReviewerProperty reviewerProperty = property.getProperty();
//								reviewerProperty.setOrganizationProperties(new HashSet<OrganizationProperty>());
//								property.setProperty(reviewerProperty);
//								properties.add(property);
//							}
//						}
//					} else {
//						throw new MessageException(Constants.EXCEPTION_ORGANIZATION_NOT_FOUND);
//					}
//				} else {
//					throw new MessageException(Constants.EXCEPTION_ORGANIZATION_NOT_FOUND);
//				}
//			} else {
//				throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
//			}
//		} catch( Exception e){
//			if (e instanceof MessageException){
//				throw (MessageException) e;
//			} else {
//				e.printStackTrace();
//				throw new MessageException(Constants.EXCEPTION_GET_ORGANIZATION_PROPERTIES);
//			}
//		}
//		return properties;
//	}
//	
//	
//	/**
//	 *  thies method return the organization with id equals to organizationId
//	 * @param request HttpServletRequest used to initialize the controller
//	 * @param organizationId id of the organization to return
//	 * @return Organization organization with id equals to organizationId
//	 * @throws MessageException message to the user
//	 */
//	@RequestMapping(value="organizations/{organizationId}", method = RequestMethod.GET)
//	public @ResponseBody  Organization getOrganizations(HttpServletRequest request, @PathVariable Long organizationId) throws MessageException {
//		Organization anOrganization = null;
//		try{
//			initialize(request);
//			if (isSuperAdmin()){
//				if (organizationId != null) {
//					anOrganization = organizationDao.load(organizationId);
//					anOrganization.setOrganizationProperties(new HashSet<OrganizationProperty>());
//					if (anOrganization == null) {
//						throw new MessageException(Constants.EXCEPTION_ORGANIZATION_NOT_FOUND);
//					}
//				} else {
//					throw new MessageException(Constants.EXCEPTION_ORGANIZATION_NOT_FOUND);
//				}
//			} else {
//				throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
//			}
//		} catch( Exception e){
//			if (e instanceof MessageException){
//				throw (MessageException) e;
//			} else {
//				e.printStackTrace();
//				throw new MessageException(Constants.EXCEPTION_GET_ORGANIZATION);
//			}
//		}
//		return anOrganization;
//	}
}
