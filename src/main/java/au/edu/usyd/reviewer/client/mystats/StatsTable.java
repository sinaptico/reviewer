package au.edu.usyd.reviewer.client.mystats;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.Table.Options;

public class StatsTable extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();
	private DataTable data;

	public StatsTable() {
		initWidget(mainPanel);
	}

	private Options createOptions() {
		Options options = Options.create();
		options.setSort(Options.Policy.DISABLE);
		return options;
	}

	private AbstractDataTable createTable() {
		data = DataTable.create();
		data.addColumn(ColumnType.STRING, "");
		data.addColumn(ColumnType.NUMBER, "You");
		data.addColumn(ColumnType.NUMBER, "Average");
		
		data.addRows(1);
		data.setValue(data.getNumberOfRows() - 1, 0, "Word Count");
		data.setValue(data.getNumberOfRows() - 1, 1, 975);
		data.setValue(data.getNumberOfRows() - 1, 2, 950);
		
		data.addRows(1);
		data.setValue(data.getNumberOfRows() - 1, 0, "Readability");
		data.setValue(data.getNumberOfRows() - 1, 1, 73);
		data.setValue(data.getNumberOfRows() - 1, 2, 60);
		
		return data;
	}

	@Override
	public void onLoad() {
		// Create a callback to be called when the visualization API
		// has been loaded.
		Runnable onLoadCallback = new Runnable() {
			@Override
			public void run() {
				Table table = new Table(createTable(), createOptions());
				mainPanel.add(table);
			}
		};

		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback, Table.PACKAGE);

	}
}