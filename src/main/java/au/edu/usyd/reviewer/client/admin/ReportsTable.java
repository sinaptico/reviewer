package au.edu.usyd.reviewer.client.admin;

import java.util.Collection;

import au.edu.usyd.reviewer.client.admin.report.ActivityStats;
import au.edu.usyd.reviewer.client.admin.report.ActivityStatsViz;
import au.edu.usyd.reviewer.client.admin.report.PieChartViz;
import au.edu.usyd.reviewer.client.admin.report.UserStats;
import au.edu.usyd.reviewer.client.admin.report.UserStatsViz;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.gwt.SubmitButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ReportsTable extends Composite {

	private final AdminServiceAsync adminService;
	private SimplePanel mainPanel = new SimplePanel();

	public ReportsTable(AdminServiceAsync adminService) {
		this.adminService = adminService;
		initWidget(mainPanel);
	}

	@Override
	public void onLoad() {

	}

	public void setWritingActivity(WritingActivity writingActivity) {
		this.updateReportsTable(writingActivity);
	}

	private void updateReportsTable(final WritingActivity writingActivity) {
		final VerticalPanel reportsContent = new VerticalPanel();
		final SubmitButton generateReport = new SubmitButton("Generate Report", "Generating...", "Generated");
		generateReport.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				generateReport.updateStateSubmitting();
				adminService.getWritingActivityStats(writingActivity.getId(), new AsyncCallback<Collection<UserStats>>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to update report: " + caught.getMessage());
						generateReport.updateStateSubmit();
					}

					@Override
					public void onSuccess(Collection<UserStats> entries) {
						Button emailButton = new Button("Compose");
						emailButton.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								final DialogBox dialogBox = new DialogBox();
								HorizontalPanel buttonsPanel = new HorizontalPanel();
								buttonsPanel.setWidth("100%");
								buttonsPanel.add(new Button("Send"));
								buttonsPanel.add(new Button("Close", new ClickHandler() {
									@Override
									public void onClick(ClickEvent event) {
										dialogBox.hide();
									}
								}));

								TextArea emailMessage = new TextArea();
								emailMessage.setPixelSize(300, 200);
								
								ListBox template = new ListBox();
								template.addItem("Humour");
								template.addItem("Serious");

								Grid grid = new Grid(4, 2);
								grid.setWidget(0, 0, new HTML("<b>To:</b>"));
								grid.setWidget(0, 1, new HTML("6 students [names hidden]"));
								grid.setWidget(1, 0, new HTML("<b>Subject:</b>"));
								grid.setWidget(1, 1, new HTML("iWrite - Progress Update"));
								grid.setWidget(2, 0, new HTML("<b>Template:</b>"));
								grid.setWidget(2, 1, template);
								grid.setWidget(3, 1, emailMessage);
								VerticalPanel panel = new VerticalPanel();
								panel.add(grid);
								panel.add(new HTML("<hr/>"));
								panel.add(buttonsPanel);
								dialogBox.setTitle("Email: 0-9 Revisions");
								dialogBox.setWidget(panel);
								dialogBox.center();
								dialogBox.show();
							}
						});
						
						ListBox criteriaList = new ListBox();
						criteriaList.addItem("0-9 revisions");
						Grid emailPanel = new Grid(2,2);
						emailPanel.setWidget(0,0,new HTML("Criteria:"));
						emailPanel.setWidget(0,1,criteriaList);
						emailPanel.setWidget(1,1,emailButton);
						
						reportsContent.clear();
						reportsContent.add(new HTML("<h3>Contact students</h3>"));
						reportsContent.add(emailPanel);
						reportsContent.add(new HTML("<br/><hr/>"));
						reportsContent.add(new PieChartViz(entries, 10));
						reportsContent.add(new HTML("<br/><hr/><h3>Individual User Stats</h3>"));
						reportsContent.add(new UserStatsViz(writingActivity, entries));
						reportsContent.add(new HTML("<br/><hr/><h3>Overall User Stats</h3>"));
						reportsContent.add(new ActivityStatsViz(new ActivityStats(entries)));
						generateReport.updateStateSubmit();
					}
				});
			}
		});

		// reports panel
		VerticalPanel reportsPanel = new VerticalPanel();
		reportsPanel.add(generateReport);
		reportsPanel.add(new HTML("<hr/>"));
		reportsPanel.add(reportsContent);
		mainPanel.setWidget(reportsPanel);
	}
}
