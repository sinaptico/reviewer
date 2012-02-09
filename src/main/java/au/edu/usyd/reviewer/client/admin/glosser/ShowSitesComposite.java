package au.edu.usyd.reviewer.client.admin.glosser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.libideas.validation.client.BuiltInTextBoxSubject;
import com.google.gwt.libideas.validation.client.ValidatorController;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ShowSitesComposite extends Composite {

	private class MyClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			final MyLabel ml = (MyLabel) event.getSource();
			final int row = ml.getRow();
			final int column = ml.getColumn();
			// flexTable.removeCell(row,column);
			TextBox child = new TextBox();
			child.setValue(ml.getText().trim());
			child.setWidth(ml.getWidth());
			flexTable.setWidget(row, column, child);
			child.setFocus(true);
			child.setCursorPos(0);
			// BuiltInTextBoxSubject.setErrorStyleName("inputerrorstyle");
			final BuiltInTextBoxSubject subject = new BuiltInTextBoxSubject(child);
			ValidatorController vc = new ValidatorController(subject, new SiteValidator(), new MyErrorHandler());
			// ValidatorController.addAsFocusListener(child, new
			// SiteValidator());
			child.addChangeListener(vc);

			child.addBlurHandler(new BlurHandler() {

				@Override
				public void onBlur(BlurEvent event) {
					final String text = ((TextBox) event.getSource()).getText().trim();
					if (subject.getError()) {
						return;
					}
					final SiteForm siteForm = rowToSite.get(row);
					siteForm.addFieldValue(ml.getFieldName(), text);

					siteManagerService.saveOrUpdateSite(siteForm, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Failed to update. ");
						}

						@Override
						public void onSuccess(Void SiteForm) {
							final MyLabel mL = new MyLabel(text);
							mL.setFieldName(ml.getFieldName());
							mL.setRow(ml.getRow());
							mL.setColumn(ml.getColumn());
							mL.addClickHandler(new MyClickHandler());
							mL.setWidth(ml.getWidth());
							mL.setHeight(text == null || "".equals(text) ? "20px" : "");
							// ml.setText(res);
							HorizontalPanel decp = new HorizontalPanel();
							decp.setSize(ml.getWidth(), "20px");
							decp.setStyleName("savedstyle");
							decp.add(new Label("Saved"));
							flexTable.setWidget(row, column, decp);

							Timer timer = new Timer() {
								@Override
								public void run() {
									HorizontalPanel horzPanel = new HorizontalPanel();
									horzPanel.setBorderWidth(1);
									horzPanel.add(mL);
									flexTable.setWidget(row, column, horzPanel);
									siteForm.clearFieldValue();
								}
							};
							timer.schedule(2000);
						}
					});

				}
			});

		}

	}

	private final GlosserServiceAsync siteManagerService = GWT.create(GlosserService.class);
	private VerticalPanel mainPanel = new VerticalPanel();
	private final FlexTable flexTable = new FlexTable();
	private final HashMap<Integer, SiteForm> rowToSite = new HashMap<Integer, SiteForm>();

	private List<String> tools = new LinkedList<String>();

	public ShowSitesComposite() {
		initWidget(mainPanel);
	}

	private void addRow(final SiteForm siteForm) {
		int row = flexTable.getRowCount();
		HorizontalPanel horzPanel = new HorizontalPanel();

		rowToSite.put(row, siteForm);
		flexTable.setWidget(row, 0, new CheckBox());

		horzPanel = new HorizontalPanel();
		horzPanel.setBorderWidth(1);
		MyLabel name = new MyLabel(siteForm.getName());
		name.setFieldName("name");
		name.setRow(row);
		name.setColumn(1);
		name.addClickHandler(new MyClickHandler());
		name.setWidth("150px");
		name.setHeight(siteForm.getName() == null || "".equals(siteForm.getName().trim()) ? "20px" : "");
		horzPanel.add(name);
		flexTable.setWidget(row, 1, horzPanel);

		CheckBox widget = new CheckBox("");
		widget.setValue(siteForm.isAutoHarvest());

		widget.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				siteForm.addFieldValue("autoHarvest", ((CheckBox) event.getSource()).getValue().toString());
				siteManagerService.saveOrUpdateSite(siteForm, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to update. ");
					}

					@Override
					public void onSuccess(Void SiteForm) {
						siteForm.clearFieldValue();
					}
				});
			}
		});
		flexTable.setWidget(row, 2, widget);

		horzPanel = new HorizontalPanel();
		horzPanel.setBorderWidth(1);
		String indexPeriodStr = "" + siteForm.getIndexPeriod();
		MyLabel indexPeriod = new MyLabel(indexPeriodStr);
		indexPeriod.setFieldName("indexPeriod");
		indexPeriod.setRow(row);
		indexPeriod.setColumn(3);
		indexPeriod.addClickHandler(new MyClickHandler());
		indexPeriod.setWidth("50px");
		indexPeriod.setHeight(indexPeriodStr == null || "".equals(indexPeriodStr) ? "20px" : "");
		horzPanel.add(indexPeriod);
		flexTable.setWidget(row, 3, horzPanel);

		horzPanel = new HorizontalPanel();
		horzPanel.setBorderWidth(1);
		String referingUrlStr = siteForm.getReferringUrl();
		MyLabel referringUrl = new MyLabel(referingUrlStr);
		referringUrl.setFieldName("referringUrl");
		referringUrl.setRow(row);
		referringUrl.setColumn(4);
		referringUrl.addClickHandler(new MyClickHandler());
		referringUrl.setWidth("250px");
		referringUrl.setHeight(siteForm.getReferringUrl() == null || "".equals(siteForm.getReferringUrl()) ? "20px" : "");
		horzPanel.add(referringUrl);
		flexTable.setWidget(row, 4, horzPanel);

		horzPanel = new HorizontalPanel();
		horzPanel.setBorderWidth(1);
		String titleStr = siteForm.getTitle();
		MyLabel title = new MyLabel(titleStr);
		title.setFieldName("title");
		title.setRow(row);
		title.setColumn(5);
		title.addClickHandler(new MyClickHandler());
		title.setWidth("200px");
		title.setHeight(titleStr == null || "".equals(titleStr) ? "20px" : "");
		horzPanel.add(title);
		flexTable.setWidget(row, 5, horzPanel);

		final ListBox multiBoxTools = new ListBox(true);
		multiBoxTools.setWidth("100px");
		multiBoxTools.setVisibleItemCount(3);
		int indx = 0;
		for (String tool : tools) {
			multiBoxTools.addItem(tool);
			if (siteForm.getFormTools().contains(tool)) {
				multiBoxTools.setItemSelected(indx, true);
			}
			indx++;
		}
		VerticalPanel multiBoxPanel = new VerticalPanel();
		multiBoxPanel.setSpacing(4);
		multiBoxPanel.add(multiBoxTools);
		flexTable.setWidget(row, 6, multiBoxPanel);

		multiBoxTools.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				if (Window.confirm("Do you want to save the changes?")) {
					final List<String> newTools = new LinkedList<String>();

					for (int itemC = 0; itemC < multiBoxTools.getItemCount(); itemC++) {

						if (multiBoxTools.isItemSelected(itemC)) {
							newTools.add(multiBoxTools.getItemText(itemC));
						}

					}
					final List<String> oldTools = siteForm.getFormTools();
					siteForm.setFormTools(newTools);
					siteForm.addFieldValue("tools", null);
					siteManagerService.saveOrUpdateSite(siteForm, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							siteForm.setFormTools(oldTools);
							multiBoxTools.clear();
							int indx = 0;
							for (String tool : tools) {
								multiBoxTools.addItem(tool);
								if (siteForm.getFormTools().contains(tool)) {
									multiBoxTools.setItemSelected(indx, true);
								}
								indx++;
							}
							Window.alert("Failed to update. ");
							siteForm.clearFieldValue();
						}

						@Override
						public void onSuccess(Void SiteForm) {
							siteForm.clearFieldValue();
						}
					});

				} else {
					multiBoxTools.clear();
					int indx = 0;
					for (String tool : tools) {
						multiBoxTools.addItem(tool);
						if (siteForm.getFormTools().contains(tool)) {
							multiBoxTools.setItemSelected(indx, true);
						}
						indx++;
					}
				}
			}
		});

		final DialogBox dialogBox = createMessageDialogBox(row);
		dialogBox.setAnimationEnabled(true);
		dialogBox.setModal(true);

		// Create a button to show the dialog Box
		Button openButton = new Button("Messages", new ClickHandler() {
			@Override
			public void onClick(ClickEvent sender) {
				dialogBox.center();
				dialogBox.show();
			}
		});
		flexTable.setWidget(row, 7, openButton);

		horzPanel = new HorizontalPanel();
		horzPanel.setBorderWidth(1);
		MyLabel harvesterType = new MyLabel(siteForm.getHarvesterType());
		harvesterType.setFieldName("harvesterType");
		harvesterType.setRow(row);
		harvesterType.setColumn(8);
		harvesterType.addClickHandler(new MyClickHandler());
		harvesterType.setWidth("100px");
		harvesterType.setHeight(siteForm.getHarvesterType() == null || "".equals(siteForm.getHarvesterType()) ? "20px" : "");
		horzPanel.add(harvesterType);
		flexTable.setWidget(row, 8, horzPanel);

		horzPanel = new HorizontalPanel();
		horzPanel.setBorderWidth(1);
		MyLabel harvesterUsername = new MyLabel(siteForm.getHarvesterUsername());
		harvesterUsername.setFieldName("harvesterUsername");
		harvesterUsername.setRow(row);
		harvesterUsername.setColumn(9);
		harvesterUsername.addClickHandler(new MyClickHandler());
		harvesterUsername.setWidth("200px");
		harvesterUsername.setHeight(siteForm.getHarvesterUsername() == null || "".equals(siteForm.getHarvesterUsername()) ? "20px" : "");
		horzPanel.add(harvesterUsername);
		flexTable.setWidget(row, 9, horzPanel);

		horzPanel = new HorizontalPanel();
		horzPanel.setBorderWidth(1);
		MyLabel harvesterPassword = new MyLabel(siteForm.getHarvesterPassword());
		harvesterPassword.setFieldName("harvesterPassword");
		harvesterPassword.setRow(row);
		harvesterPassword.setColumn(10);
		harvesterPassword.addClickHandler(new MyClickHandler());
		harvesterPassword.setWidth("100px");
		harvesterPassword.setHeight(siteForm.getHarvesterPassword() == null || "".equals(siteForm.getHarvesterPassword()) ? "20px" : "");
		horzPanel.add(harvesterPassword);
		flexTable.setWidget(row, 10, horzPanel);

	}

	// private void removeRow(FlexTable flexTable) {
	// int numRows = flexTable.getRowCount();
	// if (numRows > 1) {
	// flexTable.removeRow(numRows - 1);
	// flexTable.getFlexCellFormatter().setRowSpan(0, 1, numRows - 1);
	// }

	private DialogBox createMessageDialogBox(int row) {
		// Create a dialog box and set the caption text
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("messages");

		// Create a table to layout the content
		final VerticalPanel dialogContents = new VerticalPanel();
		dialogContents.setSpacing(4);
		dialogBox.setWidget(dialogContents);

		final FlexTable msgTable = new FlexTable();
		msgTable.setBorderWidth(1);

		msgTable.addStyleName("cw-FlexTable");
		msgTable.setWidth("100%");

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setWidth("200px");
		hPanel.add(new HTML("<b>Key</b>"));
		msgTable.setWidget(0, 1, hPanel);
		hPanel = new HorizontalPanel();
		hPanel.setWidth("600px");
		hPanel.add(new HTML("<b>Value</b>"));

		msgTable.setWidget(0, 2, hPanel);

		final SiteForm siteForm = rowToSite.get(row);
		Map<String, String> messages = siteForm.getFormMessages();
		int tempRow = 1;
		for (String key : messages.keySet()) {
			msgTable.setWidget(tempRow, 0, new CheckBox());
			String value = messages.get(key);
			HorizontalPanel horzPanel = new HorizontalPanel();
			horzPanel.setWidth("200px");
			horzPanel.add(new Label(key));
			msgTable.setWidget(tempRow, 1, horzPanel);
			horzPanel = new HorizontalPanel();
			horzPanel.setWidth("600px");
			horzPanel.add(new Label(value));
			msgTable.setWidget(tempRow, 2, horzPanel);
			tempRow++;

		}

		dialogContents.add(msgTable);

		// Add a close button at the bottom of the dialog
		Button closeButton = new Button("close", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});
		dialogContents.add(closeButton);

		HorizontalPanel hPanelButtons = new HorizontalPanel();
		hPanelButtons.setSpacing(3);

		final Button addButton = new Button("Add");
		hPanelButtons.add(addButton);
		final Button cancelButton = new Button("Cancel");
		hPanelButtons.add(cancelButton);
		cancelButton.setVisible(false);
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int rowCount = msgTable.getRowCount() - 1;
				msgTable.removeRow(rowCount);
				cancelButton.setVisible(false);
				addButton.setText("Add");
			}
		});

		final Button deleteButton = new Button("Delete");
		hPanelButtons.add(deleteButton);
		deleteButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (Window.confirm("Selected row(s) will be deleted, Are you sure?")) {
					final Map<String, String> remainingRows = new HashMap<String, String>(siteForm.getFormMessages());
					int rowCount = msgTable.getRowCount() - 1;
					final List<Integer> rowsBeenDeleted = new LinkedList<Integer>();
					for (int rw = 1; rw <= rowCount; rw++) {
						CheckBox cb = (CheckBox) msgTable.getWidget(rw, 0);
						if (cb.getValue()) {
							String key = ((Label) ((HorizontalPanel) msgTable.getWidget(rw, 1)).getWidget(0)).getText();
							remainingRows.remove(key);
							rowsBeenDeleted.add(rw);
						}
					}
					if (remainingRows.keySet().size() == siteForm.getFormMessages().keySet().size()) {
						Window.alert("No row has been selected");
					} else {
						final Map<String, String> oldMessages = siteForm.getFormMessages();
						siteForm.setFormMessages(remainingRows);
						siteForm.addFieldValue("messages", null);
						siteManagerService.saveOrUpdateSite(siteForm, new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								siteForm.setFormMessages(oldMessages);
								Window.alert("Failed to update. ");
								siteForm.clearFieldValue();
							}

							@Override
							public void onSuccess(Void SiteForm) {
								int deleted = 0;
								for (int row : rowsBeenDeleted) {
									msgTable.removeRow(row - deleted);
									deleted++;
								}
								siteForm.clearFieldValue();
							}
						});

					}
				}
			}
		});

		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if ("Add".equalsIgnoreCase(addButton.getText())) {
					int rowCount = msgTable.getRowCount();
					TextBox key = new TextBox();
					key.setWidth("200px");

					msgTable.setWidget(rowCount, 1, key);
					TextBox value = new TextBox();
					value.setWidth("600px");

					msgTable.setWidget(rowCount, 2, value);
					addButton.setText("Done");
					cancelButton.setVisible(true);

				} else if ("Done".equalsIgnoreCase(addButton.getText())) {
					int rowCount = msgTable.getRowCount() - 1;
					final TextBox key = (TextBox) msgTable.getWidget(rowCount, 1);
					final TextBox value = (TextBox) msgTable.getWidget(rowCount, 2);

					final Map<String, String> oldMessages = siteForm.getFormMessages();
					if (oldMessages.keySet().contains(key.getText().trim())) {
						Window.alert("Key Already Exists");
						return;
					}

					final Map<String, String> newMessages = siteForm.getFormMessages();
					newMessages.put(key.getText(), value.getText());
					siteForm.setFormMessages(newMessages);
					siteForm.addFieldValue("messages", null);

					siteManagerService.saveOrUpdateSite(siteForm, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							siteForm.setFormMessages(oldMessages);
							Window.alert("Failed to update. ");
							siteForm.clearFieldValue();
						}

						@Override
						public void onSuccess(Void SiteForm) {
							int rowCount = msgTable.getRowCount() - 1;
							msgTable.setWidget(rowCount, 0, new CheckBox());
							HorizontalPanel horzPanel = new HorizontalPanel();
							horzPanel.setWidth("200px");
							horzPanel.add(new Label(key.getText()));
							msgTable.setWidget(rowCount, 1, horzPanel);
							horzPanel = new HorizontalPanel();
							horzPanel.setWidth("600px");
							horzPanel.add(new Label(value.getText()));
							msgTable.setWidget(rowCount, 2, horzPanel);
							cancelButton.setVisible(false);
							addButton.setText("Add");
							siteForm.clearFieldValue();
						}
					});

				}
			}
		});
		dialogContents.add(hPanelButtons);
		if (LocaleInfo.getCurrentLocale().isRTL()) {
			dialogContents.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_LEFT);

		} else {
			dialogContents.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
		}

		// Return the dialog box
		return dialogBox;
	}

	@Override
	public void onLoad() {

		flexTable.addStyleName("cw-FlexTable");
		// flexTable.setWidth("32em");
		// flexTable.setCellSpacing(5);
		// flexTable.setCellPadding(3);
		flexTable.setWidth("100%");
		flexTable.setHTML(0, 0, "");
		flexTable.setHTML(0, 1, "<b>ID</b>");
		flexTable.setHTML(0, 2, "<b>Auto Harvest</b>");
		flexTable.setHTML(0, 3, "<b>Index Period (ms)</b>");
		flexTable.setHTML(0, 4, "<b>Referring URL</b>");
		flexTable.setHTML(0, 5, "<b>Title</b>");
		flexTable.setHTML(0, 6, "<b>Tools</b>");
		flexTable.setHTML(0, 7, "");
		flexTable.setHTML(0, 8, "<b>Harvester Type</b>");
		flexTable.setHTML(0, 9, "<b>Harvester Username</b>");
		flexTable.setHTML(0, 10, "<b>Harvester Password</b>");

		mainPanel.add(flexTable);

		HorizontalPanel hPanelButtons = new HorizontalPanel();
		hPanelButtons.setSpacing(3);
		final Button addButton = new Button("Add");
		hPanelButtons.add(addButton);
		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				siteManagerService.saveSite(new AsyncCallback<SiteForm>() {

					@Override
					public void onFailure(Throwable caught) {
						try {
							throw caught;
						} catch (SiteException se) {
							Window.alert(se.getMessage());
						} catch (Throwable t) {
							Window.alert("Failed to add. ");
						}

					}

					@Override
					public void onSuccess(SiteForm siteForm) {
						addRow(siteForm);

					}
				});
			}
		});

		final Button deleteButton = new Button("Delete");
		hPanelButtons.add(deleteButton);
		deleteButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (Window.confirm("Selected row(s) will be deleted, Are you sure?")) {

					int rowCount = flexTable.getRowCount() - 1;
					final List<Integer> rowsBeenSeleted = new LinkedList<Integer>();
					for (int rw = 1; rw <= rowCount; rw++) {
						CheckBox cb = (CheckBox) flexTable.getWidget(rw, 0);
						if (cb.getValue()) {
							rowsBeenSeleted.add(rw);
						}
					}
					if (rowsBeenSeleted.isEmpty()) {
						Window.alert("No row has been selected");
					} else {
						List<SiteForm> sitesToDelete = new LinkedList<SiteForm>();
						for (int row : rowsBeenSeleted) {
							sitesToDelete.add(rowToSite.get(row));
						}

						siteManagerService.deleteSites(sitesToDelete, new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failed to delete. ");
							}

							@Override
							public void onSuccess(Void SiteForm) {
								int deleted = 0;
								for (int row : rowsBeenSeleted) {
									flexTable.removeRow(row - deleted);
									deleted++;
									rowToSite.remove(row);
								}
							}
						});
					}
				}
			}

		});

		mainPanel.add(hPanelButtons);
	}

	public void setSitesAndTools(List<SiteForm> sites, List<String> tools) {
		this.tools = tools;
		for (SiteForm siteForm : sites) {
			addRow(siteForm);
		}

	}

}
