package au.edu.usyd.reviewer.client.review.form;

import au.edu.usyd.reviewer.client.core.Review;

import com.google.gwt.junit.client.GWTTestCase;

public class CommentsReviewFormGwtTest extends GWTTestCase {

    private CommentsReviewForm commentsReviewForm;

    @Override
    public void gwtSetUp() {
        commentsReviewForm = new CommentsReviewForm();
        commentsReviewForm.onLoad();
    }

    public void testGetAndSetCourse() {
        Review review = new Review();
        review.setId(new Long(1));
        commentsReviewForm.setReview(review);

        Review actualReview = commentsReviewForm.getReview();
        assertSame(review, actualReview);
    }

    @Override
    public String getModuleName() {
        return "au.edu.usyd.reviewer.Review";
    }
}
