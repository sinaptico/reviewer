package au.edu.usyd.reviewer.server;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Email;
import au.edu.usyd.reviewer.client.core.EmailCourse;
import au.edu.usyd.reviewer.client.core.EmailOrganization;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

public class EmailDao extends ObjectDao {

	public static EmailDao instance;
	
	private EmailDao(){
		super();
	}
	
	public static EmailDao getInstance(){
		if (instance == null){
			instance = new EmailDao();
		}
		return instance;
	}
	
	@Override
	protected Object getObject(Long objectId) throws MessageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<Object> getObjects(String name) throws MessageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object getObject(String name) throws MessageException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public EmailOrganization saveEmailOrganization(EmailOrganization email, Organization organization) throws MessageException{
		try{
			email = (EmailOrganization) super.save(email);
			if (email != null){
				email = email.clone();
				email.setOrganization(organization);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_SAVE_EMAIL_ORGANIZATION);
		}
		return email;
	}
	
	public EmailCourse saveEmailCourse(EmailCourse email, Course course) throws MessageException{
		try{
			email = (EmailCourse) super.save(email);
			if (email != null){
				email = email.clone();
				email.setCourse(course);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_SAVE_EMAIL_COURSE);
		}
		return email;
	}
	
	public void deleteOrphanEmails() throws Exception {
		Session session = null;
		try{
			session = getSession();
			session.beginTransaction();
			String stringQuery = "Delete from Email " +
						   		 "where organization_id is null and course_id is null";
			
			Query query = session.createQuery(stringQuery);
			query.executeUpdate();
			session.getTransaction().commit();
		} catch (Exception e){
			e.printStackTrace();
			if ( session != null && session.getTransaction() != null){
				session.getTransaction().rollback();
			}
			throw new MessageException(Constants.EXCEPTION_DELETE_ORPHAN_EMAILS);
		}
	}
}
