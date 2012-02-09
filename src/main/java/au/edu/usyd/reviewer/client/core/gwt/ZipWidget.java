package au.edu.usyd.reviewer.client.core.gwt;

import au.edu.usyd.reviewer.client.core.util.UrlLib;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class ZipWidget extends Composite {

	private final HorizontalPanel panel = new HorizontalPanel();

	private String title;
	private String filename;
	private Long deadlineId;
	private String tutorial;
	private String reviewingActivity=null;

	public ZipWidget(String title, String filename, Long deadlineId, String tutorial) {
		this.title = title;
		this.filename = filename;
		this.deadlineId = deadlineId;
		this.tutorial = tutorial;
		formatHTML();
		initWidget(panel);
	}
	
	public ZipWidget(String title, String filename, Long deadlineId, String tutorial, String reviewActivity) {
		this.title = title;
		this.filename = filename;
		this.deadlineId = deadlineId;
		this.tutorial = tutorial;
		this.reviewingActivity = reviewActivity;
		formatHTML();
		initWidget(panel);
	}	

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
