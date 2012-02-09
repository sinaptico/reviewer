package au.edu.usyd.reviewer.server.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.core.User;

public class LogoutServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user != null) {
			logger.debug("Logging out user: " + user.getId());
		}
		request.getSession().invalidate();
		response.sendRedirect(request.getRequestURL().toString().replace("/logout", ""));
	}
}
