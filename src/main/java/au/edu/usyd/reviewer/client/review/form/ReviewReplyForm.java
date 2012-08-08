package au.edu.usyd.reviewer.client.review.form;

import java.util.ArrayList;
import java.util.List;

import au.edu.usyd.reviewer.client.core.Choice;
import au.edu.usyd.reviewer.client.core.ReviewReply;
import au.edu.usyd.reviewer.client.core.Section;
import au.edu.usyd.reviewer.client.core.TemplateReply;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ReviewReplyForm extends ReviewForm<ReviewReply> {
	private VerticalPanel mainPanel = new VerticalPanel();
	private TextArea contentField = new TextArea();
	private FlexTable questionFlexTable = new FlexTable();
	private String contentWidth = "700px";
	private String contentHeight = "500px";
	

	public ReviewReplyForm() {
		initWidget(mainPanel);
	}

	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		final List<HandlerRegistration> listHandlerRegistrations = new ArrayList<HandlerRegistration>();
		HandlerRegistration handlerRegistrationX;
		handlerRegistrationX = contentField.addChangeHandler(handler);
		listHandlerRegistrations.add(handlerRegistrationX);
		
//		for (TemplateReply tempReply : review.getTemplateReplies()) {			
//
//			if (tempReply.getSection().getType() == Section.OPEN_QUESTION) {
//				handlerRegistrationX = ((RichTextArea) questionFlexTable.getWidget(i, 0)).addKeyDownHandler((KeyDownHandler) handler);
//				//handlerRegistrationX =((HasChangeHandlers) questionFlexTable.getWidget(i, 0)).addChangeHandler(handler);
//			} else {
//				handlerRegistrationX = ((ListBox) questionFlexTable.getWidget(i,0)).addChangeHandler(handler);
//			}
//			listHandlerRegistrations.add(handlerRegistrationX);
//			i = i + 2;
//		}
		
		HandlerRegistration handlerRegistration = new HandlerRegistration() {
			@Override
			public void removeHandler() {
				
				for (HandlerRegistration handlerRegistration : listHandlerRegistrations){
					handlerRegistration.removeHandler();	
				}
			}
		};
		return handlerRegistration;
	}

	@Override
	public ReviewReply getReview() {
		int i=1;
		for (TemplateReply tempReply : review.getTemplateReplies()){

			if (tempReply.getSection().getType() == Section.OPEN_QUESTION){
				tempReply.setText(((RichTextArea)questionFlexTable.getWidget(i, 0)).getHTML());
			}else{
				int selectedChoice = ((ListBox)questionFlexTable.getWidget(i, 0)).getSelectedIndex();				
				tempReply.setChoice(((ListBox)questionFlexTable.getWidget(i, 0)).getValue(selectedChoice));
			}			
			
			i=i+2;
		}

		review.setContent(contentField.getText());
		return review;
	}	
	
	@Override
	public boolean isModified() {
		//return !inputbox1.getValue().equals(question1.getQuestion()) || !inputbox2.getValue().equals(question2.getQuestion()) || !inputbox3.getValue().equals(question3.getQuestion()) || !inputbox4.getValue().equals(question4.getQuestion()) || !inputbox5.getValue().equals(question5.getQuestion()) || !contentField.getValue().equals(review.getContent());
		return true;
	}

	@Override
	public void onLoad() {
		VerticalPanel contentPanel = new VerticalPanel();
		contentPanel.add(new HTML("Additional comments about the assignment:"));
		contentPanel.add(contentField);
		contentField.setSize("800px", "80px");
		questionFlexTable.setCellPadding(4);
		
		int i=0;
		for (TemplateReply tempReply : review.getTemplateReplies()){
			//questionFlexTable.setText(i, 0, tempReply.getSection().getText());
			//font-style:italic;
			String html = "<div style='font-style:italic; font-size:medium;'>"+tempReply.getSection().getText()+"</div>";
			questionFlexTable.setHTML(i, 0, html);
			i++;
			
			if (tempReply.getSection().getType() == Section.OPEN_QUESTION){
				RichTextArea inputbox = new RichTextArea();
				inputbox.setSize("800px", "240px");
				//inputbox.
				if (tempReply.getText() != null){
					inputbox.setHTML(tempReply.getText());
				}
				questionFlexTable.setWidget(i, 0, inputbox);
				i++;
			}
			
			if (tempReply.getSection().getType() == Section.MULTIPLE_CHOICE){
				ListBox list = new ListBox();
				for (Choice choice : tempReply.getSection().getChoices()){
					list.addItem(choice.getText());
				}
				if (tempReply.getChoice() != null){
					for( int j=0; j<list.getItemCount(); j++ )
					{
					  if (tempReply.getChoice().equalsIgnoreCase(list.getItemText(j))){
						  list.setSelectedIndex(j);
					  }
					}
				}				
				questionFlexTable.setWidget(i, 0, list);
				i++;
			}			
			
			if (tempReply.getSection().getType() == Section.SCALE){
				ListBox list = new ListBox();
				for (Choice choice : tempReply.getSection().getChoices()){
					list.addItem(choice.getNumber().toString());
				}
				if (tempReply.getChoice() != null){
					for( int j=0; j<list.getItemCount(); j++ )
					{
					  if (tempReply.getChoice().equalsIgnoreCase(list.getItemText(j))){
						  list.setSelectedIndex(j);
					  }
					}
				}					
				questionFlexTable.setWidget(i, 0, list);
				i++;
			}			
		}
		
		questionFlexTable.setWidget(i, 0, contentPanel);
		mainPanel.setSize(contentWidth, contentHeight);
		mainPanel.setSpacing(15);
		mainPanel.add(questionFlexTable);
	}

	@Override
	public void setLocked(boolean locked) {
		this.locked = locked;
		contentField.setEnabled(!locked);
		
		int i=1;
		for (TemplateReply tempReply : review.getTemplateReplies()){

			if (tempReply.getSection().getType() == Section.OPEN_QUESTION){
				((TextArea)questionFlexTable.getWidget(i, 0)).setEnabled(!locked);
			}else{
				((ListBox)questionFlexTable.getWidget(i, 0)).setEnabled(!locked);
			}			
			
			i=i+2;
		}
	}

	@Override
	public void setReview(ReviewReply review) {
		this.review = review;
		contentField.setText(this.review.getContent());		
	}
}