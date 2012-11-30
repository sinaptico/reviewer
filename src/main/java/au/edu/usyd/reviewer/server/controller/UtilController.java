package au.edu.usyd.reviewer.server.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
	public @ResponseBody List<Integer> getYears(HttpServletRequest request) throws MessageException{
		MessageException me = null;
		try{
			initialize(request);
			if (super.isAdminOrSuperAdminOrGuest()){
				return CalendarUtil.getYears();
			} else {
				me = new MessageException( Constants.EXCEPTION_PERMISSION_DENIED);
				me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
				throw me;
			}
		} catch( Exception e){
			if (e instanceof MessageException){
				me = (MessageException) e;
			} else {
				e.printStackTrace();
				me = new MessageException(Constants.EXCEPTION_GET_YEARS);
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
		    }
		    throw me;
		}
	}	
}
