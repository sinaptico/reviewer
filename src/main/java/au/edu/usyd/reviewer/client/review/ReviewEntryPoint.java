package au.edu.usyd.reviewer.client.review;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import au.edu.usyd.reviewer.client.core.Activity;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.FeedbackTemplate;
import au.edu.usyd.reviewer.client.core.Rubric;
import au.edu.usyd.reviewer.client.core.DocumentType;
import au.edu.usyd.reviewer.client.core.GeneralRating;
import au.edu.usyd.reviewer.client.core.Grade;
import au.edu.usyd.reviewer.client.core.QuestionRating;
import au.edu.usyd.reviewer.client.core.QuestionReview;
import au.edu.usyd.reviewer.client.core.Rating;
import au.edu.usyd.reviewer.client.core.Review;
import au.edu.usyd.reviewer.client.core.ReviewEntry;
import au.edu.usyd.reviewer.client.core.ReviewReply;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.gwt.PDFWidget;
import au.edu.usyd.reviewer.client.core.gwt.SubmitButton;
import au.edu.usyd.reviewer.client.core.gwt.WidgetFactory;
import au.edu.usyd.reviewer.client.core.util.StyleLib;
import au.edu.usyd.reviewer.client.core.util.UrlLib;
import au.edu.usyd.reviewer.client.review.form.CommentsReviewForm;
import au.edu.usyd.reviewer.client.review.form.GeneralRatingForm;
import au.edu.usyd.reviewer.client.review.form.GradesForm;
import au.edu.usyd.reviewer.client.review.form.QuestionRatingForm;
import au.edu.usyd.reviewer.client.review.form.QuestionReviewForm;
import au.edu.usyd.reviewer.client.review.form.ReviewReplyForm;
import au.edu.usyd.reviewer.client.review.form.ReviewForm;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLFlow;  
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickEvent;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickHandler;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;


public class ReviewEntryPoint implements EntryPoint {
	final CheckBox addFailLink = WidgetFactory.createNewCheckBoxWithId("addFailLink","addFailLink", "");  
    final CheckBox addPassLink = WidgetFactory.createNewCheckBoxWithId("addPassLink","addPassLink", "");  
    final CheckBox addCreditLink = WidgetFactory.createNewCheckBoxWithId("addFailLink","addCreditLink", "");  
    final CheckBox addDistinctionLink = WidgetFactory.createNewCheckBoxWithId("addPassLink","addDistinctionLink", "");  
    final CheckBox addHighLink = WidgetFactory.createNewCheckBoxWithId("addPassLink","addHighLink", "");

	private ReviewServiceAsync reviewService = (ReviewServiceAsync) GWT.create(ReviewService.class);
	ReviewForm<?> reviewForm;

	@Override
	public void onModuleLoad() {
		String view = Window.Location.getParameter("view");
		String edit = Window.Location.getParameter("edit");
		String docId = Window.Location.getParameter("docId");
		//String docVersion = Window.Location.getParameter("docVersion");
		final String cssTextStyle = "STYLE='font: normal 15px Arial,Helvetica,sans-serif; margin: 0 0 -5 -20;'"; 
		final String cssH1Style = "STYLE='color: #CE1126; margin: 0 0 0 0; font-weight: normal; clear: left; font-size: 1.3em;'";

		final HTML titleHTML = new HTML();
		final HTML firstStepTitle = new HTML ("<ul> <li "+cssTextStyle+"><b> Read the document. </b></li> </ul>");
		final HTML thirdStepTitle = new HTML ("<ul> <li "+cssTextStyle+"><b> Write and save feedback. </b></li> </ul>");
		final DecoratorPanel contentPanel = new DecoratorPanel();
		final DisclosurePanel ratingPanel = new DisclosurePanel();
		final VerticalPanel docLinkPanel = new VerticalPanel();
		final HorizontalPanel buttonsPanel = new HorizontalPanel();
		final VerticalPanel submitPanel = new VerticalPanel();
		final VerticalPanel gradesPanel = new VerticalPanel();
		final VerticalPanel templateGridsPanel = new VerticalPanel();
		final VerticalPanel feedbackBrowser = new VerticalPanel();


		if (view != null) {
			reviewService.getUserReviewForViewing(Long.valueOf(view), new AsyncCallback<Course>() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Failed to get review: " + caught.getMessage());
				}

				@Override
				public void onSuccess(Course course) {
					// review form
					WritingActivity writingActivity = course.getWritingActivities().iterator().next();
					DocEntry docEntry = writingActivity.getEntries().iterator().next();
					final Review review = docEntry.getReviews().iterator().next();
					titleHTML.setHTML("<h1>"+ course.getName() +" - Review of "+ writingActivity.getName() + "</h1>");
					
					if (review instanceof QuestionReview) {
						reviewForm = new QuestionReviewForm();
						((QuestionReviewForm) reviewForm).setReview((QuestionReview) review);
					} else {
						if (review instanceof ReviewReply) {
							reviewForm = new ReviewReplyForm();
							((ReviewReplyForm) reviewForm).setReview((ReviewReply) review);
						} else{
							reviewForm = new CommentsReviewForm();
							((CommentsReviewForm) reviewForm).setReview(review);
						}
					}
					contentPanel.setWidget(reviewForm);
					reviewForm.setLocked(true);

					// rating form
					if (writingActivity.getReviewingActivities().get(0).getRatings()) {
						final GeneralRatingForm generalRatingForm = new GeneralRatingForm();
						reviewService.getUserRatingForEditing(review, new AsyncCallback<Rating>() {
							@Override
							public void onFailure(Throwable caught) {
								GeneralRating rating = new GeneralRating();
								generalRatingForm.setRating(rating);
							}

							@Override
							public void onSuccess(Rating rating) {
								generalRatingForm.setRating((GeneralRating) rating);
							}
						});
						final SubmitButton rateButton = new SubmitButton();
						rateButton.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								GeneralRating rating = generalRatingForm.getRating();
								if (rating.getOverallScore() <= 0 || rating.getEvidenceScore() <= 0 || rating.getUsefulnessScore() <= 0 || rating.getContentScore() <= 0) {
									Window.alert("Please input a rating.");
									return;
								}
								rateButton.updateStateSubmitting();
								reviewService.submitRating(rating, review, new AsyncCallback<Rating>() {
									@Override
									public void onFailure(Throwable caught) {
										Window.alert("Failed to save review: " + caught.getMessage());
										rateButton.updateStateSubmit();
									}

									@Override
									public void onSuccess(Rating rating) {
										Window.alert("Your rating has been successfully submitted.");
										rateButton.updateStateSubmit();
									}
								});
							}
						});

						VerticalPanel ratingFormPanel = new VerticalPanel();
						ratingFormPanel.add(generalRatingForm);
						ratingFormPanel.add(new HTML("<br/>"));
						ratingFormPanel.add(rateButton);
						ratingPanel.setHeader(new HTML("<br/><h2>&gt; Rate this review</h2>"));
						ratingPanel.setContent(ratingFormPanel);
					}
				}
			});
		} else if (edit != null) {
			reviewService.getUserReviewForEditing(Long.valueOf(edit), new AsyncCallback<Course>() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Failed to get review: " + caught.getMessage());
				}

				@Override
				public void onSuccess(final Course course) {
					WritingActivity writingActivity = null; //course.getWritingActivities().iterator().next();
					
					for (Iterator iterator = course.getWritingActivities().iterator(); iterator.hasNext();) {						
						writingActivity = (WritingActivity) iterator.next();						
						if (writingActivity != null){
							break;
						}
					}
					
					ReviewingActivity rwActivity = null;
					//final ReviewingActivity reviewingActivity = null; //writingActivity.getReviewingActivities().iterator().next();					
					for (Iterator iterator = writingActivity.getReviewingActivities().iterator(); iterator.hasNext();) {						
					    rwActivity = (ReviewingActivity) iterator.next();						
						if (rwActivity != null){
							break;
						}
					}					
								
					final ReviewingActivity reviewingActivity = rwActivity;
					// review form
					final ReviewEntry reviewEntry = reviewingActivity.getEntries().iterator().next();	
					Review review = reviewEntry.getReview();
					final DocEntry docEntry = reviewEntry.getDocEntry();						
					//StyleLib.longDateFormat(writingActivity.getReviewingActivities().get(0).getFinishDate())										
					titleHTML.setHTML("<h1 "+cssH1Style+">YOU ARE NOW REVIEWING: " + writingActivity.getName().toUpperCase() + "</h1> <a href='Assignments.html'><< Back to the Assignments List</a></br></br> "+"Deadline: "+StyleLib.dueDateFormat(reviewingActivity.getFinishDate())+"</br></br>");
					
					//final ReviewForm<?> reviewForm;
					if (review instanceof QuestionReview) {
						reviewForm = new QuestionReviewForm();
						((QuestionReviewForm) reviewForm).setReview((QuestionReview) review);
					} else {
						if (review instanceof ReviewReply) {
							reviewForm = new ReviewReplyForm();
							((ReviewReplyForm) reviewForm).setReview((ReviewReply) review);
						} else {						
							reviewForm = new CommentsReviewForm();
							((CommentsReviewForm) reviewForm).setReview(review);
						}
					}
					contentPanel.setWidget(reviewForm);
					
					// document Link
					HorizontalPanel documentLink = new HorizontalPanel();
					documentLink.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
					documentLink.setWidth("100%");


					documentLink.add(firstStepTitle);
					//writingActivity.getReviewingActivities().get(0).getStartDate().getId()					
					//documentLink.add(new PDFWidget(docEntry.getTitle(), writingActivity.getName(), docEntry.getDocumentId(),reviewingActivity.getStartDate().getId()));
					docLinkPanel.add(documentLink);
					PDFWidget pdfLink = new PDFWidget(docEntry.getTitle(), writingActivity.getName(), docEntry.getDocumentId(),reviewingActivity.getStartDate().getId());
 
					HorizontalPanel pdfDocLink = new HorizontalPanel();
					pdfDocLink.add(new HTML("<span style='margin-left:20px;'></span>"));
					pdfDocLink.add(pdfLink);
					docLinkPanel.add(pdfDocLink);
					
					// glosser link
					if (!writingActivity.getGlosserSite().equals(WritingActivity.GLOSSER_SITE_NONE)) {
						Anchor glosserLink = new Anchor();
						glosserLink.setHref(UrlLib.glosserUrl(writingActivity.getGlosserSite(), docEntry.getDocumentId()));
						//glosserLink.setHTML("<img style='margin-left:20px; margin-top:-4px;' height='19px' src='images/glosser.png'> <span>(View this document in Glosser)</span></img>");
						glosserLink.setHTML("<img style='margin-left:5px; margin-top:16px;' height='0px' src='images/glosser.png'><span>(View this document in Glosser)</span></img>");
						glosserLink.setTitle("Automatic Feedback");
						glosserLink.setTarget("_blank");
						//documentLink.add(glosserLink);
						pdfDocLink.add(glosserLink);
					}					
					

					
					// grades form
					List<User> users = new LinkedList<User>();
					if(docEntry.getOwner() != null) {
						users.add(docEntry.getOwner());
					} else {
						users.addAll(docEntry.getOwnerGroup().getUsers());
					}
					List<Grade> grades = new LinkedList<Grade>();
					STUDENT_LOOP: for(User user : users) {
						for(Grade grade : writingActivity.getGrades()) {
							if(grade.getDeadline().equals(reviewingActivity.getStartDate()) && grade.getUser().equals(user)) {
								grades.add(grade);
								break STUDENT_LOOP;
							}
						}
						Grade grade = new Grade();
						grade.setDeadline(reviewingActivity.getStartDate());
						grade.setUser(user);
						grade.setValue(0.0);
						grades.add(grade);
					}
					final GradesForm gradesForm = new GradesForm(grades);
						
					//if (reviewingActivity.getStudentMarks()){
					if(course.getLecturers().contains(reviewEntry.getOwner()) || course.getTutors().contains(reviewEntry.getOwner())){
					  gradesPanel.add(new HTML("<ul> <li "+cssTextStyle+"><b> Give it a mark/grade out of " + reviewingActivity.getStartDate().getMaxGrade() + ".</b></li> </ul>"));
					  gradesPanel.add(gradesForm);						
					}
					
					
					// save review button
					final SubmitButton saveButton = new SubmitButton("Save", "Saving...", "Saved");	
					final HTML saveDate = new HTML();
					
					saveButton.setWidth("100");
					//saveButton.setText("Save");
					saveButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							saveButton.updateStateSubmitting();

							// save grades and review
							boolean validMarks = true;
							List<Grade> grades = null;
							try {
								grades = gradesForm.getGrades();
							} catch (Exception e) {
								validMarks = false;
								saveButton.updateStateSubmit();
								Window.alert("Unable to save marks. "+ e.getMessage());								
							}

							if(validMarks){
								if(course.getLecturers().contains(reviewEntry.getOwner()) || course.getTutors().contains(reviewEntry.getOwner())){
									reviewService.submitGrades(grades, new AsyncCallback<Collection<Grade>>(){
										@Override
										public void onFailure(Throwable caught) {
											Window.alert("Failed to save marks: " + caught.getMessage());
										}

										@Override
										public void onSuccess(Collection<Grade> grades) {
											// TODO Auto-generated method stub
										}});
								}
								reviewService.saveReview(reviewForm.getReview(), new AsyncCallback<Review>() {
									@Override
									public void onFailure(Throwable caught) {
										Window.alert("Failed to save review: " + caught.getMessage());
										saveButton.updateStateSubmit();
									}

									@Override
									public void onSuccess(Review review) {
										(reviewForm.getReview()).setSaved(review.getSaved());
										saveDate.setHTML("<div STYLE='margin: 3 0 0 3em;'>Saved " + StyleLib.dueDateFormat(review.getSaved())+"</div>");
										saveButton.updateStateSubmitted();
									}
								});								
							}
						}
					});
					

					// submit review button
					final SubmitButton submitButton = new SubmitButton();					

					submitButton.setWidth("100");
					submitButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {

							if (Window.confirm("Are you sure you want to submit this review? \n *Once submitted you won't be able to make any changes to it.")) {
								submitButton.updateStateSubmitting();

								// save grades and submit review
								boolean validMarks = true;
								List<Grade> grades = null;
								try {
									grades = gradesForm.getGrades();
								} catch (Exception e) {
									validMarks = false;
									submitButton.updateStateSubmit();
									Window.alert("Unable to submit marks. "+ e.getMessage());								
								}

								if(validMarks){
									if(course.getLecturers().contains(reviewEntry.getOwner()) || course.getTutors().contains(reviewEntry.getOwner())){
										reviewService.submitGrades(grades, new AsyncCallback<Collection<Grade>>(){
											@Override
											public void onFailure(Throwable caught) {
												Window.alert("Failed to submit marks: " + caught.getMessage());
											}

											@Override
											public void onSuccess(Collection<Grade> grades) {
												// TODO Auto-generated method stub
											}});
									}
									reviewService.submitReview(reviewForm.getReview(), new AsyncCallback<Review>() {
										@Override
										public void onFailure(Throwable caught) {
											Window.alert("Failed to submit review: " + caught.getMessage());
											submitButton.updateStateSubmit();
										}

										@Override
										public void onSuccess(Review review) {
											(reviewForm.getReview()).setSaved(review.getSaved());
											saveDate.setHTML("<div STYLE='margin: 3 0 0 3em;'>Submitted " + StyleLib.dueDateFormat(review.getSaved())+"</div>");
											submitButton.updateStateSubmitted();
											Window.Location.reload();
										}
									});								
								}
							}
						}
					});					

					
					reviewForm.addChangeHandler(new ChangeHandler() {
						@Override
						public void onChange(ChangeEvent event) {
							if (reviewForm.isModified()) {
								//submitButton.updateStateSubmit();
								saveButton.updateStateSubmit();
							}
						}
					});

					gradesForm.addChangeHandler(new ChangeHandler() {
						@Override
						public void onChange(ChangeEvent event) {
							//submitButton.updateStateSubmit();
							saveButton.updateStateSubmit();
						}
					});

					// buttons panel
					//buttonsPanel.add(saveDate);					
					if (review.getSaved() != null) {
						saveDate.setHTML("<div STYLE='margin: 3 0 0 3em;'>Saved " + StyleLib.dueDateFormat(review.getSaved())+"</div>");
						//submitButton.updateStateSubmitted();
						saveButton.updateStateSubmitted();
					} else {
						//submitButton.updateStateSubmit();
						saveButton.updateStateSubmit();
					}
					
					//writingActivity.getReviewingActivities().get(0).getStatus()
					if (reviewingActivity.getStatus() < Activity.STATUS_FINISH || isCourseInstructor(course, reviewEntry)) {
						if(!isCourseInstructor(course, reviewEntry) && (review.getEarlySubmitted())){
							reviewForm.setLocked(true);
						} else{
							submitPanel.setWidth("100%");
							submitPanel.add(thirdStepTitle);
							//submitPanel.add(saveDate);
							
							
							/***************************  CELL BROWSER **********************************/
							final WritingActivity writingActivityForMail = writingActivity;
							reviewService.getDocumentTypes(new AsyncCallback<Collection<DocumentType>>() {

								@Override
								public void onFailure(Throwable caught) {
									// TODO Auto-generated method stub
									
								}

								@Override
								public void onSuccess(Collection<DocumentType> documentTypes) {
								    final SectionStack sectionStack = new SectionStack();  
							        sectionStack.setVisibilityMode(VisibilityMode.MULTIPLE);  
							        sectionStack.setWidth("895px"); 
							        sectionStack.setHeight("25px");
								        
									final SectionStackSection section1 = new SectionStackSection("Show <b>SpeedBack</b> options");
							        section1.setID("SPEEDBACK_OPTIONS");
							        section1.setCanCollapse(true);  
							        
							        final Tree gridTree = new Tree();
							        gridTree.setModelType(TreeModelType.PARENT);
							        gridTree.setNameProperty("Name");
							        
							        RubricsTreeNode rootNode = new RubricsTreeNode((long) 0, "DocumentType", null, null, "DocumentType", 0, 0, "", "", buildRubricsTree(documentTypes));
						        	gridTree.setRoot(rootNode);
									gridTree.getRoot().setCanAcceptDrop(false);	

							        final RubricsTreeGrid rubricsGrid = new RubricsTreeGrid();
							        rubricsGrid.setData(gridTree);
							        //rubricsGrid.getTree().openAll();							        
							        
							        VStack grids = new VStack();
							        //grids.setHeight("283px");
							        grids.addMember(rubricsGrid);
							        
							        
							        final HorizontalPanel insertTextPanel = new HorizontalPanel();
							        final SubmitButton insertSelectedTextButton = new SubmitButton("Insert selected rubrics", "Inserting...", "Inserted");
								    insertSelectedTextButton.setWidth("180px");
								    insertTextPanel.setHeight("25px");
								    insertTextPanel.add(insertSelectedTextButton);
								    
							        addFailLink.setValue(true);
							        addPassLink.setValue(true);
							        
							        Grid gridInsterLinks = new Grid(1, 19);
							        gridInsterLinks.setWidth("696px");							        
							        gridInsterLinks.setWidget(0, 3, new Label("Insert tutorial links for:"));
							        
							        gridInsterLinks.setWidget(0, 4, addFailLink);
							        gridInsterLinks.setWidget(0, 5, new Label("Fail"));
							        gridInsterLinks.setWidget(0, 6, new Label(" - "));
									gridInsterLinks.setWidget(0, 7, addPassLink);
									gridInsterLinks.setWidget(0, 8, new Label("Pass"));
									gridInsterLinks.setWidget(0, 9, new Label(" - "));
									gridInsterLinks.setWidget(0, 10, addCreditLink);
									gridInsterLinks.setWidget(0, 11, new Label("Credit"));
									gridInsterLinks.setWidget(0, 12, new Label(" - "));
									gridInsterLinks.setWidget(0, 13, addDistinctionLink);
									gridInsterLinks.setWidget(0, 14, new Label("Distinction"));
									gridInsterLinks.setWidget(0, 15, new Label(" - "));
									gridInsterLinks.setWidget(0, 16, addHighLink);
									gridInsterLinks.setWidget(0, 17, new Label("High Distinction"));
								    
									insertTextPanel.add(gridInsterLinks);
									
								    grids.addMember(insertTextPanel);							        
							        grids.draw();							        

								    templateGridsPanel.setWidth("100%");								    
								    templateGridsPanel.setBorderWidth(1);
								    templateGridsPanel.add(grids);
								    
							        
								    insertSelectedTextButton.addClickHandler(new ClickHandler() {
										@Override
										public void onClick(ClickEvent event) {
											insertSelectedTextButton.updateStateSubmitting();

											ListGridRecord[] records = rubricsGrid.getSelection();
											
											Review tmpReview = ((CommentsReviewForm) reviewForm).getReview();
											if (tmpReview.getContent().isEmpty() || tmpReview.getContent().equalsIgnoreCase("<br>")){
												tmpReview.setContent(buildFeedbackWithRubrics(records, docEntry, reviewEntry, writingActivityForMail, reviewingActivity));
												((CommentsReviewForm) reviewForm).setReview(tmpReview);
											}else{
												if (Window.confirm("Do you want to overwrite the current content of your feedback summary?")) {
													tmpReview.setContent(buildFeedbackWithRubrics(records, docEntry, reviewEntry, writingActivityForMail, reviewingActivity));
													((CommentsReviewForm) reviewForm).setReview(tmpReview);
												}
											}
											insertSelectedTextButton.updateStateSubmit();
											saveButton.updateStateSubmit();
										}

									});								    
								    
								    
								    sectionStack.addSection(section1);							        
							        sectionStack.addSectionHeaderClickHandler(new SectionHeaderClickHandler() {
							            @Override
							            public void onSectionHeaderClick(SectionHeaderClickEvent sectionHeaderClickEvent) {							            	
							                if (sectionHeaderClickEvent.getSection().getID().equalsIgnoreCase("SPEEDBACK_OPTIONS") && sectionStack.sectionIsExpanded("SPEEDBACK_OPTIONS")) {
							                	section1.setTitle("Show <b>SpeedBack</b> options");
							                	feedbackBrowser.remove(templateGridsPanel);
							               }else{
							            	   section1.setTitle("<b>SpeedBack</b> options (All the rubircs you select will be inserted in your feedback summary below where you can edit them)");
							            	   feedbackBrowser.add(templateGridsPanel);
							               }
							            }
							        });	
							        
							        feedbackBrowser.add(sectionStack);
							        section1.setExpanded(false);  
								}

							});					        
					  
							submitPanel.add(feedbackBrowser);
							/****************************************************************************/								

							buttonsPanel.add(saveButton);
							if (reviewingActivity.getEarlySubmit()){	
								buttonsPanel.add(new HTML("<BR>"));
								buttonsPanel.add(submitButton);
							}
							
							buttonsPanel.add(saveDate);
							
							submitPanel.add(new HTML("<BR>"));
							submitPanel.add(buttonsPanel);							
							submitPanel.add(new HTML("<hr/>"));
						}
					} else {
						reviewForm.setLocked(true);
					}

					// check if review is saved before closing window
					Window.addWindowClosingHandler(new Window.ClosingHandler() {
						@Override
						public void onWindowClosing(ClosingEvent event) {
							if (reviewForm.isModified()) {
								//event.setMessage("This review has unsaved changes. Would you like to leave this page and discard your changes?");
								event.setMessage("You have unsaved changes that will be lost.");
								
							}
						}
					});
				}

				private boolean isCourseInstructor(Course course,
						ReviewEntry reviewEntry) {
					if (course.getLecturers().contains(reviewEntry.getOwner()) 
							|| course.getTutors().contains(reviewEntry.getOwner()) 
								|| course.getSupervisors().contains(reviewEntry.getOwner()) 
									|| course.getAutomaticReviewers().contains(reviewEntry.getOwner())){
						return true;
					}
					return false;
				}
			});
		} else if (docId != null) {
			reviewService.getQuestionRating(docId, new AsyncCallback<QuestionRating>() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Failed to get question rating: " + caught.getMessage());
				}

				@Override
				public void onSuccess(QuestionRating rating) {
					QuestionRatingForm ratingForm = new QuestionRatingForm();
					ratingForm.setRating(rating);
					contentPanel.setWidget(ratingForm);
				}
			});
		}

		VerticalPanel menuPanel = new VerticalPanel();
		menuPanel.setWidth("100%");
		menuPanel.add(titleHTML);		
		menuPanel.add(docLinkPanel);		
		
		VerticalPanel gradePanel = new VerticalPanel();
		gradePanel.setWidth("100%");
		gradePanel.add(gradesPanel);
		
		VerticalPanel contentsPanel = new VerticalPanel();
		contentsPanel.setWidth("100%");
		contentsPanel.add(submitPanel);		
		contentsPanel.add(contentPanel);			
		
		VerticalPanel reviewContent = new VerticalPanel();
		reviewContent.setSpacing(5);
		reviewContent.add(menuPanel);		
		reviewContent.add(gradePanel);
		reviewContent.add(contentsPanel);
		reviewContent.add(ratingPanel);
		

		VerticalPanel reviewPanel = new VerticalPanel();
		reviewPanel.setSize("100%", "920px");
		reviewPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		reviewPanel.add(reviewContent);
		RootPanel.get("reviewPanel").add(reviewPanel);
	}
	
		  public static class RubricsTreeGrid extends TreeGrid {
		        public RubricsTreeGrid() {
		            setWidth("887px");
		            setHeight("256px");
		            setShowEdges(true);
		            setBorder("0px");
		            setBodyStyleName("normal");
		            setShowHeader(false);
		            setLeaveScrollbarGap(false);
		            setEmptyMessage("<br>There's no rubrics uploaded into iWrite");
		            setSelectionAppearance(SelectionAppearance.CHECKBOX);
		            setAttribute("selectionProperty", "isSelected", false);
		            setAttribute("Link", "", true);
		            setAttribute("Text", "", true);
		            setAttribute("IsTemplate", "", true);
		            setAttribute("IsRubric", "", true);
		            setAttribute("IsDoctype", "", true);
		            setAttribute("GradeNum", "", true);
		            setAttribute("DescriptionA", "", true);
		            setAttribute("DescriptionB", "", true);
		            setAttribute("Id", "", true);
		            setShowSelectedStyle(false);  
		            setShowPartialSelection(true);  
		            setCascadeSelection(true);  
		            setFixedFieldWidths(false);			            		            
		        }
		    }

		    public static class RubricsTreeNode extends TreeNode {
		        
		        public RubricsTreeNode(Long Id, String name, String link, String icon, String text, int type, int gradeNum, String descriptionA, String descriptionB, RubricsTreeNode... children) {
		        	setAttribute("Name", name);
		            setAttribute("Text", text);
		            setAttribute("Id", Id);
		            
		            if (type == 0)//DocType
		            	setAttribute("IsDoctype", true);
		            
		            if (type == 1)//Rubric
		            	setAttribute("IsRubric", true);
		            
		            if (type == 2)//Template
		            	setAttribute("IsTemplate", true);
		            
		            if (link != null)
		            	setAttribute("Link", link);
		            		            
		            if (icon != null)
		            	setAttribute("icon", icon);
		            
		            if (gradeNum != 0)
		            	setAttribute("GradeNum",gradeNum);
		            
		            if (descriptionA != null){
		            	setAttribute("DescriptionA",descriptionA);
		            }else{
		            	setAttribute("DescriptionA","");
		            }
		            	
		            if (descriptionB != null){
		            	setAttribute("DescriptionB",descriptionB);
		            }else{
		            	setAttribute("DescriptionB","");
		            }		            
		            
		            if (children != null)
		            	setAttribute("children", children);		            		           		            
		        }
		    }	
		    
			private String buildFeedbackWithRubrics(ListGridRecord[] records, DocEntry docEntry, ReviewEntry reviewEntry, WritingActivity writingActivity, ReviewingActivity reviewingActivity) {
				List<String> selectedRubrics =new ArrayList<String>();
				String test = "";
				int i=0;
				String typeFeedbackTemplate = FeedbackTemplate.FEEDBACK_TYPE_DESCRIPTION_DEFAULT;
				
				if (reviewingActivity.getFeedbackTemplateType().equalsIgnoreCase(FeedbackTemplate.FEEDBACK_TYPE_DESCRIPTION_A)){
					typeFeedbackTemplate = FeedbackTemplate.FEEDBACK_TYPE_DESCRIPTION_A;					
				}
				if (reviewingActivity.getFeedbackTemplateType().equalsIgnoreCase(FeedbackTemplate.FEEDBACK_TYPE_DESCRIPTION_B)){
					typeFeedbackTemplate = FeedbackTemplate.FEEDBACK_TYPE_DESCRIPTION_B;
				}
				
				reviewForm.getReview().setFeedbackTemplateType(typeFeedbackTemplate);
				
				reviewForm.getReview().getFeedback_templates().clear();
				for (int j = 0; j < records.length; j++) {
					if (records[j].getAttributeAsBoolean("IsRubric")){
						if (i==0){
							selectedRubrics.add("With regards to <b>" + records[j].getAttribute("Text").toLowerCase() + "</b>, I think ");
							i++;
						}else{
							selectedRubrics.add("The <b>"+records[j].getAttribute("Text").toLowerCase()+"</b> ");
						}
					}					
					if (records[j].getAttributeAsBoolean("IsTemplate")){
						String templateFeedbackToInsert = "";
						if (typeFeedbackTemplate.equalsIgnoreCase(FeedbackTemplate.FEEDBACK_TYPE_DESCRIPTION_A)){
							templateFeedbackToInsert = records[j].getAttribute("DescriptionA").toLowerCase();
						}
						if (typeFeedbackTemplate.equalsIgnoreCase(FeedbackTemplate.FEEDBACK_TYPE_DESCRIPTION_B)){
							templateFeedbackToInsert = records[j].getAttribute("DescriptionB").toLowerCase();
						}
						if (templateFeedbackToInsert.isEmpty()){
							templateFeedbackToInsert = records[j].getAttribute("Text").toLowerCase();
						}												
						
						selectedRubrics.add(templateFeedbackToInsert+". ");
						String suggest = "I suggest you do the online tutorial on this topic available at: ";
						if (records[j].getAttributeAsString("Link") != null && !records[j].getAttributeAsString("Link").isEmpty()){
							if (checkGradeNum(records[j].getAttributeAsInt("GradeNum"))){
								String link = "<a href="+records[j].getAttributeAsString("Link")+">"+records[j].getAttributeAsString("Link")+"</a>";
								selectedRubrics.add(suggest+link+". <br>");	
							}
							
						}
					}
					if (!records[j].getAttribute("Id").equalsIgnoreCase("0")){
						FeedbackTemplate feedbackTemplate = new FeedbackTemplate();
						feedbackTemplate.setId(Long.valueOf(records[j].getAttribute("Id")));
						reviewForm.getReview().getFeedback_templates().add(feedbackTemplate);
					}
				}			
				
				String finalContent = test+" Dear"+ docEntry.getTitle().substring(docEntry.getTitle().indexOf(",")+1)+ "<br /><br />";
				String submitedDate = "";
				
				if (docEntry.getEarlySubmitDate() != null){
					submitedDate = StyleLib.submitDateFormat(docEntry.getEarlySubmitDate());
				}else{
					for (Deadline deadline: writingActivity.getDeadlines()){
						if (deadline.getStatus() == 2){ 
							submitedDate = StyleLib.submitDateFormat(deadline.getFinishDate());
						}
					}
				}
				finalContent = finalContent + "After reading the assignment you submitted on "+ submitedDate +", I would like to provide you with some extra feedback on the different aspects of your work that were assessed: <br /><br />";
				
				for (String rubric : selectedRubrics){ finalContent = finalContent + rubric; }
				
				finalContent = finalContent.replace(" .", ".").replace("..", ".") + "<br/><br/> I hope you can use this feedback at a later stage. <br/><br/> Regards, <br />"+reviewEntry.getOwner().getFirstname()+" "+reviewEntry.getOwner().getLastname();

				return finalContent;
			}	
			
			private boolean checkGradeNum(Integer gradeNum) {
				boolean result =false;
				
				if ((gradeNum == 49) && (addFailLink.getValue()))
					result = true;
				
				if ((gradeNum == 64) && (addPassLink.getValue()))
					result = true;
				
				if ((gradeNum == 74) && (addCreditLink.getValue()))
					result = true;
				
				if ((gradeNum == 84) && (addDistinctionLink.getValue()))
					result = true;
				
				if ((gradeNum == 85) && (addHighLink.getValue()))
					result = true;
				
				return result;
			}
			
			
			private RubricsTreeNode[] buildRubricsTree(Collection<DocumentType> documentTypes) {
				RubricsTreeNode[] documentTypeNodes = new RubricsTreeNode[documentTypes.size()];
				int i=0;
		        for (DocumentType docType: documentTypes){							        
		        	List<Rubric> rubrics = docType.getRubrics();

		        	RubricsTreeNode[] rubricNodes = new RubricsTreeNode[rubrics.size()];
		        	int j=0;
		        	
		        	String strToDisplay;
		        	String strTextContent;
		        	
		        	for (Rubric rubric: rubrics){
		        		RubricsTreeNode[] feedbackTemplateNodes = new RubricsTreeNode[rubric.getFeedbackTemplates().size()];
		        		int k=0;
		        		for (FeedbackTemplate feedbackTemplate : rubric.getFeedbackTemplates()){
		        			strToDisplay = feedbackTemplate.getNumber()+" <b>("+feedbackTemplate.getGrade()+")</b> - "+feedbackTemplate.getText();
				        	strTextContent = feedbackTemplate.getText();									        								   	
		        			feedbackTemplateNodes[k] = new RubricsTreeNode(feedbackTemplate.getId(), strToDisplay, rubric.getLink(), "text.png", strTextContent, 2, feedbackTemplate.getGradeNum(), feedbackTemplate.getDescriptionA(), feedbackTemplate.getDescriptionB());
							feedbackTemplateNodes[k].setCanAcceptDrop(false);
							k++;
		        		}							        		
		        		strToDisplay = rubric.getNumber()+" - "+rubric.getName();
			        	strTextContent = rubric.getName();								        							   	
		        		rubricNodes[j] =  new RubricsTreeNode((long) 0, strToDisplay, rubric.getLink(), null, strTextContent, 1, 0, "", "", feedbackTemplateNodes);
		        		rubricNodes[j].setCanAcceptDrop(false);
		        		j++;
		        	}
		        	strToDisplay = docType.getNumber()+" - "+docType.getName();
		        	strTextContent = docType.getName();
		        	documentTypeNodes[i] = new RubricsTreeNode((long) 0, strToDisplay, null, null, strTextContent, 0, 0, "", "", rubricNodes);
		        	documentTypeNodes[i].setCanAcceptDrop(false);
		        	i++;  
				}								

				return documentTypeNodes;
			}	
}