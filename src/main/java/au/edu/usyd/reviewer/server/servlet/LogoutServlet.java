package au.edu.usyd.reviewer.server.servlet;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.UserDao;

public class LogoutServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try{
			HttpSession	 session = request.getSession();
			User user = getLoggedUser(request);
			if (user != null) {
				logger.debug("Logging out user: " + user.getEmail());
			}
			if (session != null)
			{
				session.invalidate();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException(Constants.EXCEPTION_LOGOUT);
		}
	}
	
	public User getLoggedUser(HttpServletRequest request) {
		User user = null;
		try {
			Object obj = request.getSession().getAttribute("user");
			
			if (obj != null)
			{
				user = (User) obj;
			}
			Principal principal = request.getUserPrincipal();
			UserDao userDao = UserDao.getInstance();
			if  (user == null){
				user = userDao.getUserByEmail(principal.getName());
				request.getSession().setAttribute("user", user);
			} else if (principal.getName() != null && !principal.getName().equals(user.getEmail())){
				user = userDao.getUserByEmail(principal.getName());
				request.getSession().setAttribute("user", user);
			}
		} catch (MessageException e) {
			e.printStackTrace();
		}
		return user;
	}
}
