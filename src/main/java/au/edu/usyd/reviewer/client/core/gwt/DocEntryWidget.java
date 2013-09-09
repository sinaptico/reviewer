package au.edu.usyd.reviewer.client.core.gwt;

import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.UrlLib;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * <p>Class that extends Composite class and it is used to build links to Google documents, 
 * includes document information such as tile, link, domain name and if the document is locked.</p>
 */
public class DocEntryWidget extends Composite {

	/** The panel where the link is placed. */
	private final HorizontalPanel panel = new HorizontalPanel();
	
	/** The id. */
	private String id;
	
	/** The document title. */
	private String title;
	
	/** The domain name. */
	private String domainName;
	
	/** Boolean that defines if the document is locked. */
	private Boolean locked;
	//
	
	private User loggedUser;
	
	/**
	 * Instantiates a new doc entry widget.
	 *
	 * @param docEntry the doc entry
	 * @param title the title
	 * @param User loggedUser
	 */
	public DocEntryWidget(DocEntry docEntry, String title, User user) {		
		this(docEntry.getDocumentId(), title, docEntry.getDomainName(), docEntry.getLocked(), user);
		//docEntrylocal = docEntry;
	}

	/**
	 * Instantiates a new doc entry widget.
	 *
	 * @param id the id
	 * @param title the title
	 * @param domainName the domain name
	 * @param locked the locked
	 * @param User loggedUser
	 */
	public DocEntryWidget(String id, String title, String domainName, boolean locked, User user) {
		this.id = id;
		this.title = title;
		this.domainName = domainName;
		this.locked = locked;		
		this.loggedUser = user;
		formatHTML();
		initWidget(panel);
	}

	/**
	 * Method that builds the link with the id, title and domain name.
	 */
	private void formatHTML() {
		panel.clear();
		Anchor link = new Anchor();
		link.setTitle("Open in Google Docs");
		link.setTarget("_blank");
		String hostPageBaseURL = GWT.getHostPageBaseURL();
		if (id.startsWith("document:")) {
			link.setHref(UrlLib.documentUrl(id, domainName, loggedUser, hostPageBaseURL));
			link.setHTML("<div style='padding-top: 4.5px;'><img style='width: 12px; height: 11px;' src='images/" + (!locked ? "google/icon_6_doc.gif" : "icon-padlock.jpg") + "'></img><span>" + title + "</span></a></div>");
		} else if (id.startsWith("presentation:")) {
			link.setHref(UrlLib.presentationUrl(id, domainName, loggedUser,hostPageBaseURL));
			link.setHTML("<div style='padding-top: 4.5px;'><img style='width: 12px; height: 11px;' src='images/" + (!locked ? "google/icon_6_pres.gif" : "icon-padlock.jpg") + "'></img><span>" + title + "</span></a></div>");
		} else if (id.startsWith("spreadsheet:")) {
			link.setHref(UrlLib.spreadsheetUrl(id, domainName, loggedUser,hostPageBaseURL));
			link.setHTML("<div style='padding-top: 4.5px;'><img style='width: 12px; height: 11px;' src='images/" + (!locked ? "google/icon_6_spread.gif" : "icon-padlock.jpg") + "'></img><span>" + title + "</span></a></div>");
		} else if (id.startsWith("folder:")) {
			link.setHref(UrlLib.folderUrl(id, domainName, loggedUser,hostPageBaseURL));
			if (title.startsWith("* See ")){
				link.setHTML("<div style='padding-top: 4.5px;'><img style='width: 12px; height: 11px;' src='images/" + (!locked ? "google/icon_6_doc.gif" : "icon-padlock.jpg") + "'></img><span>" + title + "</span></a></div>");
			}else{
				link.setHTML("<div style='padding-top: 4.5px;'><img style='width: 12px; height: 11px;' src='images/" + (!locked ? "google/icon_6_folder.gif" : "icon-padlock.jpg") + "'></img><span>" + title + "</span></a></div>");
			}
		} else if (id.startsWith("file:")) {
			link.setHref(UrlLib.pdfDownloadUrl(id, id));
			//if (docEntrylocal.isUploaded()){
			  link.setHTML("<div style='padding-top: 4.5px;'><img style='width: 12px; height: 11px;' src='images/" + (!locked ? "google/icon_6_doc.gif" : "icon-padlock.jpg") + "'></img><span>" + title + "</span></a></div>");
			//}
		}else {
			// nothing;
		}
		panel.add(link);
	}	
}
