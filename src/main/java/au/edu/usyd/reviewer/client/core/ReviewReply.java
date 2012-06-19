package au.edu.usyd.reviewer.client.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
	@Cascade(CascadeType.SAVE_UPDATE)
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
}
