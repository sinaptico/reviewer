package au.edu.usyd.reviewer.client.admin;

import au.edu.usyd.reviewer.client.core.FeedbackTemplate;
import au.edu.usyd.reviewer.client.core.Review;
import au.edu.usyd.reviewer.client.core.ReviewEntry;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FeedbackTrackReport extends Composite {
	  private VerticalPanel mainPanel = new VerticalPanel();
	  private ReviewingActivity reviewingActivity;
	  
      public FeedbackTrackReport(ReviewingActivity reviewingActivity) {
      	initWidget(mainPanel);
      	this.reviewingActivity = reviewingActivity;;
      }		
      
      
  	@Override
  	public void onLoad() {
  		Grid trackReportGrid = new Grid(reviewingActivity.getEntries().size()+1, 10);
  		trackReportGrid.setWidget(0, 0, new Label("Unikey;"));
  		trackReportGrid.setWidget(0, 1, new Label("Feedback;"));
  		trackReportGrid.setWidget(0, 2, new Label("Rubric Details;"));
  		
  		int i=1;	    		
  		for (ReviewEntry reviewEntry : reviewingActivity.getEntries()) {
  			trackReportGrid.setWidget(i, 0, new Label(reviewEntry.getDocEntry().getOwner().getId()));
  			Review review = reviewEntry.getReview();
  			if (review.getContent()!=null){
  				trackReportGrid.setWidget(i, 1, new HTML(review.getContent()+";"));
  			}
  			if (review.getFeedback_templates().size()>0){
  				Grid rubricsGrid = new Grid(review.getFeedback_templates().size()+1, 3);
  				rubricsGrid.setWidget(0, 0, new Label("Rubric;"));
  				rubricsGrid.setWidget(0, 1, new Label("Grade;"));	    				
  				rubricsGrid.setWidget(0, 2, new Label("Description;"));
  				int j=1;	    				

  				for (FeedbackTemplate feedbackTemplate: review.getFeedback_templates()){	    				
  					rubricsGrid.setWidget(j, 0, new Label(feedbackTemplate.getNumber()+";"));
  					rubricsGrid.setWidget(j, 1, new Label(feedbackTemplate.getGrade()+";"));	
  					if (review.getFeedbackTemplateType().equalsIgnoreCase(FeedbackTemplate.FEEDBACK_TYPE_DESCRIPTION_A)){
  						rubricsGrid.setWidget(j, 2, new HTML(feedbackTemplate.getDescriptionA()+";"));
  					}
  					if (review.getFeedbackTemplateType().equalsIgnoreCase(FeedbackTemplate.FEEDBACK_TYPE_DESCRIPTION_B)){
  						rubricsGrid.setWidget(j, 2, new HTML(feedbackTemplate.getDescriptionB()+";"));
  					}
  					j++;
  				}
  				
  				trackReportGrid.setWidget(i, 2, rubricsGrid);
  			}
  			
  			i++;	    			
  		}
  		
  		mainPanel.add(trackReportGrid);
  		mainPanel.add(new Button("Close", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					((DialogBox) mainPanel.getParent().getParent().getParent().getParent()).hide();
				}
			}));	    		
  	}
}