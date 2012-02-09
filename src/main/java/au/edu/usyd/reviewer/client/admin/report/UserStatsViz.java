package au.edu.usyd.reviewer.client.admin.report;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.Table.Options;

public class UserStatsViz extends Composite {

	private SimplePanel mainPanel = new SimplePanel();
	private Collection<UserStats> entries;
	private WritingActivity writingActivity;

	public UserStatsViz(WritingActivity writingActivity, Collection<UserStats> entries) {
		this.writingActivity = writingActivity;
		this.entries = entries;
		initWidget(mainPanel);
	}

	private Options createOptions() {
		Options options = Options.create();
		return options;
	}

	private AbstractDataTable createTable(Collection<UserStats> entries) {		
		NumberFormat df = NumberFormat.getDecimalFormat();
		
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "User");
		data.addColumn(ColumnType.NUMBER, "Revisions");
		data.addColumn(ColumnType.DATE, "First Revision");
		data.addColumn(ColumnType.DATE, "Last Revision");
		data.addColumn(ColumnType.STRING, "Duration (days)");
		data.addColumn(ColumnType.NUMBER, "Sessions Writing");
		data.addColumn(ColumnType.NUMBER, "Days Writing");
		data.addColumn(ColumnType.STRING, "Revisions per Day");
		data.addColumn(ColumnType.STRING, "Revisions per Session");
		data.addColumn(ColumnType.STRING, "Sessions per Day");
		if (writingActivity.getGroups()) {
			data.addColumn(ColumnType.NUMBER, "Group Revisions");
			data.addColumn(ColumnType.NUMBER, "Group Contributors");
		}

		// get users and groups
		Set<User> users = new HashSet<User>();
		for (DocEntry docEntry : writingActivity.getEntries()) {
			if (docEntry.getOwner() != null) {
				users.add(docEntry.getOwner());
			} else if (docEntry.getOwnerGroup() != null) {
				users.addAll(docEntry.getOwnerGroup().getUsers());
			}
		}

		for (UserStats stats : entries) {
			int row = data.getNumberOfRows();
			data.addRow();
			data.setValue(row, 0, "student"+data.getNumberOfRows());
			data.setValue(row, 1, stats.getRevisions());
			data.setValue(row, 2, stats.getFirstRevision());
			data.setValue(row, 3, stats.getLastRevision());
			data.setValue(row, 4, df.format(stats.getDuration()));
			data.setValue(row, 5, stats.getSessionsWriting());
			data.setValue(row, 6, stats.getDaysWriting());
			data.setValue(row, 7, df.format(stats.getRevisionsPerSession()));
			data.setValue(row, 8, df.format(stats.getRevisionsPerDay()));
			data.setValue(row, 9, df.format(stats.getSessionsPerDay()));
			if (writingActivity.getGroups()) {
				data.setValue(row, 10, stats.getGroupRevisions());
				data.setValue(row, 11, stats.getGroupContributors());
			}
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
				Table table = new Table(createTable(entries), createOptions());
				mainPanel.setWidget(table);
			}
		};

		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback, Table.PACKAGE);

	}
}