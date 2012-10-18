package au.edu.usyd.reviewer.server.controller;

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
import au.edu.usyd.reviewer.server.report.UserStatsAnalyser;

@Controller
@RequestMapping("/")
public class ActivityController extends au.edu.usyd.reviewer.server.controller.Controller{

	@RequestMapping(value="/activities/", method = RequestMethod.DELETE)
	public @ResponseBody WritingActivity deleteWritingActivity(HttpServletRequest request,@RequestBody WritingActivity writingActivity) throws Exception {
		initialize(request);
		if (isAdminOrSuperAdmin() || isCourseLecturer(assignmentDao.loadCourseWhereWritingActivity(writingActivity))) {
			try {
				assignmentManager.deleteActivity(writingActivity);
				return writingActivity;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			throw new Exception("Permission denied");
		}
	}
	
	@RequestMapping(value="/activities/{courseId}", method = RequestMethod.PUT)
	public @ResponseBody WritingActivity saveWritingActivity(HttpServletRequest request,@PathVariable Long courseId,@RequestBody WritingActivity writingActivity) throws Exception {
		initialize(request);
		Course course = courseDao.loadCourse(courseId);
		if (isAdminOrSuperAdmin() || isCourseLecturer(course)) {
			try {
				return assignmentManager.saveActivity(course, writingActivity);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			throw new Exception("Permission denied");
		}
	}
	
	@RequestMapping(value="/userstats/{writingActivityId}", method = RequestMethod.GET)
	public @ResponseBody Collection<UserStats> getWritingActivityStats(HttpServletRequest request,@PathVariable  Long writingActivityId) throws Exception {
		initialize(request);
		WritingActivity writingActivity = assignmentDao.loadWritingActivity(writingActivityId);
		Course course = assignmentDao.loadCourseWhereWritingActivity(writingActivity);
		Set<User> users = new HashSet<User>();
		for(UserGroup studentGroup : course.getStudentGroups()) {
			users.addAll(studentGroup.getUsers());
		}
		UserStatsAnalyser userStatsAnalyser = new UserStatsAnalyser(assignmentManager.getAssignmentRepository().getGoogleDocsServiceImpl());
		return userStatsAnalyser.calculateStats(writingActivity, users);
	}

}
