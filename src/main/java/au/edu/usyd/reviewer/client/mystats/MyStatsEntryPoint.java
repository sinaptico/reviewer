package au.edu.usyd.reviewer.client.mystats;

import au.edu.usyd.reviewer.client.core.DocEntry;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MyStatsEntryPoint implements EntryPoint {

	private MyStatsServiceAsync mystatsService = (MyStatsServiceAsync) GWT.create(MyStatsService.class);
	private DocEntry docEntry;
	
	@Override
	public void onModuleLoad() {

		String docId = Window.Location.getParameter("docId");
		
		if(docId != null) {
			mystatsService.getDocEntry(docId, new AsyncCallback<DocEntry>(){
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Failed to load stats: " + caught.getMessage());
				}
				@Override
				public void onSuccess(DocEntry d) {
					docEntry = d;
				}});
		}
		
		DecoratorPanel instructionsPanel = new DecoratorPanel();
		instructionsPanel.setStyleName("instructionsDeco");
		instructionsPanel.setWidget(new HTML("<img src='images/icon-info.gif'/> MyStats provides you with daily feedback on your writing progress compared to that of your peers."));

		HorizontalPanel documentTitle = new HorizontalPanel();
		documentTitle.setWidth("100%");
		//documentTitle.add(new HTML(docEntry.getTitle()));
		HorizontalPanel infoPanel = new HorizontalPanel();
		infoPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		infoPanel.setWidth("100%");
		infoPanel.add(documentTitle);
		infoPanel.add(new HTML("Stats updated on 17 Nov 2010 at 12:00 AM"));
		
		Grid grid = new Grid(3,2);
		grid.setWidget(0,0, new HTML("<b>Time (Hrs)</b>"));
		grid.setWidget(0,1, new BarChartViz());
		grid.setWidget(1,0, new HTML("<b>Word Count</b>"));
		grid.setWidget(1,1, new BarChartViz());
		grid.setWidget(2,0, new HTML("<b>Spelling & Grammar</b>"));
		grid.setWidget(2,1, new BarChartViz());
		
		VerticalPanel contentPanel = new VerticalPanel();
		contentPanel.add(infoPanel);
		contentPanel.add(new HTML("<hr/>"));
		contentPanel.add(instructionsPanel);
		contentPanel.add(new HTML("<h3>Basic Stats</h3>"));
		contentPanel.add(new HTML("Statistics on the current revision of your document."));
		contentPanel.add(grid);
		contentPanel.add(new HTML("<br/><h3>Timeline</h3>"));
		contentPanel.add(new HTML("Timeline of your writing progress."));
		contentPanel.add(new LineChartViz());
		
		VerticalPanel mystatsPanel = new VerticalPanel();
		mystatsPanel.setWidth("100%");
		mystatsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mystatsPanel.add(new HTML("<h1><img src='images/mystats.png'></img>MyStats</h1>"));
		mystatsPanel.add(contentPanel);

		RootPanel.get("mystatsPanel").add(mystatsPanel);
	}
}