package au.edu.usyd.reviewer.client.assignment;

import java.util.Collection;
import java.util.Date;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.LogbookDocEntry;
import au.edu.usyd.reviewer.client.core.LogpageDocEntry;
import au.edu.usyd.reviewer.client.core.QuestionReview;
import au.edu.usyd.reviewer.client.core.Review;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.gwt.DocEntryWidget;
import au.edu.usyd.reviewer.client.core.gwt.PDFWidget;
import au.edu.usyd.reviewer.client.core.gwt.ReviewWidget;
import au.edu.usyd.reviewer.client.core.gwt.SubmitButton;
import au.edu.usyd.reviewer.client.core.util.StyleLib;
import au.edu.usyd.reviewer.client.core.util.UrlLib;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * <p>Panel that includes Writing activities with list of deadlines, links to PDF files created at each deadline and links to feedback 
 * received from peers/tutors/lecturers/Glosser.</p>
 */
@SuppressWarnings("deprecation")
public class WritingTasks extends Composite {

	/** FlexTable with the writing activities. */
	private FlexTable documentFlexTable = new FlexTable();
	
	/** The main panel. */
	private VerticalPanel mainPanel = new VerticalPanel();
	
	/** Asynchronous assignment service for model management. */
	private AssignmentServiceAsync assignmentService;	

	/**
	 * Instantiates a new writing tasks.
	 *
	 * @param assignmentService the assignment service
	 */
	public WritingTasks(AssignmentServiceAsync assignmentService) {
		this.assignmentService = assignmentService;
		initWidget(mainPanel);
	}

	/**
	 * Adds Courses, document entries, PDF and Feedback links to the main flex table.
	 *
	 * @param course the course
	 */
	private void addDocEntriesToTable(final Course course) {
		int row = documentFlexTable.getRowCount();
		documentFlexTable.setHTML(row, 0, "<b>"+course.getName()+"</b>");
		documentFlexTable.setHTML(row, 4, "");
		documentFlexTable.getRowFormatter().setStyleName(row, "documentsTableRowHeader");
		row = documentFlexTable.getRowCount();
		for (final WritingActivity writingActivity : course.getWritingActivities()) {
			for (final DocEntry docEntry : writingActivity.getEntries()) {
				// document link
				VerticalPanel documentLinks = new VerticalPanel();
				DocEntryWidget documentLink = new DocEntryWidget(docEntry, writingActivity.getName());
				final Tree documentsTree = new Tree();
				final TreeItem documentFolder = new TreeItem(documentLink);
				if (docEntry instanceof LogbookDocEntry) {
					documentsTree.addItem(documentFolder);
					documentLinks.add(documentsTree);
					for (LogpageDocEntry logpageDocEntry : ((LogbookDocEntry) docEntry).getPages()) {
						if (logpageDocEntry.getLocked()) {
							DocEntryWidget entryLink = new DocEntryWidget(logpageDocEntry.getDocumentId(), logpageDocEntry.getTitle(), docEntry.getDomainName(), docEntry.getLocked() || logpageDocEntry.getLocked());
							documentFolder.addItem(entryLink);
						} else {
							DocEntryWidget entryLink = new DocEntryWidget(logpageDocEntry.getDocumentId(), logpageDocEntry.getTitle(), docEntry.getDomainName(), docEntry.getLocked() || logpageDocEntry.getLocked());
							documentsTree.addItem(entryLink);
						}
					}
				} else {
					documentLinks.add(documentLink);
				}
				
				//File uploads
				String UPLOAD_ACTION_URL = "file";
				if (writingActivity.getStatus() < WritingActivity.STATUS_FINISH){					
					if (writingActivity.getDocumentType().equals(WritingActivity.DOCUMENT_TYPE_FILE_UPLOAD)) {
						final FormPanel form = new FormPanel();
						form.setAction(UPLOAD_ACTION_URL);
						form.setEncoding(FormPanel.ENCODING_MULTIPART);
						form.setMethod(FormPanel.METHOD_POST);
						form.addStyleName("table-center");
						form.addStyleName("demo-panel-padded");
						form.setWidth("275px");					
	
						final VerticalPanel holder = new VerticalPanel();
						
						Hidden param = new Hidden();
						param.setName("docId");					               
						param.setValue(docEntry.getDocumentId().toString());					
						holder.add(param);					
	
						final FileUpload upload = new FileUpload();
						upload.setName("upload");
						holder.add(upload);
						
						if (docEntry.isUploaded()){
						  holder.add(new HTML("A file has been already uploaded"));
						}else{
						  holder.add(new HTML("No file uploaded"));
						}
						
						holder.add(new HTML("<hr />"));
	
						holder.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
						holder.add(new Button("Submit", new ClickListener() {
							public void onClick(Widget sender) { form.submit();}
						}));
	
						form.add(holder);
	
						form.addFormHandler(new FormHandler() {
							public void onSubmit(FormSubmitEvent event) {
	      						if (upload.getFilename().length() == 0){      							
									Window.alert("Please select a file first");
									event.setCancelled(true);
								}else if (!upload.getFilename().contains(".pdf")){      							
									Window.alert("You can upload only PDF files");
									event.setCancelled(true);
								}else if (docEntry.isUploaded()){
									if (!Window.confirm("A file has been already uploaded for this activity, Do you want to overwrite it?")){
								 		event.setCancelled(true);
									}															
								}
							}
							
							public void onSubmitComplete(FormSubmitCompleteEvent event) {
								Window.alert("File successfully uploaded");
								Window.Location.reload();							
							}
						});
						documentLinks.add(form);
					}
				}
				//////////////////////////////////////////////////////////
				
				/////////////////////////////////////////////////////
				//Early Submit
				if (writingActivity.getEarlySubmit() && !docEntry.getLocked()){
					final SubmitButton earlySubmitButton = new SubmitButton("<img src='images/icon-upload.gif'></img><span> Submit </span>", "<img src='images/google/apps_upload_icon.gif'></img> <span> Submit </span>", "Submitted");
					earlySubmitButton.setTitle("Submit Document");
					earlySubmitButton.addClickHandler(new ClickHandler() {
						private DocEntry localDocEntry = docEntry;
						
						@Override
						public void onClick(ClickEvent event) {
							if (Window.confirm("Are you sure you want to submit the document?")) {
								earlySubmitButton.updateStateSubmitting();
								assignmentService.submitDocEntry(localDocEntry, new AsyncCallback<DocEntry>() {
									@Override
									public void onFailure(Throwable caught) {
										Window.alert("Failed to submit document: " + caught.getMessage());
										earlySubmitButton.updateStateSubmit();
									}

									@Override
									public void onSuccess(DocEntry submittedDocEntry) {
										Window.alert("'" + localDocEntry.getTitle() + "' has been successfully submitted.");
										localDocEntry = submittedDocEntry;
										earlySubmitButton.updateStateSubmit();
										Window.Location.reload();
									}
								});
							}
						}
					});	
					documentLinks.add(earlySubmitButton);
				 }				
				/////////////////////

				// due date
				VerticalPanel dueDate = new VerticalPanel();
				for (Deadline deadline : writingActivity.getDeadlines()) {
					boolean logBook = false;
					if (docEntry instanceof LogbookDocEntry) {
						logBook = true;
					}
					Date dateTime=new Date();
					dueDate.add(new HTML("<div style='" + StyleLib.dueDateStyle(dateTime,deadline.getFinishDate(),logBook) + "'>"+deadline.getName()+": " + StyleLib.dueDateFormat(deadline.getFinishDate()) + "</div>"));
				}

				// pdf download links
				VerticalPanel downloadLinks = new VerticalPanel();
				if (docEntry instanceof LogbookDocEntry) {
					final TreeItem downloadFolder = new TreeItem(new PDFWidget(writingActivity.getName(), writingActivity.getName() + ".pdf", docEntry.getDocumentId(), writingActivity.getDeadlines().get(writingActivity.getDeadlines().size()-1).getId()));
					final Tree downloadTree = new Tree();
					downloadTree.addItem(downloadFolder);
					downloadLinks.add(downloadTree);
					
					// simultaneous opening and closing of logbook trees
					documentsTree.addOpenHandler(new OpenHandler<TreeItem>(){
						@Override
						public void onOpen(OpenEvent<TreeItem> event) {
							downloadFolder.setState(true, false);
						}});
					documentsTree.addCloseHandler(new CloseHandler<TreeItem>(){
						@Override
						public void onClose(CloseEvent<TreeItem> event) {
							downloadFolder.setState(false, false);
						}});
					downloadTree.addOpenHandler(new OpenHandler<TreeItem>(){
						@Override
						public void onOpen(OpenEvent<TreeItem> event) {
							documentFolder.setState(true, false);
						}});
					downloadTree.addCloseHandler(new CloseHandler<TreeItem>(){
						@Override
						public void onClose(CloseEvent<TreeItem> event) {
							documentFolder.setState(false, false);
						}});
					
					// add entries to logbook trees
					for (LogpageDocEntry logpageDocEntry : ((LogbookDocEntry) docEntry).getPages()) {
						if (logpageDocEntry.getLocked() && logpageDocEntry.getSubmitted() != null) {
							HTML entryHtml = new HTML("<div style='height:19px'>" + StyleLib.dateFormat(logpageDocEntry.getSubmitted()) + "</div>");
							entryHtml.setTitle(logpageDocEntry.getTitle());
							downloadFolder.addItem(entryHtml);
						} else if (!docEntry.getLocked()) {
							final SubmitButton submitButton = new SubmitButton("<img src='images/icon-upload.gif'></img><span>" + logpageDocEntry.getTitle() + "</span>", "<img src='images/google/apps_upload_icon.gif'></img> <span>" + logpageDocEntry.getTitle() + "</span>", "Submitted");
							submitButton.setTitle("Submit Entry");
							submitButton.addClickHandler(new ClickHandler() {
								private LogbookDocEntry logbookDocEntry = (LogbookDocEntry) docEntry;

								@Override
								public void onClick(ClickEvent event) {
									if (Window.confirm("Are you sure you want to submit '" + logbookDocEntry.getPages().get(logbookDocEntry.getPages().size() - 1).getTitle() + "'? Once submitted, you will no longer be able to modify this entry.")) {
										submitButton.updateStateSubmitting();
										assignmentService.submitDocEntry(logbookDocEntry, new AsyncCallback<LogbookDocEntry>() {
											@Override
											public void onFailure(Throwable caught) {
												Window.alert("Failed to submit logbook entry: " + caught.getMessage());
												submitButton.updateStateSubmit();
											}

											@Override
											public void onSuccess(LogbookDocEntry submittedDocEntry) {
												Window.alert("'" + logbookDocEntry.getPages().get(logbookDocEntry.getPages().size() - 1).getTitle() + "' has been successfully submitted.");
												logbookDocEntry = submittedDocEntry;
												LogpageDocEntry newEntry = logbookDocEntry.getPages().get(logbookDocEntry.getPages().size() - 1);
												LogpageDocEntry submittedEntry = logbookDocEntry.getPages().get(logbookDocEntry.getPages().size() - 2);

												documentFolder.addItem(new DocEntryWidget(submittedEntry.getDocumentId(), submittedEntry.getTitle(),course.getDomainName(), true));
												documentsTree.removeItem(documentsTree.getItem(documentsTree.getItemCount() - 1));
												documentsTree.addItem(new DocEntryWidget(newEntry.getDocumentId(), newEntry.getTitle(),course.getDomainName(), false));
												HTML entryHtml = new HTML("<div style='height:19px'>" + StyleLib.dateFormat(submittedEntry.getSubmitted()) + "</div>");
												entryHtml.setTitle(submittedEntry.getTitle());
												downloadFolder.addItem(entryHtml);

												submitButton.setSubmitHTML("<img src='images/icon-upload.gif'></img><span>" + newEntry.getTitle() + "</span>");
												submitButton.setSubmittingHTML("<img src='images/google/apps_upload_icon.gif'></img> <span>" + newEntry.getTitle() + "</span>");
												submitButton.updateStateSubmit();
											}
										});
									}
								}
							});
							downloadTree.addItem(submitButton);
						}
					}
				} else {
					Date latestLockedDeadline = null;
					for (int i=0; i<writingActivity.getDeadlines().size(); i++) {
						Deadline deadline = writingActivity.getDeadlines().get(i);						 
						if ((deadline.getStatus() >= Deadline.STATUS_DEADLINE_FINISH)) {
							latestLockedDeadline = deadline.getFinishDate();
							Anchor downloadLink = new Anchor();
							downloadLink.setHref(UrlLib.pdfDownloadUrl(writingActivity.getName() + " - "+deadline.getName()+".pdf", docEntry.getDocumentId(), deadline.getId()));
							if(i < writingActivity.getDeadlines().size() - 1) {
								//downloadLink.setHTML("<div style='padding-top: 1px;'><img height='10px' src='images/icon-pdf.gif'></img><span>"+deadline.getName()+"-SNAPSHOT</span></div>");								
								downloadLink.setHTML("<div style='padding-top: 4.5px;'><img src='images/icon-pdf.gif'></img><span>"+deadline.getName()+"-SNAPSHOT</span></div>");
							} else {
								downloadLink.setHTML("<div style='padding-top: 4.5px;'><img src='images/icon-pdf.gif'></img><span>"+deadline.getName()+"</span></div>");
							}
							downloadLink.setTitle("Download");
							downloadLinks.add(downloadLink);
						}
					}					
					if (((writingActivity.getEarlySubmit()) && (docEntry.getEarlySubmitDate()!=null)&& (latestLockedDeadline == null || (latestLockedDeadline.before(docEntry.getEarlySubmitDate())) ) )){
						Deadline deadline = writingActivity.getDeadlines().get(writingActivity.getDeadlines().size()-1);
						Anchor downloadLink = new Anchor();
						downloadLink.setHref(UrlLib.pdfDownloadUrl(writingActivity.getName() + " - "+deadline.getName()+".pdf", docEntry.getDocumentId(), deadline.getId()));
						downloadLink.setHTML("<img src='images/icon-pdf.gif'></img><span>Early Submission</span>");
						downloadLink.setTitle("Download");
						downloadLinks.add(downloadLink);								
					}
				}

				// feedback
				HorizontalPanel feedbackPanel = new HorizontalPanel();
				// stats link
//				if (writingActivity.getShowStats()) {
//					Anchor statsLink = new Anchor();
//					statsLink.setHref("MyStats.html?docId="+docEntry.getDocumentId());
//					statsLink.setHTML("<img height='19px' src='images/mystats.png'></img>");
//					statsLink.setTitle("My Stats");
//					statsLink.setTarget("_blank");
//					feedbackPanel.add(statsLink);
//				}
				// glosser link
				if (!writingActivity.getGlosserSite().equals(WritingActivity.GLOSSER_SITE_NONE)) {
					Anchor glosserLink = new Anchor();
					//glosserLink.setHref(UrlLib.glosserUrl(writingActivity.getGlosserSite(), docEntry.getDocumentId()));
					UrlLib.glosserUrl(glosserLink,writingActivity.getGlosserSite(), docEntry.getDocumentId());
					glosserLink.setHTML("<img height='19px' src='images/glosser.png'></img>");
					glosserLink.setTitle("Automatic Feedback");
					glosserLink.setTarget("_blank");
					feedbackPanel.add(glosserLink);
				}

				for (Review review : docEntry.getReviews()) {
					if (review instanceof QuestionReview) {
						if (review.getId() == -1) {
							Anchor assessmentLink = new Anchor();
							assessmentLink.setHref("Review.html?docId=" + docEntry.getDocumentId());
							assessmentLink.setHTML("<img src='images/Feedback.PNG'></img>");
							assessmentLink.setTarget("_blank");
							assessmentLink.setTitle("Feedback Assessment");
							feedbackPanel.add(assessmentLink);
							continue;
						}
					}

					// add review link
					feedbackPanel.add(new ReviewWidget(review, "", false));
				}
				documentFlexTable.getRowFormatter().setVerticalAlign(row, HasVerticalAlignment.ALIGN_TOP);
				documentFlexTable.getRowFormatter().setStyleName(row, "documentsTableRow");
				documentFlexTable.setWidget(row, 1, documentLinks);
				documentFlexTable.setWidget(row, 2, dueDate);
				documentFlexTable.setWidget(row, 3, downloadLinks);
				documentFlexTable.setWidget(row, 4, feedbackPanel);
				
				row = documentFlexTable.getRowCount();
			}
		}
	}

	/** 
	 * <p>Main method of the panel that loads the Activity flex table where the data is then updated.</p>
	 */
	@Override
	public void onLoad() {
		documentFlexTable.setWidth("100%");
		documentFlexTable.setStyleName("documentsTable");
		mainPanel.add(documentFlexTable);
	}

	/**
	 * Sets the entries table headlines and populates them by calling the addDocEntriesToTable method.
	 *
	 * @param courses the new table entries
	 */
	public void setTableEntries(Collection<Course> courses) {
		if (courses.size() > 0){
			documentFlexTable.clear();
			documentFlexTable.removeAllRows();
			documentFlexTable.setWidth("100%");
			documentFlexTable.setHTML(0, 0, "<b>Course</b>");
			documentFlexTable.setHTML(0, 1, "<b>Activity file</b>");
			documentFlexTable.setHTML(0, 2, "<b>Due date</b>");
			documentFlexTable.setHTML(0, 3, "<b>Submitted</b>");
			documentFlexTable.setHTML(0, 4, "<b>Feedback</b>");
			documentFlexTable.getRowFormatter().setStyleName(0, "documentsTableHeader");
	
			for (Course course : courses) {
				if (course.getWritingActivities().size() > 0) {
					addDocEntriesToTable(course);
				}
			}
		}else{
			documentFlexTable.clear();
			documentFlexTable.removeAllRows();
			documentFlexTable.setWidth("100%");
			documentFlexTable.setHTML(0, 0, "<b>There are no Writing Tasks for the selected semester-year</b>");		
		}		
	}
	
	/**
	 * Sets the loading message while the activities are loaded into the main flex table.
	 */
	public void setLoadingMessage() {
			documentFlexTable.clear();
			documentFlexTable.removeAllRows();
			documentFlexTable.setWidth("100%");
			documentFlexTable.setHTML(0, 0, "<b>Loading...</b>");
				
	}	
}
