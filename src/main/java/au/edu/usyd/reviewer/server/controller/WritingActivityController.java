package au.edu.usyd.reviewer.server.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import au.edu.usyd.reviewer.client.admin.report.UserStats;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.UserGroup;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.report.UserStatsAnalyser;

/**
 * This class is the controller for all the method related with writing activities
 * @author mdagraca
 */

@Controller
@RequestMapping("/")
public class WritingActivityController extends ReviewerController {

	@RequestMapping(value="activities/{writingActivityId}", method = RequestMethod.DELETE)
	public @ResponseBody WritingActivity deleteWritingActivity(HttpServletRequest request,@PathVariable Long writingActivityId) throws MessageException {
		try{
			initialize(request);
			WritingActivity writingActivity = assignmentDao.loadWritingActivity(writingActivityId);
			if (writingActivity != null){
				if (isAdminOrSuperAdmin() || isCourseLecturer(assignmentDao.loadCourseWhereWritingActivity(writingActivity))) {
					assignmentManager.deleteActivity(writingActivity);
					return writingActivity;
				} else {
					throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
				}
			} else {
				throw new MessageException(Constants.EXCEPTION_WRITING_ACTIVITY_NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof MessageException){
				throw (MessageException) e;
			} else {
				throw new MessageException(Constants.EXCEPTION_DELETE_WRITING_ACTIVITY);
			}
		}
	}
	
	@RequestMapping(value="activities/stats/{writingActivityId}", method = RequestMethod.GET)
	public @ResponseBody Collection<UserStats> getWritingActivityStats(HttpServletRequest request, @PathVariable Long writingActivityId) throws MessageException {
		Collection<UserStats> stats = new ArrayList<UserStats>();
		try{
			initialize(request);
			WritingActivity writingActivity = assignmentDao.loadWritingActivity(writingActivityId);
			if ( writingActivity != null){
				Course course = assignmentDao.loadCourseWhereWritingActivity(writingActivity);
				if (course != null){
					Set<User> users = new HashSet<User>();
					for(UserGroup studentGroup : course.getStudentGroups()) {
						users.addAll(studentGroup.getUsers());
					}
					UserStatsAnalyser userStatsAnalyser = new UserStatsAnalyser(assignmentManager.getAssignmentRepository().getGoogleDocsServiceImpl());
					stats = userStatsAnalyser.calculateStats(writingActivity, users);
				} else {
					throw new MessageException(Constants.EXCEPTION_WRITING_ACTIVITY_COURSE_NOT_FOUND);
				}
			} else {
				throw new MessageException(Constants.EXCEPTION_WRITING_ACTIVITY_NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof MessageException){
				throw (MessageException) e;
			} else {
				throw new MessageException(Constants.EXCEPTION_GET_USER_STATS);
			}
		}
		return stats;
	}
	
	@RequestMapping(value="activities/{courseId}", method = RequestMethod.PUT)
	public @ResponseBody WritingActivity saveWritingActivity(HttpServletRequest request, @PathVariable Long courseId, 
		   @PathVariable WritingActivity writingActivity) throws Exception {
		try{
			initialize(request);
			Course course = courseDao.loadCourse(courseId);
			if (isAdminOrSuperAdmin() || isCourseLecturer(course)) {
				return assignmentManager.saveActivity(course, writingActivity);
			} else {
				throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
			}
		}catch (Exception e) {
			e.printStackTrace();
			if (e instanceof MessageException){
				throw (MessageException) e;
			} else {
				throw new MessageException(Constants.EXCEPTION_SAVE_WRITING_ACTIVITY);
			}
		}
	}
}
