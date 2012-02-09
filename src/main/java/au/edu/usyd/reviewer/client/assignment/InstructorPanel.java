package au.edu.usyd.reviewer.client.assignment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.edu.usyd.reviewer.client.core.Activity;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.ReviewEntry;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.UserGroup;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.gwt.DocEntryWidget;
import au.edu.usyd.reviewer.client.core.gwt.PDFWidget;
import au.edu.usyd.reviewer.client.core.gwt.ReviewWidget;
import au.edu.usyd.reviewer.client.core.gwt.SubmitButton;
import au.edu.usyd.reviewer.client.core.gwt.ZipWidget;
import au.edu.usyd.reviewer.client.core.util.EntryTitleComparator;
import au.edu.usyd.reviewer.client.core.util.StyleLib;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InstructorPanel extends Composite {

	private FlexTable activityFlexTable = new FlexTable();
	private VerticalPanel mainPanel = new VerticalPanel();
	private AssignmentServiceAsync assignmentService;

	public InstructorPanel(AssignmentServiceAsync assignmentService) {
		this.assignmentService = assignmentService;
		initWidget(mainPanel);
	}

	private void addDocEntiresToTable(final Course course) {

		// course folder tree
		Anchor editCourseLink = new Anchor();
		editCourseLink.setHTML("<img src='images/icon-edit.gif'></img>");
		editCourseLink.setHref("Admin.html");
		//editCourseLink.setTarget("_blank");
		editCourseLink.setTitle("Edit Course");
		HorizontalPanel coursePanel = new HorizontalPanel();
		coursePanel.add(editCourseLink);
		coursePanel.add(new DocEntryWidget(course.getFolderId(), course.getName(),course.getDomainName(), false));
		TreeItem courseItem = new TreeItem(coursePanel);
		courseItem.addItem(new DocEntryWidget(course.getSpreadsheetId(), "Students", course.getDomainName(), false));
		courseItem.addItem(new DocEntryWidget(course.getTemplatesFolderId(), "Templates", course.getDomainName(), false));
		Tree courseTree = new Tree();
		courseTree.addItem(courseItem);

		int row = activityFlexTable.getRowCount();
		activityFlexTable.setWidget(row, 0, courseTree);
		activityFlexTable.setHTML(row, 3, "");
		activityFlexTable.getRowFormatter().setStyleName(row, "documentsTableRow");
		row = activityFlexTable.getRowCount();

		for (final WritingActivity writingActivity : course.getWritingActivities()) {
			// activity folder tree
			String activityName = writingActivity.getName() + (!writingActivity.getTutorial().equals(WritingActivity.TUTORIAL_ALL) ? " (" + writingActivity.getTutorial() + ")" : "");
			DocEntryWidget activityLink = new DocEntryWidget(writingActivity.getFolderId(), activityName, course.getDomainName(), false);
			TreeItem activityItem = new TreeItem(activityLink);
			List<DocEntry> sortedDocEntries = new ArrayList<DocEntry>(writingActivity.getEntries());
			Collections.sort(sortedDocEntries, new EntryTitleComparator());
			for (final DocEntry docEntry : sortedDocEntries) {
				final SimplePanel documentLink = new SimplePanel();
				documentLink.setWidget(new DocEntryWidget(docEntry, (docEntry.getOwner() != null ? docEntry.getOwner().getLastname() + ", " + docEntry.getOwner().getFirstname() : "Group " + docEntry.getOwnerGroup().getName())));
				Image editDocumentImage = new Image("images/icon-edit.gif");
				editDocumentImage.setTitle("Edit Permissions");
				editDocumentImage.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						final DialogBox dialogBox = new DialogBox();
						final DocEntryForm docEntryForm = new DocEntryForm();
						docEntryForm.setDocEntry(docEntry);
						final SubmitButton updateButton = new SubmitButton("Update", "Updating...", "Updated");
						updateButton.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								if (Window.confirm("Are you sure you want to update '" + docEntry.getTitle() + "'?")) {
									updateButton.updateStateSubmitting();
									assignmentService.updateDocEntry(docEntryForm.getDocEntry(), new AsyncCallback<DocEntry>() {
										@Override
										public void onFailure(Throwable caught) {
											Window.alert("Failed to update document: " + caught.getMessage());
											updateButton.updateStateSubmit();
										}

										@Override
										public void onSuccess(DocEntry docEntry) {
											documentLink.setWidget(new DocEntryWidget(docEntry, (docEntry.getOwner() != null ? docEntry.getOwner().getLastname() + ", " + docEntry.getOwner().getFirstname() : "Group " + docEntry.getOwnerGroup().getName())));
											updateButton.updateStateSubmit();
											dialogBox.hide();
										}
									});
								}
							}
						});

						HorizontalPanel buttonsPanel = new HorizontalPanel();
						buttonsPanel.setWidth("100%");
						buttonsPanel.add(updateButton);
						buttonsPanel.add(new Button("Close", new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								dialogBox.hide();
							}
						}));

						VerticalPanel panel = new VerticalPanel();
						panel.add(docEntryForm);
						panel.add(buttonsPanel);
						dialogBox.setHTML("<b>Document</b>");
						dialogBox.setWidget(panel);
						dialogBox.center();
						dialogBox.show();
					}
				});
				HorizontalPanel documentLinks = new HorizontalPanel();
				documentLinks.add(editDocumentImage);
				documentLinks.add(documentLink);
				activityItem.addItem(documentLinks);
			}
			Tree editLinks = new Tree();
			editLinks.addItem(activityItem);

			// due date
			VerticalPanel downloadLinks = new VerticalPanel();
			Set<String> tutorials = new HashSet<String>();
			if (writingActivity.getTutorial().equals(WritingActivity.TUTORIAL_ALL)) {
				tutorials.addAll(course.getTutorials());
			} else {
				tutorials.add(writingActivity.getTutorial());
			}
			for (String tutorial : tutorials) {
				for(Deadline deadline : writingActivity.getDeadlines()) {
					if (deadline.getStatus() >= Deadline.STATUS_DEADLINE_FINISH) {
						TreeItem downloadFolder = new TreeItem(new ZipWidget(deadline.getName() +": download all (" + tutorial + ")", writingActivity.getName() + " (" + tutorial + ") - "  + deadline.getName(), deadline.getId(), tutorial));
						for (DocEntry docEntry : sortedDocEntries) {
							if (docEntry.getOwner() != null) {
								for (UserGroup userGroup : course.getStudentGroups()) {
									if (userGroup.getUsers().contains(docEntry.getOwner()) && userGroup.getTutorial().equals(tutorial)) {
										downloadFolder.addItem(new PDFWidget(docEntry.getOwner().getLastname() + ", " + docEntry.getOwner().getFirstname(), docEntry.getTitle() + " - " + deadline.getName(), docEntry.getDocumentId(), deadline.getId()));
										break;
									}
								}
							} else if (docEntry.getOwnerGroup() != null && docEntry.getOwnerGroup().getTutorial().equals(tutorial)) {
								downloadFolder.addItem(new PDFWidget("Group " + docEntry.getOwnerGroup().getName(), docEntry.getTitle() + " - " + deadline.getName(), docEntry.getDocumentId(), deadline.getId()));
							}
						}
						Tree downloadTree = new Tree();
						downloadTree.addItem(downloadFolder);
						downloadLinks.add(downloadTree);
						
						for (ReviewingActivity reviewingActivity : writingActivity.getReviewingActivities()) {
							if(reviewingActivity.getStatus() >= Activity.STATUS_FINISH && deadline.equals(reviewingActivity.getStartDate())) {
								TreeItem reviewDownloadFolder = new TreeItem(new ZipWidget(reviewingActivity.getName() +": download all (" + tutorial + ")", writingActivity.getName() + " (" + tutorial + ") - "  + reviewingActivity.getName(), reviewingActivity.getStartDate().getId(), tutorial, reviewingActivity.getId().toString()));
								List<ReviewEntry> sortedReviewEntries = new ArrayList<ReviewEntry>(reviewingActivity.getEntries());
								Collections.sort(sortedReviewEntries, new EntryTitleComparator());
								for(ReviewEntry reviewEntry : sortedReviewEntries) {
									reviewDownloadFolder.addItem(new ReviewWidget(reviewEntry.getReview(), reviewEntry.getOwner().getLastname() + ", " + reviewEntry.getOwner().getFirstname(), false));
								}
								Tree reviewDownloadTree = new Tree();
								downloadTree.addItem(reviewDownloadFolder);
								downloadLinks.add(reviewDownloadTree);
							}
						}
					}
				}
			}

			// due date
			VerticalPanel dueDate = new VerticalPanel();
			for (Deadline deadline : writingActivity.getDeadlines()) {
				dueDate.add(new HTML("<div style='" + StyleLib.dueDateStyle(deadline.getStatus(), Deadline.STATUS_DEADLINE_FINISH) + "'>"+deadline.getName()+": " + StyleLib.dueDateFormat(deadline.getFinishDate()) + "</div>"));
				for (ReviewingActivity reviewingActivity : writingActivity.getReviewingActivities()) {
					if(deadline.equals(reviewingActivity.getStartDate())) {
						dueDate.add(new HTML("<div style='" + StyleLib.dueDateStyle(reviewingActivity.getStatus(), Activity.STATUS_FINISH) + "'>"+reviewingActivity.getName()+": " + StyleLib.dueDateFormat(reviewingActivity.getFinishDate()) + "</div>"));
					}
				}
			}

			activityFlexTable.getRowFormatter().setVerticalAlign(row, HasVerticalAlignment.ALIGN_TOP);
			activityFlexTable.getRowFormatter().setStyleName(row, "documentsTableRow");
			activityFlexTable.setWidget(row, 1, editLinks);
			activityFlexTable.setWidget(row, 2, dueDate);
			activityFlexTable.setWidget(row, 3, downloadLinks);
			row = activityFlexTable.getRowCount();
		}
	}

	@Override
	public void onLoad() {
		activityFlexTable.setWidth("100%");
		activityFlexTable.setStyleName("documentsTable");
		mainPanel.add(activityFlexTable);
	}

	public void setTableEntries(Collection<Course> courses) {
		if (courses.size() > 0){
			activityFlexTable.clear();
			activityFlexTable.removeAllRows();
			activityFlexTable.setHTML(0, 0, "<b>Course</b>");
			activityFlexTable.setHTML(0, 1, "<b>Activity file</b>");
			activityFlexTable.setHTML(0, 2, "<b>Due date</b>");
			activityFlexTable.setHTML(0, 3, "<b>Submitted</b>");
			activityFlexTable.getRowFormatter().setStyleName(0, "documentsTableHeader");

			for (Course course : courses) {
				addDocEntiresToTable(course);
			}
		}else{
			activityFlexTable.clear();
			activityFlexTable.removeAllRows();
			activityFlexTable.setWidth("100%");
			activityFlexTable.setHTML(0, 0, "<b>There are no Courses for the selected semester-year</b>");		
		}			
		

	}
}
