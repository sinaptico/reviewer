package au.edu.usyd.reviewer.client.admin.report;

import java.util.Collection;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.PieChart;
import com.google.gwt.visualization.client.visualizations.PieChart.Options;

public class PieChartViz extends Composite {
	private int threshold;
	private VerticalPanel mainPanel = new VerticalPanel();

	private Collection<UserStats> entries;

	public PieChartViz(Collection<UserStats> entries, int threshold) {
		this.entries = entries;
		this.threshold = threshold;
		initWidget(mainPanel);
	}

	private Options createOptions() {
		Options options = Options.create();
		options.setWidth(400);
		options.setHeight(240);
		options.set3D(true);
		options.setTitle("% students who have started writing");
		return options;
	}

	private AbstractDataTable createTable() {
		int lt = 0;
		int gt = 0;
		for(UserStats userStats : entries) {
			if(userStats.getRevisions() < threshold) {
				lt++;
			} else {
				gt++;
			}
		}
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "No. Students");
		data.addColumn(ColumnType.NUMBER, "No. Revisions");
		data.addRows(2);
		data.setValue(0, 0, "0-"+(threshold-1)+" revisions");
		data.setValue(0, 1, lt);
		data.setValue(1, 0, threshold+"+ revisions");
		data.setValue(1, 1, gt);
		return data;
	}

	@Override
	public void onLoad() {
		// Create a callback to be called when the visualization API
		// has been loaded.
		Runnable onLoadCallback = new Runnable() {
			@Override
			public void run() {
				PieChart chart = new PieChart(createTable(), createOptions());
				mainPanel.add(chart);
			}
		};

		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback, PieChart.PACKAGE);

	}
}