package au.edu.usyd.reviewer.server.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.Grade;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

/**
 * Controller for reviews. It has methods to save and submit reviews  
 * @author mdagraca
 */

@Controller
@RequestMapping("/")
public class ReviewController extends ReviewerController {

//	@RequestMapping(value="activities/{id}/reviews", method=RequestMethod.PUT)
//	public @ResponseBody submitGrades(HttpServletRequest request,
//						 @PathVariable Long id, @RequestBody Grade[] grades) throws MessageException {
//		MessageException me = null;
//		try{
//			initialize(request);
//			if (isAdminOrSuperAdminOrGuest()) {
//				if (id == null){
//					me = new MessageException(Constants.EXCEPTION_WRITING_ACTIVITY_NOT_FOUND);
//					me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
//					throw me;
//				} else {
//					WritingActivity writingActivity = assignmentManager.loadWritingActivity(id);
//					for(Grade grade : grades) {
//						Deadline deadline = grade.getDeadline();
//						Course course = assignmentManager.loadCourseWhereDeadline(deadline);
//						if (isCourseInstructor(course)) {
//							User userGrade = grade.getUser();
//							Grade currentGrade = assignmentManager.loadGrade(deadline, userGrade);
//							if(currentGrade != null) {
//								currentGrade.setValue(grade.getValue());
//							} else {
//								currentGrade = grade;
//							}
//							assignmentManager.saveGrade(currentGrade);
//							if (writingActivity.getDeadlines().contains(deadline)){
//								writingActivity.getGrades().add(currentGrade);
//								assignmentManager.saveWritingActivity(writingActivity);
//							} else {
//								//exception wrong deadline for current activity
//							}
//						} else {
//							me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
//							me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
//							throw me;
//						}
//					}
//				}
//			} else {
//				
//			}
//		} catch( Exception e){
//			if (e instanceof MessageException){
//				me = (MessageException) e;
//			} else {
//				e.printStackTrace();
//				me = new MessageException(Constants.EXCEPTION_SUBMIT_GRADES);
//			}
//			if (me.getStatusCode() == 0){
//				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
//			}
//			throw me;
//		}
//	}
}
