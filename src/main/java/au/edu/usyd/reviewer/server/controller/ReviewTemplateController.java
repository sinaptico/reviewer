package au.edu.usyd.reviewer.server.controller;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.ReviewTemplate;
import au.edu.usyd.reviewer.server.OrganizationDao;

@Controller
@RequestMapping("/ReviewTemplate")
public class ReviewTemplateController extends au.edu.usyd.reviewer.server.controller.Controller{

	
	@RequestMapping(value="/saveReviewTemplate", method = RequestMethod.PUT)
	public @ResponseBody ReviewTemplate saveReviewTemplate(HttpServletRequest request,ReviewTemplate reviewTemplate) throws Exception {
		initialize(request);
		if (isAdminOrSuperAdmin()) {
			try {
				// Before save the review template, set its organization
				if (reviewTemplate.getOrganization() == null){
					reviewTemplate.setOrganization(organization);
				}
				return assignmentManager.saveReviewTemplate(reviewTemplate);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			throw new Exception("Permission denied");
		}
	}


	@RequestMapping(value="/getReviewTemplates/{organizationId}", method = RequestMethod.GET)
	public @ResponseBody Collection<ReviewTemplate> getReviewTemplates(HttpServletRequest request, @PathVariable Long organizationId) throws Exception {
		initialize(request);
		
		Collection<ReviewTemplate> reviewTemplates = new ArrayList<ReviewTemplate>();
		/*
		 * If logged user is not teacher o manager then permission denied
		 * If logged user is manager and his/her organization is equal to the organization received as 
		 * parameter then use it to obtain the templates otherwise 
		 * if the organization received as parameter is not null, obtain the organization details and use it
		 * to get the templates.
		 */
		if (isAdminOrSuperAdmin()) {
			Organization organizationSelected = null;
			if (organizationId == null || (isSuperAdmin() && user.getOrganization().equals(organizationId) )){
				organizationSelected = organization;
			} else if (organizationId != null ){
				OrganizationDao organizationDao = OrganizationDao.getInstance();
				organizationSelected = organizationDao.load(organizationId);
			} 
			if (organizationSelected != null){
				reviewTemplates = assignmentDao.loadReviewTemplates(organizationSelected);
			}
		} 
		return reviewTemplates;
	}
	
	
	@RequestMapping(value="/", method = RequestMethod.DELETE)
	public @ResponseBody ReviewTemplate deleteReviewTemplate(HttpServletRequest request,ReviewTemplate reviewTemplate) throws Exception {
		initialize(request);
		if (isAdminOrSuperAdmin()) {
			try {
				assignmentManager.deleteReviewTemplate(reviewTemplate);
				return reviewTemplate;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			throw new Exception("Permission denied");
		}
	}

}
