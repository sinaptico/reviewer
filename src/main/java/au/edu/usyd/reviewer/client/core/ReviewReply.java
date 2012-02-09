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

@Entity
@DiscriminatorValue(ReviewingActivity.REVIEW_TYPE_TEMPLATE)
public class ReviewReply extends Review implements Serializable {

	private static final long serialVersionUID = 1L;
	@OneToMany
	@IndexColumn(name = "replyIndex")
	@Cascade(CascadeType.SAVE_UPDATE)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<TemplateReply> templateReplies = new ArrayList<TemplateReply>();
	
	
	public List<TemplateReply> getTemplateReplies() {
		return templateReplies;
	}
	public void setTemplateReplies(List<TemplateReply> templateReplies) {
		this.templateReplies = templateReplies;
	}
	
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
