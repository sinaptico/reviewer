package au.edu.usyd.reviewer.client.core.gwt;

import au.edu.usyd.reviewer.client.core.util.UrlLib;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * <p>Class that extends Composite class and it is used to build links to ZIP files with PDF documents.</p>
 */
public class ZipWidget extends Composite {

	/** The panel where the link is placed. */
	private final HorizontalPanel panel = new HorizontalPanel();

	/** The ZIP file title. */
	private String title;
	
	/** The file name. */
	private String filename;
	
	/** The deadline id. */
	private Long deadlineId;
	
	/** The tutorial. */
	private String tutorial;
	
	/** The reviewing activity (Optional, only use when the ZIP file contains reviewing documents). */
	private String reviewingActivity=null;

	/**
	 * Instantiates a new zip widget.
	 *
	 * @param title the title
	 * @param filename the filename
	 * @param deadlineId the deadline id
	 * @param tutorial the tutorial
	 */
	public ZipWidget(String title, String filename, Long deadlineId, String tutorial) {
		this.title = title;
		this.filename = filename;
		this.deadlineId = deadlineId;
		this.tutorial = tutorial;
		formatHTML();
		initWidget(panel);
	}
	
	/**
	 * Instantiates a new zip widget.
	 *
	 * @param title the title
	 * @param filename the filename
	 * @param deadlineId the deadline id
	 * @param tutorial the tutorial
	 * @param reviewActivity the review activity
	 */
	public ZipWidget(String title, String filename, Long deadlineId, String tutorial, String reviewActivity) {
		this.title = title;
		this.filename = filename;
		this.deadlineId = deadlineId;
		this.tutorial = tutorial;
		this.reviewingActivity = reviewActivity;
		formatHTML();
		initWidget(panel);
	}	

	/**
	 * Method that builds the link with the id, title and domain name.
	 */
	private void formatHTML() {
		Anchor link = new Anchor();
		
		if (this.reviewingActivity == null){
			link.setHref(UrlLib.zipDownloadUrl(filename + ".zip", deadlineId, tutorial));
		}else{
			link.setHref(UrlLib.zipDownloadUrl(filename + ".zip", deadlineId, tutorial, reviewingActivity));
		}
			
		link.setHTML("<img src='images/icon-zip.gif'></img><span>" + title + "</span>");
		link.setTitle("Download all");
		panel.clear();
		panel.add(link);
	}
}
