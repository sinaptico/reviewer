package au.edu.usyd.reviewer.server.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.util.CalendarUtil;

@Controller
@RequestMapping("/")
public class UtilController extends ReviewerController {

	/**
	 * This method returns a list of years. The current year and 5 years before
	 * @return List<Integer> list of years
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="/years", method = RequestMethod.GET)
	public @ResponseBody Collection<Integer> getYears(HttpServletRequest request) throws MessageException{
		try{
			initialize(request);
			if (super.isAdminOrSuperAdminOrGuest()){
				return CalendarUtil.getYears();
			} else {
				throw new MessageException( Constants.EXCEPTION_PERMISSION_DENIED);
			}
		} catch( Exception e){
			if (e instanceof MessageException){
				throw (MessageException) e;
			} else {
				e.printStackTrace();
				throw new MessageException(Constants.EXCEPTION_GET_YEARS);
			}
		}
	}	
}
