//package au.edu.usyd.reviewer.server.servlet;
//
//import static org.hamcrest.CoreMatchers.*;
//import static org.junit.Assert.assertThat;
//
//import org.junit.Test;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.mock.web.MockHttpServletResponse;
//
//import au.edu.usyd.reviewer.client.core.User;
//
//public class LogoutServletUnitTest {
//
//	@Test 
//	public void shouldLogoutUser() throws Exception {
//		User user = new User();
//		user.setUsername("gaylord");
//		
//		MockHttpServletResponse response = new MockHttpServletResponse();
//		MockHttpServletRequest request = new MockHttpServletRequest(null, "/reviewer/logout");
//		request.getSession().setAttribute("user", user);
//		
//		LogoutServlet logoutServlet = new LogoutServlet();
//		logoutServlet.doGet(request, response);
//		assertThat(request.getSession().getAttribute("user") , nullValue());
//		assertThat(response.getRedirectedUrl(), equalTo("http://localhost:80/reviewer"));
//	}	
//}
