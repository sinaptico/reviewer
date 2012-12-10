package au.edu.usyd.reviewer.client.core.util;


/** 
 * This class has all the constants used in reviewer
 * @author mdagraca
 * TODO all the string message should be in a messages file
 */
public class Constants {

	
	/** Reviewer properties **/
	public static String REVIEWER_EMAIL_USERNAME="reviewer_email_username";
	public static String REVIEWER_EMAIL_PASSWORD="reviewer_email_password";
	public static String REVIEWER_GOOGLE_USERNAME="revierwer_google_username";
	public static String REVIEWER_GOOGLE_PASSWORD ="reviewer_google_password";
	public static String REVIEWER_GOOGLE_DOMAIN ="reviewer_google_domain";
	public static String REVIEWER_PRIVATE_KEY = "reviewer.privatekey";
	public static String REVIEWER_PUBLIC_KEY = "reviewer.publickey";
	public static String REVIEWER_DOCUMENTS_HOME="reviewer.documents.home";
	public static String REVIEWER_EMPTY_DOCUMENT ="reviewer.empty.document";
	public static String REVIEWER_EMPTY_DOCUMENT_FILENAME="empty.pdf";
	public static String REVIEWER_UPLOADS_HOME ="reviewer.uploads.home";
	public static String REVIEWER_EMPTY_FILE="reviewer.empty.file";
	public static String REVIEWER_ADMIN_USERS ="reviewer.admin.users";
	public static String REVIEWER_SMTP_HOST ="reviewer.smtp.host";
	public static String REVIEWER_SMTP_PORT = "reviewer.smtp.port";
	public static String REVIEWER_GLOSSER_HOST = "reviewer.glosser.host";
	public static String REVIEWER_GLOSSER_PORT ="reviewer.glosser.port";
	public static String SYSTEM_HTTP_PROXY_SET ="system.http.proxySet";
	public static String SYSTEM_HTTP_PROXY_HOST="system.http.proxyHost";
	public static String SYSTEM_HTTP_PROXY_PORT ="system.http.proxyPort";
	public static String SYSTEM_HTTPS_PROXY_SET="system.https.proxySet";
	public static String SYSTEM_HTTPS_PROXY_HOST="system.https.proxyHost";
	public static String SYSTEM_HTTPS_PROXY_PORT="system.https.proxyPort";
	public static String AGG_LOAD_EXCEL_PATH="aqg.loadExcelPath";
	public static String AGG_INSERT_TO_EXCEL_PATH="aqg.insertToExcelPath";
	public static String AGG_INSERT_TO_EXCEL_PATH_VALUE="Questions.xls";
	public static String REVIEWER_LOGOS_HOME = "reviewer.logos.home";
	public static String ORGANIZATION_LOGO_HOME = "organization.logo.home";
	public static String ORGANIZATION_LOGO_FILE = "organization.logo.file";
	
	/** Exception messages to the user  **/
	public static String EXCEPTION_USER_EXISTS="Exists a user in the database with the same email.";
	public static String EXCEPTION_ORGANIZATION_EXISTS="Exists an organization in the database with the same name.";
	public static String EXCEPTION_ORGANIZATION_HAS_USERS = "The organization can not be deleted because it has users that belongs to it";
	public static String EXCEPTION_ORGANIZATION_HAS_COURSES ="The organization can not be deleted because it has courses that belongg to it";
	public static String EXCEPTION_ORGANIZATION_EMPTY="The organization name can not be empty. This field is mandatory";
	public static String EXCEPTION_FIELD_EMPTIES="Please, enter some of the search fields";
	public static String EXCEPTION_PERMISSION_DENIED ="Permission denied. You don't have permission to execute this action.";
	public static String EXCEPTION_INVALID_TUTORIAL="Invalid tutorial";
	public static String EXCEPTION_GOOGLE_AUTHENTICATION="User could not be authenticated in Google Docs";
	public static String EXCEPTION_GOOGLE_URL_MALFORMED="The Google Docs URL is malformed";
	public static String EXCEPTION_USER_NOT_MOCKED = "The user could not be mocked";
	public static String EXCEPTION_LECTURER_INVALID_DOMAIN = "The email of the lecturers must belong to the organization domain";
	public static String EXCEPTION_STUDENTS_INVALID_DOMAIN = "The email of the students must belong to the organization domain";
	public static String EXCEPTION_TUTORS_INVALID_DOMAIN = "The email of the tutors must belong to the organization domain";
	public static String EXCEPTION_FAILED_INITIALIZE_ASSIGNMENT_MANAGER = "Failed to initialize assignment manager";
	public static String EXCEPTION_ACTIVITY_NOT_SAVED_GOOGLE_COURSE_NOT_EXIST="The activity could not be create because the course doesn't exist in Google Docs";
	public static String EXCEPTION_GOOGLE_AUTHENTICATION_ ="You are not authorized to work with this document in Google Docs";
	public static String EXCEPTION_GET_LOGGED_USER="There was an error obtaining the logged user information";
	public static String EXCEPTION_ACTIVITY_NOT_FINISHED ="The activity wasn't finished because the file doesn't exist.";
	
		
	// Dao Exceptions
	public static String EXCEPTION_HIBERNATE_SESSION_MESSAGE="Failed to create a session to database";
	public static String EXCEPTION_DELETE="Object could not be deleted";
	public static String EXCEPTION_GET="Object could not be loaded";
	public static String EXCEPTION_SAVE="Object could not be saved or updated";
	
	// Controller exceptions
	public static String EXCEPTION_INITIALIZE_CONTROLLER="Failed to initialize the controller";
	public static String EXCEPTION_SAVE_COURSE="Failed to save the course";
	public static String EXCEPTION_GET_COURSES="Failed to load the courses";
	public static String EXCEPTION_GET_COURSE="Failed to load the course";
	public static String EXCEPTION_DELETE_COURSE="Failed to delete the courses";
	public static String EXCEPTION_MOCKING_USER="Failed to mock the user";
	public static String EXCEPTION_DELETE_WRITING_ACTIVITY ="Failed to delete the writing activity";
	public static String EXCEPTION_GET_USER_STATS ="Failed to get the user stats";
	public static String EXCEPTION_COURSE_NOT_FOUND = "Course not found";
	public static String EXCEPTION_SAVE_REVIEW_TEMPLATE ="Failed to save the review template";
	public static String EXCEPTION_GET_REVIEW_TEMPLATE ="Failed to load the review template";
	public static String EXCEPTION_DELETE_REVIEW_TEMPLATE ="Failed to delete the review template";
	public static String EXCEPTION_GET_REVIEW_TEMPLATES="Failed to load the review templates";
	public static String EXCEPTION_REVIEW_TEMPLATE_NOT_FOUND = "Review template not found";
	public static String EXCEPTION_GET_YEARS ="Falied to load the years";
	public static String EXCEPTION_GET_REVIEW="Failed to get the review";
	public static String EXCEPTION_GET_USER="Failed to get the user";
	public static String EXCEPTION_GET_USERS="Failed to get the users";
	public static String EXCEPTION_USERNAME_OR_EMAIL_NO_EXIST="The user doesn't exist. Please enter other username or email";
	public static String EXCEPTION_USER_NOT_FOUND="User not found";
	public static String EXCEPTION_MOCKED_USER="Failed to get mocked user";
	public static String EXCEPTION_MOCKED_USER_NOT_FOUND="Mocked user not found";
	public static String EXCEPTION_ORGANIZATION_NOT_FOUND="Organization not found";
	public static String EXCEPTION_WRITING_ACTIVITY_NOT_FOUND="Writing activity not found";
	public static String EXCEPTION_WRITING_ACTIVITY_COURSE_NOT_FOUND="Failed to get the course of the writing activity";
	public static String EXCEPTION_SAVE_WRITING_ACTIVITY ="Failed to save the writing activity";
	public static String EXCEPTION_GET_DEADLINE="Failed to get the deadline";
	public static String EXCEPTION_GET_DOCENTRY="Failed to get the doc entry";
	public static String EXCEPTION_GET_REVIEWENTRY="Failed to get the review entry";
	public static String EXCEPTION_GET_GRADE="Failed to get the grade";
	public static String EXCEPTION_GET_RATING="Failed to get the rating";
	public static String EXCEPTION_GET_REVIWING_ACTIVITY="Failed to get the reviewing activity";
	public static String EXCEPTION_GET_USERGROUP="Failed to get the user group";
	public static String EXCEPTION_GET_WRITING_ACTIVITY="Failed to get the writing activity";
	public static String EXCEPTION_GET_DOCUMENT_TYPES="Failed to get the document types";
	public static String EXCEPTION_GET_ORGANIZATIONS="Failed to get the organizations";
	public static String EXCEPTION_GET_AUTOMATIC_REVIEWERS="Failed to get the automatic reviewers of the course";
	public static String EXCEPTION_GET_LECTURERS="Failed to get the lecturers of the course";
	public static String EXCEPTION_GET_TUTORS="Failed to get the tutors of the course";
	public static String EXCEPTION_GET_ORGANIZATION="Failed to get the organization";
	public static String EXCEPTION_GET_USER_GROUPS="Failed to get the user groups of the course";
	public static String EXCEPTION_GET_TEMPLATES="Failed to get the templates of the course";
	public static String EXCEPTION_GET_TUTORIALS="Failed to get the tutorials of the course";
	public static String EXCEPTION_GET_WRITING_ACTIVITIES="Failed to get the writing activities of the course";
	public static String EXCEPTION_GET_ORGANIZATION_PROPERTIES = "Failed to get the properties of the organization";
	public static String EXCEPTION_SAVE_REVIEWER ="Failed to save the reviewre";
	public static String EXCEPTION_SAVE_LECTURERS ="Failed to save the list of lecturers";
	public static String EXCEPTION_SAVE_TUTORS ="Failed to save ths list of tutors";
	public static String EXCEPTION_SAVE_USERS_GROUPS ="Failed to save the students groups";
	public static String EXCEPTION_SAVE_SUPERVISOR ="Failed to save the supervisor";
	public static String EXCEPTION_SAVE_TEMPLATE ="Failed to save the template (doc entry)";
	public static String EXCEPTION_INVALID_LECTURER="Invalid lecturer";
	public static String EXCEPTION_EMPTY_LECTURERS_LIST="Empty list of lecturers";
	public static String EXCEPTION_EMPTY_TUTORS_LIST="Empty list of tutors";
	public static String EXCEPTION_EMPTY_USERS_GROUPS_LIST="Empty list of students groups";
	public static String EXCEPTION_INVALID_STATUS="Invalid status";
	public static String EXCEPTION_SAVE_REVIEWING_ACTIVITIES="Failed to save the reviewing activities";
	public static String EXCEPTION_SAVE_ORGANIZATION ="Failed to save ths organization";
	public static String EXCEPTION_USERNAME_NO_EXIST="The user doesn't exist. Please enter other username";
	public static String EXCEPTION_GET_REVIEWER_PROPERTY="Failed to get reviewer property";
	public static String EXCEPTION_GET_REVIEWER_PROPERTIES="Failed to get reviewer properties";
	public static String EXCEPTION_SAVE_WRITING_ACTIVITIES ="Failed to save the writing activity";
	public static String EXCEPTION_REVIEWING_ACTIVITY_NOT_FOUND="Reviewing activity not found";
	public static String EXCEPTION_WRITING_ACTIVITY_NOT_EQUALS_ID="The writing activity id is not equals to path id";
	
	// Roles
	public static String ROLE_SUPER_ADMIN = "SuperAdmin";
	public static String ROLE_ADMIN = "Admin";
	public static String ROLE_GUEST = "Guest";
	
	// Rest methods parameters include and object attributes used in relationships or to generate Json maps and lists
	public static String ALL = "all";
	public static String TUTORS = "tutors";
	public static String LECTURERS = "lecturers";
	public static String STUDENTS = "students";
	public static String ACTIVITIES = "activities";
	public static String TEMPLATES = "templates";
	public static String ORGANIZATION = "organization";
	public static String DEADLINES ="deadlines";
	public static String DOC_ENTRIES ="docEntries";
	public static String GRADES ="grades";
	public static String REVIEWING_ACTIVITIES ="reviewingActivities";
	public static String PROPERTIES = "properties";
	public static String TUTORIALS = "tutorials";
	public static String ID = "id";
	public static String REVIEWING = "reviewing";
	public static String WRITING = "writing";
	
	// HTML error codes
	public static int HTTP_CODE_FORBIDDEN = 403;
	public static int HTTP_CODE_MESSAGE = 600;
	public static int HTTP_CODE_NOT_FOUND = 404;
	
	
	
	
}
