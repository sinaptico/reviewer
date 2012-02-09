package au.edu.usyd.reviewer.client.admin.report;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.Table.Options;

public class ActivityStatsViz extends Composite {

	private SimplePanel mainPanel = new SimplePanel();
	private ActivityStats activityStats;

	public ActivityStatsViz(ActivityStats activityStats) {
		this.activityStats = activityStats;
		initWidget(mainPanel);
	}

	private Options createOptions() {
		Options options = Options.create();
		return options;
	}

	private AbstractDataTable createTable(ActivityStats activityStats) {
		NumberFormat df = NumberFormat.getDecimalFormat();
		
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "");
		data.addColumn(ColumnType.STRING, "Mean");
		data.addColumn(ColumnType.STRING, "Median");
		data.addColumn(ColumnType.STRING, "Standard Deviation");

		int row = data.getNumberOfRows();
		data.addRow();
		data.setValue(row, 0, "Revisions");		
		data.setValue(row, 1, df.format(activityStats.getAvgRevisions()));
		data.setValue(row, 2, df.format(activityStats.getMedianRevisions()));
		data.setValue(row, 3, df.format(activityStats.getStdevRevisions()));
		row = data.getNumberOfRows();
		data.addRow();
		data.setValue(row, 0, "Sessions Writing");		
		data.setValue(row, 1, df.format(activityStats.getAvgSessionsWriting()));
		data.setValue(row, 2, df.format(activityStats.getMedianSessionsWriting()));
		data.setValue(row, 3, df.format(activityStats.getStdevSessionsWriting()));
		row = data.getNumberOfRows();
		data.addRow();
		data.setValue(row, 0, "Days Writing");		
		data.setValue(row, 1, df.format(activityStats.getAvgDaysWriting()));
		data.setValue(row, 2, df.format(activityStats.getMedianDaysWriting()));
		data.setValue(row, 3, df.format(activityStats.getStdevDaysWriting()));
		row = data.getNumberOfRows();
		data.addRow();
		data.setValue(row, 0, "Group Revisions");		
		data.setValue(row, 1, df.format(activityStats.getAvgGroupRevisions()));
		data.setValue(row, 2, df.format(activityStats.getMedianGroupRevisions()));
		data.setValue(row, 3, df.format(activityStats.getStdevGroupRevisions()));
		row = data.getNumberOfRows();
		data.addRow();
		data.setValue(row, 0, "Group Contributors");		
		data.setValue(row, 1, df.format(activityStats.getAvgGroupContributors()));
		data.setValue(row, 2, df.format(activityStats.getMedianGroupContributors()));
		data.setValue(row, 3, df.format(activityStats.getStdevGroupContributors()));
		
		return data;
	}

	@Override
	public void onLoad() {
		// Create a callback to be called when the visualization API
		// has been loaded.
		Runnable onLoadCallback = new Runnable() {
			@Override
			public void run() {
				Table table = new Table(createTable(activityStats), createOptions());
				table.setWidth("100%");
				mainPanel.setWidget(table);
			}
		};

		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback, Table.PACKAGE);

	}
}