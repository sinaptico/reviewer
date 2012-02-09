package au.edu.usyd.reviewer.client.core.gwt;

import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.util.UrlLib;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class DocEntryWidget extends Composite {

	private final HorizontalPanel panel = new HorizontalPanel();
	private String id;
	private String title;
	private String domainName;
	private Boolean locked;
	//private DocEntry docEntrylocal;
	
	public DocEntryWidget(DocEntry docEntry, String title) {		
		this(docEntry.getDocumentId(), title, docEntry.getDomainName(), docEntry.getLocked());
		//docEntrylocal = docEntry;
	}

	public DocEntryWidget(String id, String title, String domainName, boolean locked) {
		this.id = id;
		this.title = title;
		this.domainName = domainName;
		this.locked = locked;		
		formatHTML();
		initWidget(panel);
	}

	private void formatHTML() {
		panel.clear();
		Anchor link = new Anchor();
		link.setTitle("Open in Google Docs");
		link.setTarget("_blank");
		if (id.startsWith("document:")) {
			link.setHref(UrlLib.documentUrl(id, domainName));
			link.setHTML("<img src='images/" + (!locked ? "google/icon_6_doc.gif" : "icon-padlock.jpg") + "'></img><span>" + title + "</span></a>");
		} else if (id.startsWith("presentation:")) {
			link.setHref(UrlLib.presentationUrl(id, domainName));
			link.setHTML("<img src='images/" + (!locked ? "google/icon_6_pres.gif" : "icon-padlock.jpg") + "'></img><span>" + title + "</span></a>");
		} else if (id.startsWith("spreadsheet:")) {
			link.setHref(UrlLib.spreadsheetUrl(id, domainName));
			link.setHTML("<img src='images/" + (!locked ? "google/icon_6_spread.gif" : "icon-padlock.jpg") + "'></img><span>" + title + "</span></a>");
		} else if (id.startsWith("folder:")) {
			link.setHref(UrlLib.folderUrl(id, domainName));
			link.setHTML("<img src='images/" + (!locked ? "google/icon_6_folder.gif" : "icon-padlock.jpg") + "'></img><span>" + title + "</span></a>");
		} else if (id.startsWith("file:")) {
			link.setHref(UrlLib.pdfDownloadUrl(id, id));
			//if (docEntrylocal.isUploaded()){
			  link.setHTML("<img src='images/" + (!locked ? "google/icon_6_doc.gif" : "icon-padlock.jpg") + "'></img><span>" + title + "</span></a>");
			//}
		}else {
			// nothing;
		}
		panel.add(link);
	}
}
