package au.edu.usyd.reviewer.client.review.form;

import au.edu.usyd.reviewer.client.core.Review;
import au.edu.usyd.reviewer.client.core.gwt.RichTextToolbar;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * <p>Class that extends {@link ReviewForm ReviewForm} and implements a RichTextArea and a RichTextToolBar for comments. ScreenShot:</p>
 * <img src="doc-files/richtextarea.png">
 */
public class CommentsReviewForm extends ReviewForm<Review> {
	
	/** The main panel. */
	private VerticalPanel mainPanel = new VerticalPanel();
	
	/** The content editor tool bar. */
	private RichTextToolbar contentEditorToolbar;
	
	/** RichTextArea where the comments are written. */
	private RichTextArea contentEditor = new RichTextArea();
	
	/** The content view panel. */
	private ScrollPanel contentViewPanel = new ScrollPanel();
	
	/** The content edit panel. */
	private VerticalPanel contentEditPanel = new VerticalPanel();
	
	/** The content width. */
	private String contentWidth = "600px";
	
	/** The content height. */
	private String contentHeight = "500px";
	
	/** The review panel. */
	private VerticalPanel reviewPanel = new VerticalPanel();

	/**
	 * Instantiates a new comments review form.
	 */
	public CommentsReviewForm() {
		contentEditorToolbar = new RichTextToolbar(contentEditor);
		contentEditPanel.add(contentEditorToolbar);
		contentEditPanel.add(contentEditor);
		initWidget(mainPanel);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.HasChangeHandlers#addChangeHandler(com.google.gwt.event.dom.client.ChangeHandler)
	 */
	@Override
	public HandlerRegistration addChangeHandler(final ChangeHandler handler) {
		final HandlerRegistration handlerRegistration1 = contentEditorToolbar.addChangeHandler(handler);
		final HandlerRegistration handlerRegistration2 = contentEditor.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				handler.onChange(new ChangeEvent() {
				});
			}
		});

		HandlerRegistration handlerRegistration = new HandlerRegistration() {
			@Override
			public void removeHandler() {
				handlerRegistration1.removeHandler();
				handlerRegistration2.removeHandler();
			}
		};
		return handlerRegistration;
	}

	/* (non-Javadoc)
	 * @see au.edu.usyd.reviewer.client.review.form.ReviewForm#getReview()
	 */
	@Override
	public Review getReview() {
		review.setContent(contentEditor.getHTML());
		return review;
	}

	/* (non-Javadoc)
	 * @see au.edu.usyd.reviewer.client.review.form.ReviewForm#isModified()
	 */
	@Override
	public boolean isModified() {
		return !contentEditor.getHTML().equals(review.getContent());
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.Widget#onLoad()
	 */
	@Override
	public void onLoad() {
		setLocked(locked);
		contentEditor.setSize("880px", contentHeight);
		contentViewPanel.setSize(contentWidth, contentHeight);
		contentViewPanel.setStyleName("reviewContent");
		mainPanel.add(reviewPanel);
	}

	/* (non-Javadoc)
	 * @see au.edu.usyd.reviewer.client.review.form.ReviewForm#setLocked(boolean)
	 */
	@Override
	public void setLocked(boolean locked) {
		this.locked = locked;
		reviewPanel.clear();
		if (locked) {
			reviewPanel.add(contentViewPanel);
		} else {
			reviewPanel.add(contentEditPanel);
		}
	}

	/* (non-Javadoc)
	 * @see au.edu.usyd.reviewer.client.review.form.ReviewForm#setReview(au.edu.usyd.reviewer.client.core.Review)
	 */
	@Override
	public void setReview(Review review) {
		this.review = review;
		contentEditor.setHTML(this.review.getContent());
		contentViewPanel.setWidget(new HTML(this.review.getContent()));
	}
}