package au.edu.usyd.reviewer.server.util;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.UserDao;

public class ConnectionUtil {
	
	private static final Logger logger = LoggerFactory.getLogger("ConnectionUtil");
	
	public static void logout(HttpServletRequest request) throws IOException {
		try{
			HttpSession	 session = request.getSession();
			if (session != null)
			{
				session.invalidate();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException(Constants.EXCEPTION_LOGOUT);
		}
	}
	
}
