package au.edu.usyd.reviewer.server.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.iwrite.security.RandomMessageIDGenerator;
import au.edu.usyd.iwrite.security.WasmAuthenticationProtocol;
import au.edu.usyd.iwrite.security.WasmResponse;
import au.edu.usyd.iwrite.security.WasmService;
import au.edu.usyd.iwrite.security.WasmSocketFactory;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.server.Reviewer;
import au.edu.usyd.reviewer.server.util.DigitalSigner;

public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private DigitalSigner digitalSigner = Reviewer.getDigitalSigner();
		
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String userId = null;

		if (request.getUserPrincipal() != null) {
			// tomcat sso login
			userId = request.getUserPrincipal().getName();
		} else if (request.getParameter("loginName") != null && request.getParameter("sKey") != null) {
			// url login
			String sKey = request.getParameter("sKey");
			String loginName = request.getParameter("loginName");
			try {
				if (digitalSigner.verify(loginName, sKey)) {
					userId = loginName;
				}
			} catch (Exception e) {
				logger.error("Error authenticating user: " + loginName, e);
			}
		} else {
			// wasm login
			WasmSocketFactory wasmSocketFactory = new WasmSocketFactory();
			RandomMessageIDGenerator randomMessageIDGenerator = new RandomMessageIDGenerator();
			WasmAuthenticationProtocol wasmAuthenticationProtocol = new WasmAuthenticationProtocol(wasmSocketFactory, randomMessageIDGenerator);
			WasmService wasmService = new WasmService(wasmAuthenticationProtocol);
			WasmResponse wasmResponse = wasmService.authenticateOrRedirect(request, response);
			if (wasmResponse != null) {
				userId = wasmResponse.getLoginName();
			}
		}

		if (userId != null) {
			User user = new User();
			user.setId(userId);
			request.getSession().setAttribute("user", user);
			logger.debug("Logging in user: " + user.getId());
			response.sendRedirect(request.getRequestURL().toString().replace("/login", ""));
		}
	}
}
