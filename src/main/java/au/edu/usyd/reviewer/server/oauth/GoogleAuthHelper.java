package au.edu.usyd.reviewer.server.oauth;



import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.server.UserDao;


/**
 * This class is used to access to Google with REST to obtain the authorization and authentication tokens of the user, in order to get his/her data
 * without use the google login page
 * https://developers.google.com/accounts/docs/OAuth2WebServer
 * @author mdagraca
 *
 */
public class GoogleAuthHelper {

	private final Logger logger = LoggerFactory.getLogger(GoogleAuthHelper.class);

	// Google Authorization Page to obtain the code of the user
	private final String AUTHORIZATION_URL="https://accounts.google.com/o/oauth2/auth?";
	// Google page to obtain the token or refresh the token of the user
	private final String TOKEN_URL="https://accounts.google.com/o/oauth2/token";
	
	// Scope has the Url of Google (drive, user info) that we want to access without login
	private final String SCOPE="https://www.googleapis.com/auth/drive https://docs.google.com/feeds https://spreadsheets.google.com/feeds";
	
	
	private UserDao userDao;
	
	public GoogleAuthHelper(){	
		userDao = UserDao.getInstance();
	}
	
	
	/**
	 * This method returns the Url of Google authorization page of user to access to his/her data
	 * This page return the code of authorization used to obtain the user token 
	 * @param currentUrl this page will be used to come back to the application after the authorization
	 * @param User user used to identify him/her in Google 
	 * @return String Google Authorization page
	 * @throws Exception
	 */
	public String getGoogleAuthorizationUrl(HttpServletRequest request,User user, String currentUrl) {
		String url = "";
		try{
			String email = user.getGoogleAppsEmail();
			Organization organization = user.getOrganization();			
			StringBuilder content = new StringBuilder();
			content.append(AUTHORIZATION_URL);
			content.append("&client_id=").append(organization.getGoogleClientId());
			content.append("&response_type=code");
			content.append("&scope=").append(URLEncoder.encode(SCOPE, "UTF-8"));
			content.append("&redirect_uri=").append(URLEncoder.encode(currentUrl, "UTF-8"));
			content.append("&login_hint=").append(user.getGoogleAppsEmail());
			content.append("&access_type=offline");
			url = content.toString();
		} catch(Exception e){
			logger.error("Failed getGoogleAuthorizationUrl");
			e.printStackTrace();
		}
		logger.error("URL " + url);
		return url;
	} 
	
		
	/**
	 * This method exchanges code for access token, refresh token and token id of the user to access to his/her data without login
	 * @param user owner of the tokens
	 * @param code code obtained from Google Authorization page
	 * @param currentUrl url to come back
	 * @return user wiht his/her tokens
	 * @throws Exception
	 */
	public User getUserTokens(User user, String authCode, String currentUrl) throws Exception {
		try{
			Organization organization = user.getOrganization();
			HttpClient httpclient = new HttpClient();
			PostMethod post = new PostMethod(TOKEN_URL);
			post.addParameter("code",authCode);
			post.addParameter("client_id",organization.getGoogleClientId());
			post.addParameter("client_secret",organization.getGoogleClientSecret());
			post.addParameter("redirect_uri",currentUrl);
			post.addParameter("grant_type","authorization_code");			
			httpclient.executeMethod(post);
			
		    String responseBody = post.getResponseBodyAsString();
		    JSONParser parser = new JSONParser();
		    Object obj = parser.parse(responseBody);
		    JSONObject jsonObject = (JSONObject) obj;
			
		    // Get token
			String token = (String) jsonObject.get("access_token");
			if (!StringUtil.isBlank(token)){
//				user.setGoogleToken(token);
			}
			
			// Get refresh token
			String refreshToken = (String) jsonObject.get("refresh_token");
			if (!StringUtil.isBlank(refreshToken)){
//				user.setGoogleRefreshToken(refreshToken);
			}
			
			if (!StringUtil.isBlank(token) && !StringUtil.isBlank(refreshToken) && !StringUtil.isBlank(token)){
				// save token in database 
				user = userDao.save(user);
			} 
		} catch(Exception e){
			logger.error("Failed to get user tokens");
			e.printStackTrace();
		} finally{
			return user;
		}
	}	
	
		/**
	 * This method is used to update the token after its expiration
	 * @param user
	 * @param currentUrl
	 * @return
	 * @throws Exception
	 */
	public User refreshUserTokens (User user) throws Exception {
		try{
			HttpClient httpclient = new HttpClient();
			PostMethod post = new PostMethod(TOKEN_URL);
			Organization organization = user.getOrganization();
			post.addParameter("client_secret",organization.getGoogleClientSecret());
			post.addParameter("grant_type","refresh_token");
//			post.addParameter("refresh_token",user.getGoogleRefreshToken());
			post.addParameter("client_id",organization.getGoogleClientId());
			
			httpclient.executeMethod(post);
		    String responseBody = post.getResponseBodyAsString();
		    JSONParser parser = new JSONParser();
		    Object obj = parser.parse(responseBody);
		    
			JSONObject jsonObject = (JSONObject) obj;
			String token = (String) jsonObject.get("access_token");
			
			if (!StringUtil.isBlank(token)){
//				logger.error("User : "  + user.getEmail() + " token " + user.getGoogleToken());
//				user.setGoogleToken(token);
				user = userDao.save(user);
			}
		} catch(Exception e){
			logger.error("Failed to refresh tokens" + e.getMessage());
			e.printStackTrace();	
		}
		return user; 
	}

}
