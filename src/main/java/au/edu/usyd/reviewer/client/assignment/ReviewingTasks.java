package au.edu.usyd.reviewer.client.assignment;

import java.util.ArrayList;


import java.util.Date;
import java.util.List;

import au.edu.usyd.reviewer.client.core.util.StyleLib;
import au.edu.usyd.reviewer.server.Reviewer;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;

/**
 * Panel that includes Reviewing activities with deadlines.
 */
public class ReviewingTasks extends Composite {

	/** Table with the reviewing activities. */
	private CellTable<ReviewTask> reviewsTable = new CellTable<ReviewTask>();
	
	/** The main panel. */
	private VerticalPanel mainPanel = new VerticalPanel();

	private AssignmentServiceAsync assignmentService = null;
	
	private int PAGE_SIZE = 20;
	/**
	 * Instantiates a new reviewing tasks.
	 */
	public ReviewingTasks(AssignmentServiceAsync assignmentService) {
		initWidget(mainPanel);
		this.assignmentService = assignmentService;
	}


	/** 
	 * <p>Main method of the panel that loads the Review flex table where the data is then updated.</p>
	 */
	@Override
	public void onLoad() {
	}

	/**
	 * Sets the entries table headlines and populates them by calling the getReviewTasks method.
	 *
	 * @param courses the new table entries
	 */
	public void setTableEntries(final int semester,final int year, final Boolean includeFinishedReviews, final Long organizationId) {
		
		// Prepare table
		prepareTable();
		
		// Pagination
	    SimplePager.Resources resources = GWT.create(SimplePager.Resources.class);
	    SimplePager pager = new SimplePager(TextLocation.CENTER, resources, false, 0,
                true) {
            private int pageSize = PAGE_SIZE;

            @Override
            public int getPageSize() {
                return pageSize;
            }

            @Override
            public void previousPage() {
                if (getDisplay() != null) {
                    Range range = getDisplay().getVisibleRange();
                    setPageStart(range.getStart() - getPageSize());
                }
            }

            @Override
            public void setPageStart(int index) {
                if (getDisplay() != null) {
                    Range range = getDisplay().getVisibleRange();
                    int displayPageSize = getPageSize();
                    if (isRangeLimited() && getDisplay().isRowCountExact()) {
                        displayPageSize = Math.min(getPageSize(), getDisplay()
                                .getRowCount() - index);
                    }
                    index = Math.max(0, index);
                    if (index != range.getStart()) {
                        getDisplay().setVisibleRange(index, displayPageSize);
                    }
                }
            }

            @Override
            public void nextPage() {
                if (getDisplay() != null) {
                    Range range = getDisplay().getVisibleRange();
                    setPageStart(range.getStart() + getPageSize());
                }
            }
        };

		// Get reviewing tasks
		final AsyncDataProvider<ReviewTask> provider = new AsyncDataProvider<ReviewTask>() {
	    	@Override
	    	protected void onRangeChanged(HasData<ReviewTask> display) {
	    		final Range range = display.getVisibleRange();
	    		final int start = range.getStart();
	    		final int length =range.getLength();
	    		
	    	    AsyncCallback<List<ReviewTask>> callback = new AsyncCallback<List<ReviewTask>>() {
	                @Override
	                public void onFailure(Throwable caught) {
	                	List<ReviewTask> reviewTasks = new ArrayList<ReviewTask>();
						updateRowData(start,reviewTasks);
	                }

	                @Override
	                public void onSuccess(List<ReviewTask> reviewTasks) {
						updateRowData(start, reviewTasks);
	                }
	            };
//	            int page = (start / PAGE_SIZE) + 1;
	            assignmentService.getUserReviewingTasks(semester, year, includeFinishedReviews, start, length, callback);
	    	}
	    };
	    provider.addDataDisplay(reviewsTable);
	     
	    // Get reviewing tasks total account
	    assignmentService.getUserReviewingTasksTotalAccount(semester, year, includeFinishedReviews, organizationId, new AsyncCallback<Integer>() {
			@Override
			public void onFailure(Throwable caught) {
				provider.updateRowCount(0,true);
			}

			@Override
			public void onSuccess(Integer total) {
				provider.updateRowCount(total,true);
			}
		});	
		
		
	    pager.setRangeLimited(true);
        pager.setDisplay(reviewsTable);
        pager.setPageSize(PAGE_SIZE);
        
        VerticalPanel vp = new VerticalPanel();
	    vp.add(reviewsTable);
	    vp.add(pager);
		mainPanel.add(vp);
	}

	/**
	 * Sets the loading message while the activities are loaded into the main flex table.
	 */
	public void setLoadingMessage() {
		mainPanel.add(new HTML("<b>Loading...</b>"));
	}
	
	private void prepareTable() {
		reviewsTable = new CellTable<ReviewTask>();
		mainPanel.clear();
		
		// Define the columns of the table
		// Course name column
		TextColumn<ReviewTask> courseNameColumn = new TextColumn<ReviewTask>() {
			@Override
		    public String getValue(ReviewTask reviewTask) {
				String name = "";
		    	if (reviewTask != null && reviewTask.getCourseName() != null ){
		    		name = reviewTask.getCourseName();
		    	}
		    	return name;
		    }
		};
		reviewsTable.addColumn(courseNameColumn, "Course");
		
		// Column with the link to the document to review
		Column<ReviewTask, SafeHtml> linkColumn = new Column<ReviewTask, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(ReviewTask reviewTask) {
				SafeHtmlBuilder a = new SafeHtmlBuilder();
				if (reviewTask != null && reviewTask.getDocEntryTitle()!= null && reviewTask.getReviewId() != null){
					a.appendHtmlConstant("<a href='Review.html?edit="+reviewTask.getReviewId()+ "'>");
					a.appendHtmlConstant("<img height='19px' src='images/review.png'></img><span>" + reviewTask.getDocEntryTitle() + "</span></a>");
					
				}
				return a.toSafeHtml();
			}
		};
		reviewsTable.addColumn(linkColumn, "Document to review");
		
		// Column with the save date of the review
		Column<ReviewTask, SafeHtml> saveDateColumn = new Column<ReviewTask, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(ReviewTask reviewTask) {
				SafeHtmlBuilder a = new SafeHtmlBuilder();
				if (reviewTask != null && reviewTask.getSavedDate() != null){
					Date dateTime=new Date();
					String style = StyleLib.dueDateStyle(dateTime,reviewTask.getSavedDate());
					a.appendHtmlConstant("<div style='" + style + "'>" + StyleLib.dueDateFormat(reviewTask.getSavedDate()) + "</div>");
				}
				return a.toSafeHtml();
			}
		};
		reviewsTable.addColumn(saveDateColumn, "Save Date");
		
		// Column with the due date of the documents
		Column<ReviewTask, SafeHtml> dueDateColumn = new Column<ReviewTask, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(ReviewTask reviewTask) {
				SafeHtmlBuilder a = new SafeHtmlBuilder();
				if (reviewTask != null && reviewTask.getFinishDate() != null){
					Date dateTime=new Date();
					String style = StyleLib.dueDateStyle(dateTime,reviewTask.getFinishDate());
					a.appendHtmlConstant("<div style='" + style + "'>" + StyleLib.dueDateFormat(reviewTask.getFinishDate()) + "</div>");
				}
				return a.toSafeHtml();
			}
		};
		reviewsTable.addColumn(dueDateColumn, "Due Date");
		
		// Set rows per page
//		reviewsTable.setPageSize(PAGE_SIZE);
	}
}
