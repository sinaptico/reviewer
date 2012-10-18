package au.edu.usyd.reviewer.server.controller;

import java.util.Collection;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import au.edu.usyd.reviewer.server.util.CalendarUtil;

@Controller
@RequestMapping("/")
public class UtilController extends au.edu.usyd.reviewer.server.controller.Controller {

	@RequestMapping(value="/years", method = RequestMethod.GET)
	public @ResponseBody Collection<Integer> getYears(){
		return CalendarUtil.getYears();
	}
}
