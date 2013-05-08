package au.edu.usyd.reviewer.server.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.UserDao;

public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(getClass());
//	private DigitalSigner digitalSigner = Reviewer.getDigitalSigner();

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String email = null;
		if (request.getUserPrincipal() != null) {
			// tomcat sso login
			email = request.getUserPrincipal().getName();
		} else {

		}

		if (email != null) {
			UserDao userDao = UserDao.getInstance();
			User user = null;
			try {
				user = userDao.getUserByEmail(email);
			} catch (MessageException e) {
				e.printStackTrace();
			}
			String password = request.getParameter("password");
			if (password != null && password.equals(user.getPassword())){
				request.getSession().setAttribute("user", user);
//				logger.debug("Logging in user: " + user.getEmail());
				if (user.isAdmin() || user.isSuperAdmin()){
					response.sendRedirect(request.getRequestURL().toString().replace("/Admin.html", ""));
				} else {
					response.sendRedirect(request.getRequestURL().toString().replace("/Assignments.html", ""));
				}
			} else {
				response.sendRedirect(request.getRequestURL().toString().replace("/login", ""));
				throw new IOException(Constants.EXCEPTION_INVALID_LOGIN);
			}
		}
	}
}