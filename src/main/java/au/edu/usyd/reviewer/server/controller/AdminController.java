package au.edu.usyd.reviewer.server.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.OrganizationManager;


@Controller
@RequestMapping("/Admin")
public class AdminController extends au.edu.usyd.reviewer.server.controller.Controller{

	private OrganizationManager organizationManager = OrganizationManager.getInstance();
	@RequestMapping(value="/getOrganizations/{organizationName}", method = RequestMethod.GET)
	public  @ResponseBody List<Organization> getOrganizations(HttpServletRequest request, @PathVariable String organizationName) throws Exception{
		initialize(request);
		List<Organization> organizations = new ArrayList<Organization>();
		if (super.isSuperAdmin()){
			organizations = organizationManager.getOrganizations(organizationName);
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
		return organizations;
	}
}
