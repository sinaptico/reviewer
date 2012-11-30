package au.edu.usyd.reviewer.server.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.ReviewTemplate;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.OrganizationDao;

@Controller
@RequestMapping("/")
public class ReviewTemplateController extends ReviewerController{

//	/**
//	 * This method creates or update the review template received as parameter into the database
//	 * @param request HttpServletRequest to initialize the controller
//	 * @param reviewTemplate review template to save
//	 * @return ReviewTemplate saved or updated review template
//	 * @throws MessageException message to the user
//	 */
//	@RequestMapping(value="/reviewtemplate", method = RequestMethod.PUT)
//	public @ResponseBody ReviewTemplate saveReviewTemplate(HttpServletRequest request,ReviewTemplate reviewTemplate) throws MessageException {
//		try{
//			initialize(request);
//			if (isAdminOrSuperAdmin()) {
//				// Before save the review template, set its organization
//				if (reviewTemplate.getOrganization() == null){
//					reviewTemplate.setOrganization(organization);
//				}
//				return assignmentManager.saveReviewTemplate(reviewTemplate);
//			} else {
//				throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
//			}
//		}catch(Exception e){
//			if (e instanceof MessageException){
//				throw (MessageException) e;
//			} else {
//				e.printStackTrace();
//				throw new MessageException(Constants.EXCEPTION_SAVE_REVIEW_TEMPLATE);
//			}
//		}
//	}
//
//
//	/**
//	 * This method returns a list of review template belong to the organization whose id is equals to the organizationId received as paramter
//	 * @param request HttpServletRequest to initialize the controller
//	 * @param organizationId id of the organization which the review template belong to
//	 * @return List<ReviewTemplate> list of review template
//	 * @throws MessageException message to the user
//	 */
//	@RequestMapping(value="reviewtemplates/{organizationId}", method = RequestMethod.GET)
//	public @ResponseBody List<ReviewTemplate> getReviewTemplates(HttpServletRequest request, @PathVariable Long organizationId) throws MessageException {
//		List<ReviewTemplate> reviewTemplates = new ArrayList<ReviewTemplate>();
//		try{
//			initialize(request);
//			
//			/*
//			 * If logged user is not teacher o manager then permission denied
//			 * If logged user is manager and his/her organization is equal to the organization received as 
//			 * parameter then use it to obtain the templates otherwise 
//			 * if the organization received as parameter is not null, obtain the organization details and use it
//			 * to get the templates.
//			 */
//			if (isAdminOrSuperAdmin()) {
//				Organization organizationSelected = null;
//				if (organizationId == null || (isSuperAdmin() && user.getOrganization().equals(organizationId) )){
//					organizationSelected = organization;
//				} else if (organizationId != null ){
//					OrganizationDao organizationDao = OrganizationDao.getInstance();
//					organizationSelected = organizationDao.load(organizationId);
//				} 
//				if (organizationSelected != null){
//					reviewTemplates =  assignmentDao.loadReviewTemplates(organizationSelected);
//				}
//			}
//		} catch(Exception e){
//			if (e instanceof MessageException){
//				throw (MessageException) e;
//			} else {
//				e.printStackTrace();
//				throw new MessageException(Constants.EXCEPTION_GET_REVIEW_TEMPLATES);
//			}
//		}
//		return reviewTemplates;
//	}
//	
//	/**
//	 * Delete the review template with id equals to reviewTemplateId
//	 * @param request HttpServletRequest to initialize the controller
//	 * @param reviewTemplateId id of the review template to delete
//	 * @return ReviewTemplate review template deleted
//	 * @throws MessageException message to the user
//	 */
//	@RequestMapping(value="reviewtemplate/{reviewTemplateId}", method = RequestMethod.DELETE)
//	public @ResponseBody ReviewTemplate deleteReviewTemplate(HttpServletRequest request,@PathVariable Long reviewTemplateId) throws MessageException {
//		try{
//			initialize(request);
//			if (isAdminOrSuperAdmin()) {
//				ReviewTemplate reviewTemplate  = assignmentDao.loadReviewTemplate(reviewTemplateId);
//				if (reviewTemplate != null){
//					assignmentManager.deleteReviewTemplate(reviewTemplate);
//					return reviewTemplate;
//				} else {
//					throw new MessageException(Constants.EXCEPTION_REVIEW_TEMPLATE_NOT_FOUND);					}
//			} else {
//				throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
//			}
//		} catch(Exception e){
//			if (e instanceof MessageException){
//				throw (MessageException) e;
//			} else {
//				throw new MessageException(Constants.EXCEPTION_DELETE_REVIEW_TEMPLATE);
//			}
//		}
//	}
//
//	/**
//	 * Return the review template which id is equals to reviewTemplateId
//	 * @param request HttpServletRequest to initialize the controller
//	 * @param reviewTemplateId id of the review template to get
//	 * @return ReviewTemplate review template with id equals to reviewTemplateId
//	 * @throws MessageException message to the user
//	 */
//	@RequestMapping(value="reviewtemplate/{reviewTemplateId}", method = RequestMethod.GET)
//	public @ResponseBody ReviewTemplate getReviewTemplate(HttpServletRequest request, @PathVariable Long reviewTemplateId) throws MessageException {
//		ReviewTemplate reviewTemplate = null;
//		try{
//			initialize(request);
//			if (isAdminOrSuperAdmin()) {
//				reviewTemplate = assignmentDao.loadReviewTemplate(reviewTemplateId);
//				if (reviewTemplate != null) {
//					return reviewTemplate;
//				} else {
//					throw new MessageException(Constants.EXCEPTION_REVIEW_TEMPLATE_NOT_FOUND);					
//				}
//			} else {
//				throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
//			}
//		} catch(Exception e){
//			if (e instanceof MessageException){
//				throw (MessageException) e;
//			} else {
//				e.printStackTrace();
//				throw new MessageException(Constants.EXCEPTION_GET_REVIEW_TEMPLATE);
//			}
//		}
//	}
}
