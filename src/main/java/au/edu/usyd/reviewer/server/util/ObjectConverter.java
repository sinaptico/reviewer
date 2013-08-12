package au.edu.usyd.reviewer.server.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.commons.beanutils.PropertyUtils;

import au.edu.usyd.reviewer.client.core.Activity;
import au.edu.usyd.reviewer.client.core.Choice;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.DocumentType;
import au.edu.usyd.reviewer.client.core.Email;
import au.edu.usyd.reviewer.client.core.EmailCourse;
import au.edu.usyd.reviewer.client.core.EmailOrganization;
import au.edu.usyd.reviewer.client.core.Entry;
import au.edu.usyd.reviewer.client.core.FeedbackTemplate;
import au.edu.usyd.reviewer.client.core.GeneralRating;
import au.edu.usyd.reviewer.client.core.Grade;
import au.edu.usyd.reviewer.client.core.LogbookDocEntry;
import au.edu.usyd.reviewer.client.core.LogpageDocEntry;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.OrganizationProperty;
import au.edu.usyd.reviewer.client.core.Question;
import au.edu.usyd.reviewer.client.core.QuestionRating;
import au.edu.usyd.reviewer.client.core.QuestionReview;
import au.edu.usyd.reviewer.client.core.QuestionScore;
import au.edu.usyd.reviewer.client.core.Rating;
import au.edu.usyd.reviewer.client.core.Review;
import au.edu.usyd.reviewer.client.core.ReviewEntry;
import au.edu.usyd.reviewer.client.core.ReviewReply;
import au.edu.usyd.reviewer.client.core.ReviewTemplate;
import au.edu.usyd.reviewer.client.core.ReviewerProperty;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.Rubric;
import au.edu.usyd.reviewer.client.core.Section;
import au.edu.usyd.reviewer.client.core.TemplateReply;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.UserGroup;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.util.Constants;

/**
 * This class is used by Controllers classes to convert objects in maps or list before return a response to a Rest request method. 
 * @author mdagraca
 *
 */
public class ObjectConverter {

	/**
	 * Convert the collection of objects received as parameter in a list of map
	 * @param <E> The object in the collection are instances of the class E
	 * @param objects Collection of objects 
	 * @param include if include=all return objects instead of ids
	 * @return List<Map> list of map. Each map has an object information (all the its properties with their values)
	 */
	public static <E extends Object> List<Object> convertCollectiontInList(Collection<E> objects, String include, String relationships, int level){
		List<Object> objectList = new ArrayList<Object>();
		try{
			if (relationships != null){
				relationships = relationships.toLowerCase();
			}
			if (include != null){
				include = include.toLowerCase();
			}
			Iterator<E> it = objects.iterator();
			while(it.hasNext()){
				E object = it.next();
				objectList.add(convertObjectInMap(object, include, relationships,level));
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return objectList;
	}
	
	/**
	 * This method returns a map with the object information. If include=all return objects otherwise return ids
	 * @param <E> Class of the objects
	 * @param object list of objects to convert in a map
	 * @param include if include=all return objects instead of ids
	 * @return Map<String,Object>
	 */
	public static <E extends Object> Map<String,Object> convertObjectInMap(E object, String include, String relationships, int level){
		Map objectMap = new HashMap<String,Object>();
		try{
			if (relationships != null){
				relationships = relationships.toLowerCase();
			}
			if (include != null){
				include = include.toLowerCase();
			}
			BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
			for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
				if (descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null) {
					String name = descriptor.getName();
					Object value = PropertyUtils.getProperty(object, name);
					if (value instanceof Collection){
						objectMap.put(name, getConvertedObjectsCollection(name, value, include, relationships, level+1));			
					} else if (isReviewerObject(value)){
						if (Constants.ALL.equals(include) && level < 2){
							objectMap.put(name,value);
						} else {
							if (relationships!=null && name!= null && relationships.contains(name.toLowerCase()) && level < 2){
								objectMap.put(name,value);
							} else {
								objectMap.put(value.getClass().getSimpleName(), getObjectId(value));
							}
						}
					} else {
						objectMap.put(name, value);
					}
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return objectMap;
	}
	
	/**
	 * Convert object relationships in Ia list of ids
	 * @param <E> Class of the objects
	 * @param objects list of objects
	 * @return list of ids
	 */
	private static <E extends Object> List<Object> convertRelationshipsInIds(Collection<E> objects){
		List<Object> objectList = new ArrayList<Object>();
		
		try{
			Iterator<E> it = objects.iterator();
			while(it.hasNext()){
				E object = (E) it.next();
				if (object instanceof Collection){
					return convertRelationshipsInIds((Collection) object);
				} else {
					BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
					for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
						if (descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null) {
							String name = descriptor.getName();
							Object value = PropertyUtils.getProperty(object, name);
							if (name != null && name.equals(Constants.ID)){
								objectList.add(value);
								break;
							}
						}
					}
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return objectList;
	}
	
	/**
	 * It returns a boolean indicating if the object received as parameter is a Reviewer object or not
	 * @param value object to verify
	 * @return true if the object is a Reviewer object otherwise false
	 */
	private static boolean isReviewerObject(Object value){
		boolean isReviewerObject = (value instanceof Activity) || 
								   (value instanceof Choice) ||
								   (value instanceof Course) ||
								   (value instanceof Deadline) ||
								   (value instanceof DocEntry) ||
								   (value instanceof DocumentType) ||
								   (value instanceof Email) ||
								   (value instanceof EmailCourse) ||
								   (value instanceof EmailOrganization) ||
								   (value instanceof Entry) ||
								   (value instanceof FeedbackTemplate) ||
								   (value instanceof GeneralRating) ||
								   (value instanceof Grade) ||
								   (value instanceof LogbookDocEntry) ||
								   (value instanceof LogpageDocEntry) ||
								   (value instanceof Organization) ||
								   (value instanceof OrganizationProperty) ||
								   (value instanceof Question) ||
								   (value instanceof QuestionRating) ||
								   (value instanceof QuestionReview) ||
								   (value instanceof QuestionScore) ||
								   (value instanceof Rating) ||
								   (value instanceof Review) ||
								   (value instanceof ReviewEntry) ||
								   (value instanceof ReviewerProperty) ||
								   (value instanceof ReviewingActivity) ||
								   (value instanceof ReviewReply) ||
								   (value instanceof ReviewTemplate) ||
								   (value instanceof Rubric) ||
								   (value instanceof Section) ||
								   (value instanceof TemplateReply) ||
								   (value instanceof User) ||
								   (value instanceof UserGroup) ||
								   (value instanceof WritingActivity);
		return isReviewerObject;   
	}
	
	/**
	 * Return the id of the object received as parameter
	 * @param object  owner of the id
	 * @return Long id of the object
	 */
	private static Long getObjectId(Object object){
		Long id = null;
		try{
			BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
			for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
				if (descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null) {
					String name = descriptor.getName();
					Object value = PropertyUtils.getProperty(object, name);
					if (name != null && name.equals(Constants.ID)){
						id = (Long) value;
						break;
					}
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return id;
	}
	
	private static Collection<Object> getConvertedObjectsCollection(String name, Object value, String include, String relationships, int level){
		Collection valuesCollection = (Collection) value;
		List emptyList = new ArrayList();
		if (value instanceof List){
			List<Object> valueList = (List<Object>) value;
			if ( !valueList.isEmpty() ) {
				if (valueList.get(0) instanceof String){
					return valuesCollection;	
				} else {
					return getConvertedObjectsList(name, valuesCollection,include, relationships, level);
				}
			} else {
				return emptyList;
			}
		} else if (value instanceof Set){
			Set<Object> valueSet = (Set<Object>) value;
			if (!valueSet.isEmpty()){
				Iterator it = valueSet.iterator();
				if (it.next() instanceof String){
					return valuesCollection;
				} else {
					return getConvertedObjectsList(name, valuesCollection,include, relationships, level);
				}
			} else {
				return emptyList;
			}
		} else {
			return getConvertedObjectsList(name,valuesCollection,include,relationships, level);
		}
	}
	
	private static List<Object> getConvertedObjectsList(String name, Collection<Object> values, String include, String relationships, int level){
		if (Constants.ALL.equals(include) && level < 2){	
			return convertCollectiontInList(values,include,relationships, level+1);
		} else {
			if (relationships!=null && name!= null && relationships.contains(name.toLowerCase()) && level < 2){
				Iterator it = values.iterator();
				List<Object> objects = new ArrayList<Object>();
				while(it.hasNext()){
					Object object = it.next();
					objects.add(convertObjectInMap(object, include, relationships,level+1));
				}
				return objects;
			} else {
				return convertRelationshipsInIds(values);
			} 
		}
	}
}
