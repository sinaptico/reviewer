package au.edu.usyd.reviewer.client.mystats;

import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.LineChart;
import com.google.gwt.visualization.client.visualizations.LineChart.Options;

public class LineChartViz extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();

	public LineChartViz() {
		initWidget(mainPanel);
	}

	private Options createOptions() {
		Options options = Options.create();
		options.setWidth(650);
		options.setHeight(300);
		options.setTitleX("Day");
		options.setTitleY("Words");
		options.setLegend(LegendPosition.RIGHT);
		return options;
	}

	private AbstractDataTable createTable() {
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Day");
		data.addColumn(ColumnType.NUMBER, "You");
		data.addColumn(ColumnType.NUMBER, "Average");
		data.addRows(10);
		for(int i=0; i<10; i++) {
			data.setValue(i, 0, String.valueOf(i+1));
			data.setValue(i, 1, 100*i+Random.nextInt(100)+10);
			data.setValue(i, 2, 100*i+Random.nextInt(100));
		}
		return data;
	}

	@Override
	public void onLoad() {
		// Create a callback to be called when the visualization API
		// has been loaded.
		Runnable onLoadCallback = new Runnable() {
			@Override
			public void run() {
				LineChart lineChart = new LineChart(createTable(), createOptions());
				mainPanel.add(lineChart);
			}
		};

		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback, LineChart.PACKAGE);
	}
}