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

public class CommentsReviewForm extends ReviewForm<Review> {
	private VerticalPanel mainPanel = new VerticalPanel();
	private RichTextToolbar contentEditorToolbar;
	private RichTextArea contentEditor = new RichTextArea();
	private ScrollPanel contentViewPanel = new ScrollPanel();
	private VerticalPanel contentEditPanel = new VerticalPanel();
	private String contentWidth = "600px";
	private String contentHeight = "500px";
	private VerticalPanel reviewPanel = new VerticalPanel();

	public CommentsReviewForm() {
		contentEditorToolbar = new RichTextToolbar(contentEditor);
		contentEditPanel.add(contentEditorToolbar);
		contentEditPanel.add(contentEditor);
		initWidget(mainPanel);
	}

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

	@Override
	public Review getReview() {
		review.setContent(contentEditor.getHTML());
		return review;
	}

	@Override
	public boolean isModified() {
		return !contentEditor.getHTML().equals(review.getContent());
	}

	@Override
	public void onLoad() {
		setLocked(locked);
		contentEditor.setSize("880px", contentHeight);
		contentViewPanel.setSize(contentWidth, contentHeight);
		contentViewPanel.setStyleName("reviewContent");
		mainPanel.add(reviewPanel);
	}

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

	@Override
	public void setReview(Review review) {
		this.review = review;
		contentEditor.setHTML(this.review.getContent());
		contentViewPanel.setWidget(new HTML(this.review.getContent()));
	}
}