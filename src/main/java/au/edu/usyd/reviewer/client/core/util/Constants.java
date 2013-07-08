package au.edu.usyd.reviewer.client.core.util;


/** 
 * This class has all the constants used in reviewer
 * @author mdagraca
 * TODO all the string message should be in a messages file
 */
public class Constants {

	/** Reviewer properties **/
	public static String REVIEWER_ORGANIZATIONS_HOME="reviewer.organizations.home";
	public static String REVIEWER_ORGANIZATIONS_DOCUMENTS="reviewer.organizations.documents";
	public static String REVIEWER_ORGANIZATIONS_UPLOADS="reviewer.organizations.uploads";
	public static String REVIEWER_EMPTY_DOCUMENT ="reviewer.empty.document";
	public static String AGG_LOAD_EXCEL_PATH="aqg.loadExcelPath";
	public static String AGG_INSERT_TO_EXCEL_PATH="aqg.insertToExcelPath";

	/** Organizations properties **/
	public static String REVIEWER_EMAIL_USERNAME="reviewer.email.username";
	public static String REVIEWER_EMAIL_PASSWORD="reviewer.email.password";
	public static String REVIEWER_GOOGLE_USERNAME="reviewer.google.username";
	public static String REVIEWER_GOOGLE_PASSWORD ="reviewer.google.password";
	public static String REVIEWER_GOOGLE_DOMAIN ="reviewer.google.domain";
	public static String REVIEWER_SMTP_HOST ="reviewer.smtp.host";
	public static String REVIEWER_SMTP_PORT = "reviewer.smtp.port";
	public static String REVIEWER_GLOSSER_HOST = "reviewer.glosser.host";
	public static String REVIEWER_GLOSSER_PORT ="reviewer.glosser.port";
	public static String ORGANIZATION_LOGO_FILE = "organization.logo.file";
	public static String REVIEWER_DOMAIN="reviewer.domain";
	public static String ORGANIZATION_SHIBBOLETH_ENABLED="organization.shibboleht.enabled";
	public static String ORGANIZATION_PASSWORD_NEW_USERS="organization.password.new.users";
	
		
	/** Exception messages to the user  **/
	public static String EXCEPTION_USER_EXISTS="Exists a user in the database with the same email.";
	public static String EXCEPTION_ORGANIZATION_EXISTS="Exists an organization in the database with the same name.";
	public static String EXCEPTION_ORGANIZATION_HAS_USERS = "The organization can not be deleted because it has users that belongs to it";
	public static String EXCEPTION_ORGANIZATION_HAS_COURSES ="The organization can not be deleted because it has courses that belongg to it";
	public static String EXCEPTION_ORGANIZATION_EMPTY="The organization name can not be empty. This field is mandatory";
	public static String EXCEPTION_FIELD_EMPTIES="Please, enter some of the search fields";
	public static String EXCEPTION_PERMISSION_DENIED ="Permission denied. You don't have permission to execute this action.";
	public static String EXCEPTION_INVALID_TUTORIAL="Invalid tutorial.";
	public static String MESSAGE_RELOAD_COURSES = "\nPlease, reload the courses";
	public static String MESSAGE_STUDENTS_TUTORIAL = "\nThe tutorials of the course must be equals to the tutorials of the student in the students spreadsheets.";
	public static String EXCEPTION_GOOGLE_AUTHENTICATION="User could not be authenticated in Google Docs";
	public static String EXCEPTION_GOOGLE_URL_MALFORMED="The Google Docs URL is malformed";
	public static String EXCEPTION_GOOGLE_CONNECTION="Failed to connect to Google" + 
													 "\n" + "The admin of the organization must check the required properties to connect to Google";
	public static String EXCEPTION_SMTP_CONNECTION="Failed to connect to the SMTP server." + "\n" +
													"The admin of the organization must check the required properties to connect to the SMTP server";
	public static String EXCEPTION_USER_NOT_MOCKED = "The user could not be mocked";
	public static String EXCEPTION_LECTURER_INVALID_DOMAIN = "The email of the lecturers must belong to the organization domain";
	public static String EXCEPTION_STUDENTS_INVALID_DOMAIN = "The email of the students must belong to the organization domain";
	public static String EXCEPTION_TUTORS_INVALID_DOMAIN = "The email of the tutors must belong to the organization domain";
	public static String EXCEPTION_FAILED_INITIALIZE_ASSIGNMENT_MANAGER = "Failed to initialize assignment manager";
	public static String EXCEPTION_ACTIVITY_NOT_SAVED_GOOGLE_COURSE_NOT_EXIST="The activity could not be create because the course doesn't exist in Google Docs";
	public static String EXCEPTION_GOOGLE_AUTHENTICATION_ ="You are not authorized to work with this document in Google Docs";
	public static String EXCEPTION_GET_LOGGED_USER="Failed to get the logged user information";
	public static String EXCEPTION_ACTIVITY_NOT_FINISHED ="The activity wasn't finished because the file doesn't exist.";
	public static String EXCEPTION_ENCRYPT="Failed to encrypt the value";
	public static String EXCEPTION_DECRYPT="Failed to decrypt the value";
	public static String EXCEPTION_SAVE_EMAIL_ORGANIZATION="Failed to save the email of the organization";
	public static String EXCEPTION_SAVE_EMAIL_COURSE="Failed to save the email of the course";
	public static String EXCEPTION_WRONG_SEMESTER="The semester must correspond with the semester of the year.";
	public static String EXCEPTION_EMPTY_COURSE_NAME = "Please, enter the name of the course; this field is mandatary.";
	public static String EXCEPTION_EMPTY_COURSE_TUTORIALS ="Please, enter the tutorial of the course; this field is mandatory.";
	public static String EXCEPTION_ORGANIZATION_UNACTIVATED="The organization is not activated." + 
															"\n" + "The admin of the organization must complete the required properties to activate it.";
	public static String EXCEPTION_ORGANIZATION_DELETED ="The organization has been deleted.";
	public static String EXCEPTION_ORGANIZATION_PROPERTIES ="Properties to complete: ";
	public static String EXCEPTION_NOT_COURSES_FOR_ACTIVITY="There are no courses to add a new activity." + 
															"\n" + "Please, create a course first.";
	public static String EXCEPTION_DOCUMENT_ALREADY_SUBMITTED="Document has already been submitted.";
	public static String EXCEPTION_REVIEWER_NOT_DOCUMENT_OWNER="Reviewer can't be owner of the document.";
	public static String EXCEPTION_REVIEW_ALREADY_ASSIGNED="Review already assigned to user.";
	public static String EXCEPTION_GRADE_MARK_NUMERIC="Marks must be numeric values greater than zero (0).";
	public static String EXCEPTION_GRADE_MAX_MARK ="The maximum mark for this review is: ";
	public static String EXCEPTION_SESSION_EXPIRED_SUBMIT_DOCUMENT="Your session has expired. Please login again to submit your document.";
	public static String EXCEPTION_DEADLINE_ALREADY_PASSED="The deadline has already passed.";
	public static String EXCEPTION_WRONG_PASSWORD="Wrong password, please try again";
	public static String EXCEPTION_DOCUMENT_NOT_FOUND="Document not found";
	public static String EXCEPTION_RATING_NOT_FOUND="Rating not found";
	public static String EXCEPTION_REVIEW_NOT_FOUND="Review not found";
	public static String EXCEPTION_SESSION_EXPIRED_SUBMIT_RATING="Your session has expired. Please login again to submit your rating.";
	public static String EXCEPTION_SESSION_EXPIRED_SAVE_REVIEW="Your session has expired. Please login again to save your review.";
	public static String EXCEPTION_SESSION_EXPIRED_SUBMIT_REVIEW="Your session has expired. Please login again to submit your review.";
	public static String EXCEPTION_WRONG_ORGANIZATION_DOMAIN="The domain of the email doesn't belong to the organization";
	public static String EXCEPTION_ADMIN_CAN_NO_BE_LECTURER_OR_TUTOR="The admin user used to access to Google can not be used as lecturer or tutor.\n" +
															"Please remove him/her from lecturers/tutors and try again.";
														 
	
	// Dao Exceptions
	public static String EXCEPTION_HIBERNATE_SESSION_MESSAGE="Failed to create a session to database";
	public static String EXCEPTION_DELETE="Object could not be deleted";
	public static String EXCEPTION_GET="Object could not be loaded";
	public static String EXCEPTION_SAVE="Object could not be saved or updated";
	
	// Controller exceptions
	public static String EXCEPTION_INITIALIZE_CONTROLLER="Failed to initialize the controller";
	public static String EXCEPTION_SAVE_COURSE="Failed to save the course";
	public static String EXCEPTION_GET_COURSES="Failed to load the courses";
	public static String EXCEPTION_GET_DELETED_COURSES ="Failed to load the deleted courses";
	public static String EXCEPTION_GET_COURSE="Failed to load the course";
	public static String EXCEPTION_DELETE_COURSE="Failed to delete the course";
	public static String EXCEPTION_DELETE_COURSE_NOT_FINISHED="The course can not be deleted because it has not finished";
	public static String EXCEPTION_MOCKING_USER="Failed to mock the user";
	public static String EXCEPTION_DELETE_WRITING_ACTIVITY ="Failed to delete the writing activity";
	public static String EXCEPTION_DELETE_WRITING_ACTIVITY_NOT_FINISHED="The activity can not be deleted because it has not finished";
	public static String EXCEPTION_GET_USER_STATS ="Failed to get the user stats";
	public static String EXCEPTION_COURSE_NOT_FOUND = "Course not found";
	public static String EXCEPTION_SAVE_REVIEW_TEMPLATE ="Failed to save the review template";
	public static String EXCEPTION_GET_REVIEW_TEMPLATE ="Failed to load the review template";
	public static String EXCEPTION_DELETE_REVIEW_TEMPLATE ="Failed to delete the review template";
	public static String EXCEPTION_DELETE_REVIEW_TEMPLATE_IN_USE ="The review template can not be deleted because it's in use";
	public static String EXCEPTION_GET_REVIEW_TEMPLATES="Failed to load the review templates";
	public static String EXCEPTION_GET_DELETED_REVIEW_TEMPLATES="Failed to load the deleted review templates";
	public static String EXCEPTION_REVIEW_TEMPLATE_NOT_FOUND = "Review template not found";
	public static String EXCEPTION_GET_YEARS ="Falied to load the years";
	public static String EXCEPTION_GET_REVIEW="Failed to get the review";
	public static String EXCEPTION_GET_USER="Failed to get the user";
	public static String EXCEPTION_GET_USERS="Failed to get the users";
	public static String EXCEPTION_USERNAME_OR_EMAIL_NO_EXIST="The user doesn't exist. Please enter other username or email";
	public static String EXCEPTION_USER_NOT_FOUND="User not found. He/she doesn't exist in the database";
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
	public static String EXCEPTION_SAVE_TEMPLATE ="Failed to save the template (doc entry)";
	public static String EXCEPTION_INVALID_LECTURER="Invalid lecturer";
	public static String EXCEPTION_EMPTY_LECTURERS_LIST="Empty list of lecturers";
	public static String EXCEPTION_EMPTY_STUDENTS_LIST="Empty list of students";
	public static String EXCEPTION_EMPTY_TUTORS_LIST="Empty list of tutors";
	public static String EXCEPTION_EMPTY_USERS_GROUPS_LIST="Empty list of students groups";
	public static String EXCEPTION_INVALID_STATUS="Invalid status. The course status has changed." + 
												  "\n" + "Please, reload the course.";
	public static String EXCEPTION_ACTIVITY_FINISHED="The activity finished, It can't be modified.";
	public static String EXCEPTION_ACTIVITY_START_AFTER_DEADLINE="The start date of the activity must be before to all the deadlines finish dates." +
																 "\n" + "Deadeline with wrong finish date: ";
	public static String EXCEPTION_SAVE_REVIEWING_ACTIVITIES="Failed to save the reviewing activities";
	public static String EXCEPTION_SAVE_ORGANIZATION ="Failed to save ths organization";
	public static String EXCEPTION_USERNAME_NO_EXIST="The user doesn't exist. Please enter other username";
	public static String EXCEPTION_GET_REVIEWER_PROPERTY="Failed to get reviewer property";
	public static String EXCEPTION_GET_REVIEWER_PROPERTIES="Failed to get reviewer properties";
	public static String EXCEPTION_SAVE_WRITING_ACTIVITIES ="Failed to save the writing activity";
	public static String EXCEPTION_REVIEWING_ACTIVITY_NOT_FOUND="Reviewing activity not found";
	public static String EXCEPTION_WRITING_ACTIVITY_NOT_EQUALS_ID="The writing activity id is not equals to path id";
	public static String EXCEPTION_GET_SECTION = "Failed to get the section";
	public static String EXCEPTION_SAVE_STUDENTS = "Failed to save the students";
	public static String EXCEPTION_LOGOUT = "Failed to logout the user";
	public static String EXCEPTION_DELETE_REVIEWING_ACTIVITY_NOT_FINISHED ="The writing activity can not be deleted because there is a reviewing activity whose status is not finished";
	public static String EXCEPTION_SAVE_EMAIL="Failed to save the email";
	public static String EXCEPTION_GET_EMAILS="Failed to load the emails";
	public static String EXCEPTION_GET_EMAIL="Failed to load the email";
	public static String EXCEPTION_GENERATE_ORGANIZATION_EMAILS="Failed to generate the emails for the organization";
	public static String EXCEPTION_GENERATE_COURSE_EMAILS="Failed to generate the emails for the course";
	public static String EXCEPTION_GOOGLE_USER_HAS_ACCESS = "This user already has access to the document.";
	public static String EXCEPTION_INVALID_LOGIN = "Invalid username or password";
	public static String EXCEPTION_GET_DELETED_WRITING_ACTIVITIES="Failed to get the deleted writing activities";
	public static String EXCEPTION_REVIEW_ENTRY_NOT_FOUND = "Review entry not found";
	public static String EXCEPTION_WRONG_REVIEWING_ACTIVITY_FINISH_DATE="The reviewing task start date is greater than the finish date of one of the activity deadlines." +
																		"\n" + "Reviewing task with wrong finish date: ";
	public static String EXCEPTION_DELETE_ORPHAN_EMAILS="Failed to delete the orphan emails.";
	public static String EXCEPTION_NOT_ACTIVITY_FINISH_DATE = "The activity doesn't have a finish date. Please, set it";
	public static String EXCEPTION_COURSE_LECTURERS_TUTORS="Wrong quantity of parameters. Please, verify the information of the lecturers and tuturos";
	public static String EXCEPTION_PEER_REVIEW_NOT_EXIST= "The Sheet2 doesn't exit in the student spreadsheet.\n" + 
														  "Please, add it with the reviewee and reviewer emails.";
	public static String EXCEPTION_STUDENT_NO_EXIST = "The student doesn't exist in the database.";
	public static String EXCEPTION_INVALID_EMAIL="Please, enter a valid value for the email.";
	public static String EXCEPTION_LECTURER_FIRSTNAME_EMPTY="Please enter the firstname of the lecturer.\n" +
													        "This field is mandatary";
	public static String EXCEPTION_LECTURER_EMAIL_EMPTY="Please enter the email of the lecturer.\n" +
    														"This field is mandatary";
	public static String EXCEPTION_LECTURER_LASTNAME_EMPTY="Please enter the lastname of the lecturer.\n" +
    														"This field is mandatary";
	public static String EXCEPTION_TUTOR_FIRSTNAME_EMPTY="Please enter the firstname of the tutor.\n" +
    														"This field is mandatary";
	public static String EXCEPTION_TUTOR_EMAIL_EMPTY="Please enter the email of the tutor.\n" +
													"This field is mandatary";
	public static String EXCEPTION_TUTOR_LASTNAME_EMPTY="Please enter the lastname of the tutor.\n" +
													"This field is mandatary";
	public static String EXCEPTION_STUDENT_FIRSTNAME_EMPTY="Please enter the firstname of the student.\n" +
														 "This field is mandatary";
	public static String EXCEPTION_STUDENT_EMAIL_EMPTY="Please enter the email of the student.\n" +
													  "This field is mandatary";
	public static String EXCEPTION_STUDENT_LASTNAME_EMPTY="Please enter the lastname of the student.\n" +
														"This field is mandatary";
	
	
	// Roles
	// Role of the admin of all the organizations
	public static String ROLE_SUPER_ADMIN = "SuperAdmin";
	// Role for the admin of the organization ==> see all the courses of the organization
	public static String ROLE_ADMIN = "Admin";
	// students ==> they see only the assignments page with his/her assignments
	public static String ROLE_GUEST = "Guest";
	//Role for lecturers and tutors ==> see all the courses where he/she is lecturer or tutor
	public static String ROLE_STAFF="Staff";
	
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
	public static int HTTP_CODE_LOGOUT= 601;
	
	// Emails names
	public static String EMAIL_STUDENT_REVIEW_START = "Student review start";
	public static String EMAIL_STUDENT_ACTIVITY_START = "Student activity start";
	public static String EMAIL_LECTURER_DEADLINE_FINISH = "Lecturer deadline finish";
	public static String EMAIL_PASSWORD_DETAILS = "Password details";
	public static String EMAIL_STUDENT_REVIEW_FINISH = "Student review finish";
	public static String EMAIL_STUDENT_RECEIVED_REVIEW = "Student received review";
	
	// Emails messages
	public static String EMAIL_STUDENT_REVIEW_START_MESSAGE = "Dear @StudentName@, " + "\n\n" +
															  "You need to review the @ActivityName@ of one of your peers. " + 
															  "Please visit @ReviewerLink@ to write and submit your review before the deadline on @DeadlineDate@." + 
															  "\n\n" + "@FromName@";
			
	public static String EMAIL_STUDENT_ACTIVITY_START_MESSAGE ="Dear @StudentName@, " + "\n\n" + 
	   														   "A Google document has been created for you to write your @ActivityName@. " +
	   														   "Please visit @ReviewerLink@ to write and submit your document before the deadline on @DeadlineDate@." +
	   														   	"\n\n" + "@FromName@"; 
		
	public static String EMAIL_LECTURER_DEADLINE_FINISH_MESSAGE ="Dear @LecturerName@, " +
																 "\n\n" + "The @ActivityName@ @DeadlineName@ assessment has finished. " + "\n" + 
																 "Please go to @ReviewerLink@ to download the documents. " + "\n\n"; 
		
		
	public static String EMAIL_PASSWORD_DETAILS_MESSAGE = "Dear @UserName@, " +
														  "\n\n" + "The application for the course @CourseName@ is now available for you. " + 
														  "\n" + " To login, please go to @iWriteLink@ " +
														  "\n\n" + " Username: @UserUsername@ " + "\n" + " Password: @Password@" + "\n\n" + "@FromName@";
	
	public static String EMAIL_STUDENT_REVIEW_FINISH_MESSAGE = "Dear @LecturerName@, " + "\n\n" +
															   "The @ActivityName@ @DeadlineName@ assessment has finished. " + 
															   "\n" + "Reviews are now available in @ReviewerLink@." + "\n\n" + "@FromName@";
	
	public static String EMAIL_STUDENT_RECEIVED_REVIEW_MESSAGE = "Dear @UserName@, " + 
																 "\n\n" + "You have received feedback from the activity @ActivityName@. " +
																 "\n" + "You can go to @ReviewerLink@ to read it." + "\n\n" + "@FromName@";
	
	public static String EMAIL_TEST_MESSAGE = "This email is sent by reviewer to test the SMTP connection";

	// Google user service messages
	public static String EXCEPTION_GOOGLE_USER_DELETED_RECENTLY="The request instructs Google to create a new user but uses the username of an account that was deleted in the previous five days";
	public static String EXCEPTION_GOOGLE_USER_SUSPENDED="The user identified in the request is suspended";
	public static String EXCEPTION_GOOGLE_DOMAIN_USER_LIMIT_EXCEEDED = "The domain has already reached its quota of user accounts";
	public static String EXCEPTION_GOOGLE_DOMAIN_SUSPENDED="Google has suspended the specified domain's access to Google Apps";
	public static String EXCEPTION_GOOGLE_ENTITY_NOT_EXIST="The request asks Google to retrieve an entity that does not exist.";
	public static String EXCEPTION_FAILED_CREATE_USER="Failed to create user";
	public static String EXCEPTION_FAILED_DELETE_USER="Failed to delete user";
	public static String EXCEPTION_FAILED_RETRIEVE_USER="Failed to retrieve user";
	public static String EXCEPTION_GOOGLE_DOWNLOAD_FILE="Could not download file %s of type: %s";
	public static String EXCEPTION_GOOGLE_APPS="Google Apps message ";
	public static String EXCEPTION_SPREADSHEET_GROUP="The group must be numeric";
	public static String EXCEPTION_SPREADSHEET_EMAIL="The email is not valid, it must belong to the organization domain";
	
	// Google Document Types
	public static String GOOGLE_DOCUMENT_TYPE_PDF = "pdf";
	public static String GOOGLE_DOCUMENT_TYPE_DOCUMENT="document";
	public static String GOOGLE_DOCUMENT_TYPE_PRESENTATION="presentation";
	public static String GOOGLE_DOCUMENT_TYPE_SPREADSHEET="spreadsheet";
	public static String GOOGLE_DOCUMENT_TYPE_DRAWING="drawing";
	
	// Google Export Types
	public static String GOOGLE_EXPORT_TYPE_DOC="doc";
	public static String GOOGLE_EXPORT_TYPE_XLS="xls";
	public static String GOOGLE_EXPORT_TYPE_PPT="ppt";
	public static String GOOGLE_EXPORT_TYPE_PNG="png";
	public static String GOOGLE_EXPORT_TYPE_PDF="pdf";
	public static String GOOGLE_EXPORT_TYPE_HTML="html";

	
	// Shibboleth enabled property values
	public static String SHIBBOLETH_ENABLED_YES="YES";
	public static String SHIBBOLETH_ENABLED_NO="NO";
	
	// Default password of new users in Google Apps
	public static String NEW_USERS_PASSWORD_DEFAULT_VALUE="Changeme";
	
	// Shibboleth enabled property values
	public static String YES="YES";

}
