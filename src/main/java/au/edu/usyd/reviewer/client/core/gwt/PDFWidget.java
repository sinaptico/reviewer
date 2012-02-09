package au.edu.usyd.reviewer.client.core.gwt;

import au.edu.usyd.reviewer.client.core.util.UrlLib;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.NamedFrame;

public class PDFWidget extends Composite {

	private final HorizontalPanel panel = new HorizontalPanel();
	private String title;
	private String filename;
	private String docId;
	private Long docVersion;

	public PDFWidget(String title, String filename, String docId, Long docVersion) {
		this.title = title;
		this.filename = filename;
		this.docId = docId;
		this.docVersion = docVersion;
		formatHTML();
		initWidget(panel);
	}

	private void formatHTML() {
		NamedFrame frame = new NamedFrame(docId);
		frame.setPixelSize(0, 0);
		frame.setVisible(false);

		Anchor link = new Anchor();
		link.setHref(UrlLib.pdfDownloadUrl(filename + ".pdf", docId, docVersion));
		link.setHTML("<img src='images/icon-pdf.gif'></img><span>" + title + "</span>");
		link.setTitle("Download");
		link.setTarget(docId);

		panel.clear();
		panel.add(link);
		panel.add(frame);
	}
}
