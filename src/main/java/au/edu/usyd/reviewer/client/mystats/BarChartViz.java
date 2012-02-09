package au.edu.usyd.reviewer.client.mystats;

import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.BarChart;
import com.google.gwt.visualization.client.visualizations.BarChart.Options;

public class BarChartViz extends Composite {
	private VerticalPanel mainPanel = new VerticalPanel();

	public BarChartViz() {
		initWidget(mainPanel);
	}

	private Options createOptions() {
		Options options = Options.create();
		options.setSize(450, 60);
		options.setEnableTooltip(false);
		return options;
	}

	private AbstractDataTable createTable() {
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.NUMBER, "You");
		data.addColumn(ColumnType.NUMBER, "Average");
		data.addRows(1);
		data.setValue(0, 0, Random.nextInt(100));
		data.setValue(0, 1, Random.nextInt(100));
		return data;
	}

	@Override
	public void onLoad() {
		VisualizationUtils.loadVisualizationApi(new Runnable() {
			@Override
			public void run() {
				BarChart chart = new BarChart(createTable(), createOptions());
				mainPanel.add(chart);
			}
		}, BarChart.PACKAGE);
	}
}