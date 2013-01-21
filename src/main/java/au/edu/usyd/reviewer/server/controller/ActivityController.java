package au.edu.usyd.reviewer.server.controller;

import java.util.ArrayList;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import au.edu.usyd.reviewer.client.admin.report.UserStats;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.UserGroup;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.report.UserStatsAnalyser;
import au.edu.usyd.reviewer.server.util.ObjectConverter;

/**
 * This class is the controller for CRUD the method related with writing activities 
 * @author mdagraca
 */

@Controller
@RequestMapping("/")
public class ActivityController extends ReviewerController {
	
	/**
	 * Delete the writing activity with id equals to {id}
	 * @param request HttpServletRequest to initialize the controller
	 * @param id id of the writing activity to delete
	 * @param tutors list of tutors users
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="activities/{id}", method = RequestMethod.DELETE)
	public @ResponseBody void deleteWritingActivity(HttpServletRequest request,@PathVariable Long id) throws MessageException {
		MessageException me = null;
		try{
			initialize(request);
			if (id != null){
				WritingActivity writingActivity = assignmentManager.loadWritingActivity(id);
				if (writingActivity != null){
					Course course = courseManager.loadCourseWhereWritingActivity(writingActivity);
					if ( course != null){
						if (isAdminOrSuperAdmin() || isCourseLecturer(course)) {
							assignmentManager.deleteActivity(writingActivity);
						} else {
							me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
							me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
							throw me;
						}
					} else {
						me = new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
						me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
						throw me;
					}
				} else {
					me = new MessageException(Constants.EXCEPTION_WRITING_ACTIVITY_NOT_FOUND);
					me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
					throw me;
				}
			} else {
				me = new MessageException(Constants.EXCEPTION_WRITING_ACTIVITY_NOT_FOUND);
				me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
				throw me;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof MessageException){
				me = (MessageException) e;
			} else {
				me = new MessageException(Constants.EXCEPTION_DELETE_WRITING_ACTIVITY);
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}
	/**
	 * This method returns the statistics of the writing activity
	 * @param request HttpServletRequest to initialize the controller
	 * @param id id of the writing activity 
	 * @return List of Map
	 * @throws MessageException
	 */
	@RequestMapping(value="activities/{id}/stats", method = RequestMethod.GET)
	public @ResponseBody List<Object> getWritingActivityStats(HttpServletRequest request, @PathVariable Long id) throws MessageException {
		MessageException me = null;
		try{
			initialize(request);
			if (isAdminOrSuperAdmin() ){ // || isCourseLecturer(course)){
				if (id != null){
					WritingActivity writingActivity = assignmentManager.loadWritingActivity(id);
					if ( writingActivity != null){
						Course course = courseManager.loadCourseWhereWritingActivity(writingActivity);
						if (course != null){
							Set<User> users = new HashSet<User>();
							for(UserGroup studentGroup : course.getStudentGroups()) {
								users.addAll(studentGroup.getUsers());
							}
							UserStatsAnalyser userStatsAnalyser = new UserStatsAnalyser(assignmentManager.getAssignmentRepository().getGoogleDocsServiceImpl());
							Collection<UserStats> stats = userStatsAnalyser.calculateStats(writingActivity, users);
							List<Object> statsList = ObjectConverter.convertCollectiontInList(stats, "", "",0);
							return statsList;
						} else {
							me = new MessageException(Constants.EXCEPTION_WRITING_ACTIVITY_COURSE_NOT_FOUND);
							me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
							throw me;
						}
					} else {
						me = new MessageException(Constants.EXCEPTION_WRITING_ACTIVITY_NOT_FOUND);
						me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
						throw me;
					}
				} else {
					me = new MessageException(Constants.EXCEPTION_WRITING_ACTIVITY_NOT_FOUND);
					me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
					throw me;
				}
			} else {
				me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
				me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
				throw me;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof MessageException){
				me = (MessageException) e;
			} else {
				me = new MessageException(Constants.EXCEPTION_GET_USER_STATS);
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}
	
	/**
	 * Return the writing activity with id equals to the {id}. 
	 * @param request  HttpServletRequest to initialize the controller
	 * @param id id of the writing activity to return
	 * @param include if include is equals to all returns the writing activity with all its relationships
	 * @param relationship relationships to return: deadlines, docEntries, grades, reviewActivities
	 * @return WritingActivity
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="activities/{id}",method = RequestMethod.GET)
	public @ResponseBody  Map getWritingActivity(HttpServletRequest request, @PathVariable Long id, 
			@RequestParam(value="include", required=false) String include, 
			@RequestParam(value="relationships", required=false) String relationships) throws MessageException { 
		WritingActivity activity = null;
		MessageException me = null;
		try{
			initialize(request);
			if (isAdminOrSuperAdminOrGuest()){
				if (id == null){
					me = new MessageException(Constants.EXCEPTION_WRITING_ACTIVITY_NOT_FOUND);
					me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
					throw me;
				} else { 
					activity = assignmentManager.loadWritingActivity(id);
					if (activity !=  null){
						Map activityMap = ObjectConverter.convertObjectInMap(activity, include,relationships,0);
						return activityMap;	
					} else {
						me = new MessageException(Constants.EXCEPTION_WRITING_ACTIVITY_NOT_FOUND);
						me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
						throw me;
					}
				}
			} else {
				me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
				me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
				throw me;
			}
		} catch( Exception e){
			e.printStackTrace();
			if (e instanceof MessageException){
				me = (MessageException) e;
			} else {	
				me = new MessageException(Constants.EXCEPTION_GET_WRITING_ACTIVITY);
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}
	

	@RequestMapping(value="reviewing/{id}",method = RequestMethod.GET)
	public @ResponseBody  Map getReviewingctivity(HttpServletRequest request, @PathVariable Long id, 
			@RequestParam(value="include", required=false) String include, 
			@RequestParam(value="relationships", required=false) String relationships) throws MessageException { 
		ReviewingActivity activity = null;
		MessageException me = null;
		try{
			initialize(request);
			if (isAdminOrSuperAdminOrGuest()){
				if (id == null){
					me = new MessageException(Constants.EXCEPTION_REVIEWING_ACTIVITY_NOT_FOUND);
					me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
					throw me;
				} else { 
					activity = assignmentManager.loadReviewingActivity(id);
					if (activity !=  null){
						Map activityMap = ObjectConverter.convertObjectInMap(activity, include,relationships,0);
						return activityMap;	
					} else {
						me = new MessageException(Constants.EXCEPTION_REVIEWING_ACTIVITY_NOT_FOUND);
						me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
						throw me;
					}
				}
			} else {
				me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
				me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
				throw me;
			}
		} catch( Exception e){
			e.printStackTrace();
			if (e instanceof MessageException){
				me = (MessageException) e;
			} else {	
				me = new MessageException(Constants.EXCEPTION_REVIEWING_ACTIVITY_NOT_FOUND);
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}

	
}
