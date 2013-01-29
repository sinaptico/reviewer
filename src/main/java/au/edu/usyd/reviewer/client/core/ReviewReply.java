package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import au.edu.usyd.reviewer.client.core.util.StringUtil;

/**
 * <p>Class that extends {@link Review Review} class and it is used for reviews with a predefined 'Template' (For example: A test template).
 * Templates define sections("Questions") and the "answers" to those questions are the list of 
 * List<{@link TemplateReply TemplateReply}> stored in this Class.
 * </p>
 */
@Entity
@DiscriminatorValue(ReviewingActivity.REVIEW_TYPE_TEMPLATE)
public class ReviewReply extends Review implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The template replies/answers to the review template sections. */
	@OneToMany
	@IndexColumn(name = "replyIndex")
//	@Cascade(CascadeType.SAVE_UPDATE)
	@Cascade(CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<TemplateReply> templateReplies = new ArrayList<TemplateReply>();
	
	
	/**
	 * Gets the template replies.
	 *
	 * @return the template replies
	 */
	public List<TemplateReply> getTemplateReplies() {
		return templateReplies;
	}
	
	/**
	 * Sets the template replies.
	 *
	 * @param templateReplies the new template replies
	 */
	public void setTemplateReplies(List<TemplateReply> templateReplies) {
		this.templateReplies = templateReplies;
	}
	
	/** 
	 * Check if all the answers given to the template are empty. (Return true if they are).
	 */
	@Override
	public boolean isBlank() {
		for (TemplateReply templateReply : templateReplies) {
			if (templateReply.getSection().getType() == Section.OPEN_QUESTION){
				if (StringUtil.isNotBlank(templateReply.getText())) {
					return false;
				}	
			}else{
				if (StringUtil.isNotBlank(templateReply.getChoice())) {
					return false;
				}
			}			
		}
		return StringUtil.isBlank(content);
	}
	
	public ReviewReply clone(){
		ReviewReply reviewReply = new ReviewReply();
		reviewReply.setContent(this.getContent());
		reviewReply.setEarlySubmitted(this.getEarlySubmitted());
		
		Set<FeedbackTemplate> templates = new HashSet<FeedbackTemplate>();
		for(FeedbackTemplate template : this.getFeedback_templates()){
			if (template != null){
				templates.add(template.clone());
			}
		}
		
		reviewReply.setFeedback_templates(templates);
		reviewReply.setFeedbackTemplateType(this.getFeedbackTemplateType());
		reviewReply.setId(this.getId());
		reviewReply.setSaved(this.getSaved());
		
		List<TemplateReply> replies = new ArrayList<TemplateReply>();
		for(TemplateReply reply :this.getTemplateReplies()){
			if (reply != null){
				replies.add(reply.clone());
			}
		}
		reviewReply.setTemplateReplies(replies);
		return reviewReply;
	}
}
