package au.edu.usyd.reviewer.client.core.gwt;

import au.edu.usyd.reviewer.client.core.util.UrlLib;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.NamedFrame;

/**
 * <p>Class that extends Composite class and it is used to build links to PDF documents, 
 * includes document information such as tile, file name, document id and version.</p>
 */
public class PDFWidget extends Composite {

	/** The panel where the link is placed. */
	private final HorizontalPanel panel = new HorizontalPanel();
	
	/** The document title. */
	private String title;
	
	/** The file name. */
	private String filename;
	
	/** The document id. */
	private String docId;
	
	/** The document version. */
	private Long docVersion;

	/**
	 * Instantiates a new pDF widget.
	 *
	 * @param title the title
	 * @param filename the filename
	 * @param docId the doc id
	 * @param docVersion the doc version
	 */
	public PDFWidget(String title, String filename, String docId, Long docVersion) {
		this.title = title;
		this.filename = filename;
		this.docId = docId;
		this.docVersion = docVersion;
		formatHTML();
		initWidget(panel);
	}

	/**
	 * Method that builds the link with the id, title and document version.
	 */
	private void formatHTML() {
		NamedFrame frame = new NamedFrame(docId);
		frame.setPixelSize(0, 0);
		frame.setVisible(false);

		Anchor link = new Anchor();
		link.setHref(UrlLib.pdfDownloadUrl(filename + ".pdf", docId, docVersion));
		link.setHTML("<div style='padding-top: 4.5px;'><img src='images/icon-pdf.gif'></img><span>" + title + "</span></div>");
		link.setTitle("Download");
		link.setTarget(docId);

		panel.clear();
		panel.add(link);
		panel.add(frame);
	}
}
