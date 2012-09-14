package au.edu.usyd.reviewer.server;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;

import org.junit.BeforeClass;
import org.junit.Test;

import au.edu.usyd.reviewer.client.core.User;

public class EmailNotifierUnitTest {
    private static EmailNotifier emailNotifier;
    private String domain = Reviewer.getGoogleDomain();

    @BeforeClass
    public static void setUp() throws NoSuchProviderException {
        //emailNotifier = new EmailNotifier(Reviewer.getGoogleUsername(), Reviewer.getGooglePassword());
        emailNotifier = new EmailNotifier(Reviewer.getEmailUsername(), Reviewer.getEmailPassword(), Reviewer.getSMTPHost(),Reviewer.getSMTPPort());
    }

    @Test
    public void testSendNotification() throws MessagingException, UnsupportedEncodingException {
    	
        User user = new User();
        user.setUsername("student01");
        user.setEmail("test.student01@"+domain);        
        user.setFirstname("Test");
        user.setLastname("Student01");

        emailNotifier.sendNotification(user, "test subject", "test message.");
        assertTrue(true);
    }
}
