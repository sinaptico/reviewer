/*
 * This software is published under the Apchae 2.0 licenses.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Author: Erik Scholtz 
 * Web: http://blog.elitecoderz.net
 */

package au.edu.usyd.reviewer.client.core.gwt;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.CustomButton;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Class that includes "Rich text option" into Review forms. 
 */
public class RichTextToolbar extends Composite implements HasChangeHandlers {
	
	/**
	 * Click Handler of the Toolbar *.
	 */
	private class EventHandler implements ClickHandler, KeyUpHandler, ChangeHandler {
		
		/* (non-Javadoc)
		 * @see com.google.gwt.event.dom.client.ChangeHandler#onChange(com.google.gwt.event.dom.client.ChangeEvent)
		 */
		@Override
		public void onChange(ChangeEvent event) {
			if (event.getSource().equals(fontlist)) {
				if (isHTMLMode()) {
					changeHtmlStyle("<span style=\"font-family: " + fontlist.getValue(fontlist.getSelectedIndex()) + ";\">", HTML_STYLE_CLOSE_SPAN);
				} else {
					styleTextFormatter.setFontName(fontlist.getValue(fontlist.getSelectedIndex()));
				}
			} else if (event.getSource().equals(colorlist)) {
				if (isHTMLMode()) {
					changeHtmlStyle("<span style=\"color: " + colorlist.getValue(colorlist.getSelectedIndex()) + ";\">", HTML_STYLE_CLOSE_SPAN);
				} else {
					styleTextFormatter.setForeColor(colorlist.getValue(colorlist.getSelectedIndex()));
				}
			}
		}

		/* (non-Javadoc)
		 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
		 */
		@Override
		public void onClick(ClickEvent event) {
			if (event.getSource().equals(bold)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_BOLD, HTML_STYLE_CLOSE_SPAN);
				} else {
					styleTextFormatter.toggleBold();
				}
			} else if (event.getSource().equals(italic)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_ITALIC, HTML_STYLE_CLOSE_SPAN);
				} else {
					styleTextFormatter.toggleItalic();
				}
			} else if (event.getSource().equals(underline)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_UNDERLINE, HTML_STYLE_CLOSE_SPAN);
				} else {
					styleTextFormatter.toggleUnderline();
				}
			} else if (event.getSource().equals(stroke)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_LINETHROUGH, HTML_STYLE_CLOSE_SPAN);
				} else {
					styleTextFormatter.toggleStrikethrough();
				}
			} else if (event.getSource().equals(subscript)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_SUBSCRIPT, HTML_STYLE_CLOSE_SUBSCRIPT);
				} else {
					styleTextFormatter.toggleSubscript();
				}
			} else if (event.getSource().equals(superscript)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_SUPERSCRIPT, HTML_STYLE_CLOSE_SUPERSCRIPT);
				} else {
					styleTextFormatter.toggleSuperscript();
				}
			} else if (event.getSource().equals(alignleft)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_ALIGNLEFT, HTML_STYLE_CLOSE_DIV);
				} else {
					styleTextFormatter.setJustification(RichTextArea.Justification.LEFT);
				}
			} else if (event.getSource().equals(alignmiddle)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_ALIGNCENTER, HTML_STYLE_CLOSE_DIV);
				} else {
					styleTextFormatter.setJustification(RichTextArea.Justification.CENTER);
				}
			} else if (event.getSource().equals(alignright)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_ALIGNRIGHT, HTML_STYLE_CLOSE_DIV);
				} else {
					styleTextFormatter.setJustification(RichTextArea.Justification.RIGHT);
				}
			} else if (event.getSource().equals(orderlist)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_ORDERLIST, HTML_STYLE_CLOSE_ORDERLIST);
				} else {
					styleTextFormatter.insertOrderedList();
				}
			} else if (event.getSource().equals(unorderlist)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_UNORDERLIST, HTML_STYLE_CLOSE_UNORDERLIST);
				} else {
					styleTextFormatter.insertUnorderedList();
				}
			} else if (event.getSource().equals(indentright)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_INDENTRIGHT, HTML_STYLE_CLOSE_DIV);
				} else {
					styleTextFormatter.rightIndent();
				}
			} else if (event.getSource().equals(indentleft)) {
				if (isHTMLMode()) {
					// TODO nothing can be done here at the moment
				} else {
					styleTextFormatter.leftIndent();
				}
			} else if (event.getSource().equals(generatelink)) {
				String url = Window.prompt(GUI_DIALOG_INSERTURL, "http://");
				if (url != null) {
					if (isHTMLMode()) {
						changeHtmlStyle("<a href=\"" + url + "\">", "</a>");
					} else {
						styleTextFormatter.createLink(url);
					}
				}
			} else if (event.getSource().equals(breaklink)) {
				if (isHTMLMode()) {
					// TODO nothing can be done here at the moment
				} else {
					styleTextFormatter.removeLink();
				}
			} else if (event.getSource().equals(insertimage)) {
				String url = Window.prompt(GUI_DIALOG_IMAGEURL, "http://");
				if (url != null) {
					if (isHTMLMode()) {
						changeHtmlStyle("<img src=\"" + url + "\">", "");
					} else {
						styleTextFormatter.insertImage(url);
					}
				}
			} else if (event.getSource().equals(insertline)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_HLINE, "");
				} else {
					styleTextFormatter.insertHorizontalRule();
				}
			} else if (event.getSource().equals(removeformatting)) {
				if (isHTMLMode()) {
					// TODO nothing can be done here at the moment
				} else {
					styleTextFormatter.removeFormat();
				}
			} else if (event.getSource().equals(texthtml)) {
				if (texthtml.isDown()) {
					styleText.setText(styleText.getHTML());
				} else {
					styleText.setHTML(styleText.getText());
				}
			} else if (event.getSource().equals(styleText)) {
				// Change invoked by the richtextArea
			}
			updateStatus();
		}

		/* (non-Javadoc)
		 * @see com.google.gwt.event.dom.client.KeyUpHandler#onKeyUp(com.google.gwt.event.dom.client.KeyUpEvent)
		 */
		@Override
		public void onKeyUp(KeyUpEvent event) {
			updateStatus();
		}
	}

	/** Local CONSTANTS *. */
	// ImageMap and CSS related
	private static final String HTTP_STATIC_ICONS_GIF = "http://blog.elitecoderz.net/wp-includes/js/tinymce/themes/advanced/img/icons.gif";

	/** The Constant CSS_ROOT_NAME. */
	private static final String CSS_ROOT_NAME = "RichTextToolbar";
	// Color and Fontlists - First Value (key) is the Name to display, Second
	// Value (value) is the HTML-Definition
	/** The Constant GUI_COLORLIST. */
	public final static HashMap<String, String> GUI_COLORLIST = new HashMap<String, String>();
	static {
		GUI_COLORLIST.put("White", "#FFFFFF");
		GUI_COLORLIST.put("Black", "#000000");
		GUI_COLORLIST.put("Red", "red");
		GUI_COLORLIST.put("Green", "green");
		GUI_COLORLIST.put("Yellow", "yellow");
		GUI_COLORLIST.put("Blue", "blue");
	}
	
	/** The Constant GUI_FONTLIST. */
	public final static HashMap<String, String> GUI_FONTLIST = new HashMap<String, String>();

	static {
		GUI_FONTLIST.put("Times New Roman", "Times New Roman");
		GUI_FONTLIST.put("Arial", "Arial");
		GUI_FONTLIST.put("Courier New", "Courier New");
		GUI_FONTLIST.put("Georgia", "Georgia");
		GUI_FONTLIST.put("Trebuchet", "Trebuchet");
		GUI_FONTLIST.put("Verdana", "Verdana");
	}
	// HTML Related (styles made by SPAN and DIV)
	/** The Constant HTML_STYLE_CLOSE_SPAN. */
	private static final String HTML_STYLE_CLOSE_SPAN = "</span>";
	
	/** The Constant HTML_STYLE_CLOSE_DIV. */
	private static final String HTML_STYLE_CLOSE_DIV = "</div>";
	
	/** The Constant HTML_STYLE_OPEN_BOLD. */
	private static final String HTML_STYLE_OPEN_BOLD = "<span style=\"font-weight: bold;\">";
	
	/** The Constant HTML_STYLE_OPEN_ITALIC. */
	private static final String HTML_STYLE_OPEN_ITALIC = "<span style=\"font-weight: italic;\">";
	
	/** The Constant HTML_STYLE_OPEN_UNDERLINE. */
	private static final String HTML_STYLE_OPEN_UNDERLINE = "<span style=\"font-weight: underline;\">";
	
	/** The Constant HTML_STYLE_OPEN_LINETHROUGH. */
	private static final String HTML_STYLE_OPEN_LINETHROUGH = "<span style=\"font-weight: line-through;\">";
	
	/** The Constant HTML_STYLE_OPEN_ALIGNLEFT. */
	private static final String HTML_STYLE_OPEN_ALIGNLEFT = "<div style=\"text-align: left;\">";
	
	/** The Constant HTML_STYLE_OPEN_ALIGNCENTER. */
	private static final String HTML_STYLE_OPEN_ALIGNCENTER = "<div style=\"text-align: center;\">";
	
	/** The Constant HTML_STYLE_OPEN_ALIGNRIGHT. */
	private static final String HTML_STYLE_OPEN_ALIGNRIGHT = "<div style=\"text-align: right;\">";

	/** The Constant HTML_STYLE_OPEN_INDENTRIGHT. */
	private static final String HTML_STYLE_OPEN_INDENTRIGHT = "<div style=\"margin-left: 40px;\">";
	// HTML Related (styles made by custom HTML-Tags)
	/** The Constant HTML_STYLE_OPEN_SUBSCRIPT. */
	private static final String HTML_STYLE_OPEN_SUBSCRIPT = "<sub>";
	
	/** The Constant HTML_STYLE_CLOSE_SUBSCRIPT. */
	private static final String HTML_STYLE_CLOSE_SUBSCRIPT = "</sub>";
	
	/** The Constant HTML_STYLE_OPEN_SUPERSCRIPT. */
	private static final String HTML_STYLE_OPEN_SUPERSCRIPT = "<sup>";
	
	/** The Constant HTML_STYLE_CLOSE_SUPERSCRIPT. */
	private static final String HTML_STYLE_CLOSE_SUPERSCRIPT = "</sup>";
	
	/** The Constant HTML_STYLE_OPEN_ORDERLIST. */
	private static final String HTML_STYLE_OPEN_ORDERLIST = "<ol><li>";
	
	/** The Constant HTML_STYLE_CLOSE_ORDERLIST. */
	private static final String HTML_STYLE_CLOSE_ORDERLIST = "</ol></li>";
	
	/** The Constant HTML_STYLE_OPEN_UNORDERLIST. */
	private static final String HTML_STYLE_OPEN_UNORDERLIST = "<ul><li>";

	/** The Constant HTML_STYLE_CLOSE_UNORDERLIST. */
	private static final String HTML_STYLE_CLOSE_UNORDERLIST = "</ul></li>";

	// HTML Related (styles without closing Tag)
	/** The Constant HTML_STYLE_HLINE. */
	private static final String HTML_STYLE_HLINE = "<hr style=\"width: 100%; height: 2px;\">";
	// GUI Related stuff
	/** The Constant GUI_DIALOG_INSERTURL. */
	private static final String GUI_DIALOG_INSERTURL = "Enter a link URL:";

	/** The Constant GUI_DIALOG_IMAGEURL. */
	private static final String GUI_DIALOG_IMAGEURL = "Enter an image URL:";
	
	/** The Constant GUI_LISTNAME_COLORS. */
	private static final String GUI_LISTNAME_COLORS = "Colors";

	/** The Constant GUI_LISTNAME_FONTS. */
	private static final String GUI_LISTNAME_FONTS = "Fonts";
	
	/** The Constant GUI_HOVERTEXT_SWITCHVIEW. */
	private static final String GUI_HOVERTEXT_SWITCHVIEW = "Switch View HTML/Source";
	
	/** The Constant GUI_HOVERTEXT_REMOVEFORMAT. */
	private static final String GUI_HOVERTEXT_REMOVEFORMAT = "Remove Formatting";
	
	/** The Constant GUI_HOVERTEXT_IMAGE. */
	private static final String GUI_HOVERTEXT_IMAGE = "Insert Image";
	
	/** The Constant GUI_HOVERTEXT_HLINE. */
	private static final String GUI_HOVERTEXT_HLINE = "Insert Horizontal Line";
	
	/** The Constant GUI_HOVERTEXT_BREAKLINK. */
	private static final String GUI_HOVERTEXT_BREAKLINK = "Break Link";
	
	/** The Constant GUI_HOVERTEXT_LINK. */
	private static final String GUI_HOVERTEXT_LINK = "Generate Link";
	
	/** The Constant GUI_HOVERTEXT_IDENTLEFT. */
	private static final String GUI_HOVERTEXT_IDENTLEFT = "Ident Left";
	
	/** The Constant GUI_HOVERTEXT_IDENTRIGHT. */
	private static final String GUI_HOVERTEXT_IDENTRIGHT = "Ident Right";
	
	/** The Constant GUI_HOVERTEXT_UNORDERLIST. */
	private static final String GUI_HOVERTEXT_UNORDERLIST = "Unordered List";
	
	/** The Constant GUI_HOVERTEXT_ORDERLIST. */
	private static final String GUI_HOVERTEXT_ORDERLIST = "Ordered List";
	
	/** The Constant GUI_HOVERTEXT_ALIGNRIGHT. */
	private static final String GUI_HOVERTEXT_ALIGNRIGHT = "Align Right";
	
	/** The Constant GUI_HOVERTEXT_ALIGNCENTER. */
	private static final String GUI_HOVERTEXT_ALIGNCENTER = "Align Center";
	
	/** The Constant GUI_HOVERTEXT_ALIGNLEFT. */
	private static final String GUI_HOVERTEXT_ALIGNLEFT = "Align Left";
	
	/** The Constant GUI_HOVERTEXT_SUPERSCRIPT. */
	private static final String GUI_HOVERTEXT_SUPERSCRIPT = "Superscript";
	
	/** The Constant GUI_HOVERTEXT_SUBSCRIPT. */
	private static final String GUI_HOVERTEXT_SUBSCRIPT = "Subscript";
	
	/** The Constant GUI_HOVERTEXT_STROKE. */
	private static final String GUI_HOVERTEXT_STROKE = "Stroke";
	
	/** The Constant GUI_HOVERTEXT_UNDERLINE. */
	private static final String GUI_HOVERTEXT_UNDERLINE = "Underline";
	
	/** The Constant GUI_HOVERTEXT_ITALIC. */
	private static final String GUI_HOVERTEXT_ITALIC = "Italic";

	/** The Constant GUI_HOVERTEXT_BOLD. */
	private static final String GUI_HOVERTEXT_BOLD = "Bold";

	/**
	 * Native JavaScript that returns the selected text and position of the
	 * start.
	 *
	 * @param elem the elem
	 * @return the selection
	 */
	public static native JsArrayString getSelection(Element elem) /*-{
																	var txt = "";
																	var pos = 0;
																	var range;
																	var parentElement;
																	var container;

																	if (elem.contentWindow.getSelection) {
																	txt = elem.contentWindow.getSelection();
																	pos = elem.contentWindow.getSelection().getRangeAt(0).startOffset;
																	} else if (elem.contentWindow.document.getSelection) {
																	txt = elem.contentWindow.document.getSelection();
																	pos = elem.contentWindow.document.getSelection().getRangeAt(0).startOffset;
																	} else if (elem.contentWindow.document.selection) {
																	range = elem.contentWindow.document.selection.createRange();
																	txt = range.text;
																	parentElement = range.parentElement();
																	container = range.duplicate();
																	container.moveToElementText(parentElement);
																	container.setEndPoint('EndToEnd', range);
																	pos = container.text.length - range.text.length;
																	}
																	return [""+txt,""+pos];
																	}-*/;

	/** Private Variables *. */
	// The main (Vertical)-Panel and the two inner (Horizontal)-Panels
	private VerticalPanel outer;

	/** The top panel. */
	private HorizontalPanel topPanel;
	
	/** The bottom panel. */
	private HorizontalPanel bottomPanel;

	// The RichTextArea this Toolbar referes to and the Interfaces to access the
	// RichTextArea
	/** The style text. */
	private RichTextArea styleText;

	/** The style text formatter. */
	private Formatter styleTextFormatter;
	// We use an internal class of the ClickHandler and the KeyUpHandler to be
	// private to others with these events
	/** The ev handler. */
	private EventHandler evHandler;
	// The Buttons of the Menubar
	/** The bold. */
	private ToggleButton bold;
	
	/** The italic. */
	private ToggleButton italic;
	
	/** The underline. */
	private ToggleButton underline;
	
	/** The stroke. */
	private ToggleButton stroke;
	
	/** The subscript. */
	private ToggleButton subscript;
	
	/** The superscript. */
	private ToggleButton superscript;
	
	/** The alignleft. */
	private PushButton alignleft;
	
	/** The alignmiddle. */
	private PushButton alignmiddle;
	
	/** The alignright. */
	private PushButton alignright;
	
	/** The orderlist. */
	private PushButton orderlist;
	
	/** The unorderlist. */
	private PushButton unorderlist;
	
	/** The indentleft. */
	private PushButton indentleft;
	
	/** The indentright. */
	private PushButton indentright;
	
	/** The generatelink. */
	private PushButton generatelink;
	
	/** The breaklink. */
	private PushButton breaklink;
	
	/** The insertline. */
	private PushButton insertline;
	
	/** The insertimage. */
	private PushButton insertimage;

	/** The removeformatting. */
	private PushButton removeformatting;
	
	/** The texthtml. */
	private ToggleButton texthtml;

	/** The fontlist. */
	private ListBox fontlist;

	/** The colorlist. */
	private ListBox colorlist;

	/**
	 * Constructor of the Toolbar *.
	 *
	 * @param richtext the richtext
	 */
	public RichTextToolbar(RichTextArea richtext) {
		// Initialize the main-panel
		outer = new VerticalPanel();

		// Initialize the two inner panels
		topPanel = new HorizontalPanel();
		bottomPanel = new HorizontalPanel();
		topPanel.setStyleName(CSS_ROOT_NAME);
		bottomPanel.setStyleName(CSS_ROOT_NAME);

		// Save the reference to the RichText area we refer to and get the
		// interfaces to the stylings

		styleText = richtext;
		styleTextFormatter = styleText.getFormatter();

		// Set some graphical options, so this toolbar looks how we like it.
		topPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		bottomPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		// Add the two inner panels to the main panel
		outer.add(topPanel);
		outer.add(bottomPanel);

		// Some graphical stuff to the main panel and the initialisation of the
		// new widget
		outer.setWidth("100%");
		outer.setStyleName(CSS_ROOT_NAME);
		initWidget(outer);

		//
		evHandler = new EventHandler();

		// Add KeyUp and Click-Handler to the RichText, so that we can actualize
		// the toolbar if neccessary
		styleText.addKeyUpHandler(evHandler);
		styleText.addClickHandler(evHandler);

		// Now lets fill the new toolbar with life
		buildTools();
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.HasChangeHandlers#addChangeHandler(com.google.gwt.event.dom.client.ChangeHandler)
	 */
	@Override
	public HandlerRegistration addChangeHandler(final ChangeHandler handler) {

		final List<HandlerRegistration> handlerRegistrations = new LinkedList<HandlerRegistration>();
		handlerRegistrations.add(fontlist.addChangeHandler(handler));
		handlerRegistrations.add(colorlist.addChangeHandler(handler));
		CustomButton[] buttons = new CustomButton[] { bold, italic, underline, stroke, subscript, superscript, alignleft, alignmiddle, alignright, orderlist, unorderlist, indentleft, indentright, generatelink, breaklink, insertline, insertimage, removeformatting };
		for (CustomButton button : buttons) {
			handlerRegistrations.add(button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					handler.onChange(new ChangeEvent() {
					});
				}
			}));
		}

		HandlerRegistration handlerRegistration = new HandlerRegistration() {
			@Override
			public void removeHandler() {
				for (HandlerRegistration handlerRegistration : handlerRegistrations) {
					handlerRegistration.removeHandler();
				}
			}
		};
		return handlerRegistration;
	}

	/**
	 * Initialize the options on the toolbar *.
	 */
	private void buildTools() {
		// Init the TOP Panel forst
		topPanel.add(bold = createToggleButton(HTTP_STATIC_ICONS_GIF, 0, 0, 20, 20, GUI_HOVERTEXT_BOLD));
		topPanel.add(italic = createToggleButton(HTTP_STATIC_ICONS_GIF, 0, 60, 20, 20, GUI_HOVERTEXT_ITALIC));
		topPanel.add(underline = createToggleButton(HTTP_STATIC_ICONS_GIF, 0, 140, 20, 20, GUI_HOVERTEXT_UNDERLINE));
		topPanel.add(stroke = createToggleButton(HTTP_STATIC_ICONS_GIF, 0, 120, 20, 20, GUI_HOVERTEXT_STROKE));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(subscript = createToggleButton(HTTP_STATIC_ICONS_GIF, 0, 600, 20, 20, GUI_HOVERTEXT_SUBSCRIPT));
		topPanel.add(superscript = createToggleButton(HTTP_STATIC_ICONS_GIF, 0, 620, 20, 20, GUI_HOVERTEXT_SUPERSCRIPT));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(alignleft = createPushButton(HTTP_STATIC_ICONS_GIF, 0, 460, 20, 20, GUI_HOVERTEXT_ALIGNLEFT));
		topPanel.add(alignmiddle = createPushButton(HTTP_STATIC_ICONS_GIF, 0, 420, 20, 20, GUI_HOVERTEXT_ALIGNCENTER));
		topPanel.add(alignright = createPushButton(HTTP_STATIC_ICONS_GIF, 0, 480, 20, 20, GUI_HOVERTEXT_ALIGNRIGHT));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(orderlist = createPushButton(HTTP_STATIC_ICONS_GIF, 0, 80, 20, 20, GUI_HOVERTEXT_ORDERLIST));
		topPanel.add(unorderlist = createPushButton(HTTP_STATIC_ICONS_GIF, 0, 20, 20, 20, GUI_HOVERTEXT_UNORDERLIST));
		topPanel.add(indentright = createPushButton(HTTP_STATIC_ICONS_GIF, 0, 400, 20, 20, GUI_HOVERTEXT_IDENTRIGHT));
		topPanel.add(indentleft = createPushButton(HTTP_STATIC_ICONS_GIF, 0, 540, 20, 20, GUI_HOVERTEXT_IDENTLEFT));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(generatelink = createPushButton(HTTP_STATIC_ICONS_GIF, 0, 500, 20, 20, GUI_HOVERTEXT_LINK));
		topPanel.add(breaklink = createPushButton(HTTP_STATIC_ICONS_GIF, 0, 640, 20, 20, GUI_HOVERTEXT_BREAKLINK));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(insertline = createPushButton(HTTP_STATIC_ICONS_GIF, 0, 360, 20, 20, GUI_HOVERTEXT_HLINE));
		topPanel.add(insertimage = createPushButton(HTTP_STATIC_ICONS_GIF, 0, 380, 20, 20, GUI_HOVERTEXT_IMAGE));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(removeformatting = createPushButton(HTTP_STATIC_ICONS_GIF, 20, 460, 20, 20, GUI_HOVERTEXT_REMOVEFORMAT));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(texthtml = createToggleButton(HTTP_STATIC_ICONS_GIF, 0, 260, 20, 20, GUI_HOVERTEXT_SWITCHVIEW));

		// Init the BOTTOM Panel
		bottomPanel.add(fontlist = createFontList());
		bottomPanel.add(new HTML("&nbsp;"));
		bottomPanel.add(colorlist = createColorList());
	}

	/**
	 * Method called to toggle the style in HTML-Mode *.
	 *
	 * @param startTag the start tag
	 * @param stopTag the stop tag
	 */
	private void changeHtmlStyle(String startTag, String stopTag) {
		JsArrayString tx = getSelection(styleText.getElement());
		String txbuffer = styleText.getText();
		Integer startpos = Integer.parseInt(tx.get(1));
		String selectedText = tx.get(0);
		styleText.setText(txbuffer.substring(0, startpos) + startTag + selectedText + stopTag + txbuffer.substring(startpos + selectedText.length()));
	}

	/**
	 * Method to create the colorlist for the toolbar *.
	 *
	 * @return the list box
	 */
	private ListBox createColorList() {
		ListBox mylistBox = new ListBox();
		mylistBox.addChangeHandler(evHandler);
		mylistBox.setVisibleItemCount(1);

		mylistBox.addItem(GUI_LISTNAME_COLORS);
		for (String name : GUI_COLORLIST.keySet()) {
			mylistBox.addItem(name, GUI_COLORLIST.get(name));
		}

		return mylistBox;
	}

	/**
	 * Method to create the fontlist for the toolbar *.
	 *
	 * @return the list box
	 */
	private ListBox createFontList() {
		ListBox mylistBox = new ListBox();
		mylistBox.addChangeHandler(evHandler);
		mylistBox.setVisibleItemCount(1);

		mylistBox.addItem(GUI_LISTNAME_FONTS);
		for (String name : GUI_FONTLIST.keySet()) {
			mylistBox.addItem(name, GUI_FONTLIST.get(name));
		}

		return mylistBox;
	}

	/**
	 * Method to create a Push button for the toolbar *.
	 *
	 * @param url the url
	 * @param top the top
	 * @param left the left
	 * @param width the width
	 * @param height the height
	 * @param tip the tip
	 * @return the push button
	 */
	private PushButton createPushButton(String url, Integer top, Integer left, Integer width, Integer height, String tip) {
		Image extract = new Image(url, left, top, width, height);
		PushButton tb = new PushButton(extract);
		tb.setHeight(height + "px");
		tb.setWidth(width + "px");
		tb.addClickHandler(evHandler);
		if (tip != null) {
			tb.setTitle(tip);
		}
		return tb;
	}

	/**
	 * Method to create a Toggle button for the toolbar *.
	 *
	 * @param url the url
	 * @param top the top
	 * @param left the left
	 * @param width the width
	 * @param height the height
	 * @param tip the tip
	 * @return the toggle button
	 */
	private ToggleButton createToggleButton(String url, Integer top, Integer left, Integer width, Integer height, String tip) {
		Image extract = new Image(url, left, top, width, height);
		ToggleButton tb = new ToggleButton(extract);
		tb.setHeight(height + "px");
		tb.setWidth(width + "px");
		tb.addClickHandler(evHandler);
		if (tip != null) {
			tb.setTitle(tip);
		}
		return tb;
	}

	/**
	 * Private method with a more understandable name to get if HTML mode is on
	 * or not.
	 *
	 * @return the boolean
	 */
	private Boolean isHTMLMode() {
		return texthtml.isDown();
	}

	/**
	 * Private method to set the toggle buttons and disable/enable buttons which
	 * do not work in html-mode.
	 */
	private void updateStatus() {
		if (styleTextFormatter != null) {
			bold.setDown(styleTextFormatter.isBold());
			italic.setDown(styleTextFormatter.isItalic());
			underline.setDown(styleTextFormatter.isUnderlined());
			subscript.setDown(styleTextFormatter.isSubscript());
			superscript.setDown(styleTextFormatter.isSuperscript());
			stroke.setDown(styleTextFormatter.isStrikethrough());
		}

		if (isHTMLMode()) {
			removeformatting.setEnabled(false);
			indentleft.setEnabled(false);
			breaklink.setEnabled(false);
		} else {
			removeformatting.setEnabled(true);
			indentleft.setEnabled(true);
			breaklink.setEnabled(true);
		}
	}

}
