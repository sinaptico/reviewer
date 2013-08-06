package au.edu.usyd.reviewer.server.oauth;



import java.net.URLEncoder;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.StringUtil;


/**
 * This class is used to access to Google with REST to obtain the authorization code of the user, in order to get his/her data
 * without loogin the user in Google
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
	
	// Client Id for Web Applications
	private final String CLIENT_ID = "894979939992.apps.googleusercontent.com";
	// Client Secret
	private final String CLIENT_SECRET = "RAEdhNj8lEFfpOos9_0CL5Z9"; 
	
	// Scope has the Url of Google (drive, user info) that we want to access without login
	private final String SCOPE_DRIVE_URL="https://www.googleapis.com/auth/drive.file";
	private final String SCOPE_USERINFO_EMAIL ="https://www.googleapis.com/auth/userinfo.email";
	private final String SCOPE_USERINFO_PROFILE="https://www.googleapis.com/auth/userinfo.profile";
	
	public GoogleAuthHelper(){	
	}
	
	
	/**
	 * This method returns the Url of Google authorization page of user to access to his/her data
	 * This page return the code of authorization used to obtain the user token 
	 * @param currentUrl this page will be used to come back to the application after the authorization
	 * @param User user used to identify him/her in Google 
	 * @return String Google Authorization page
	 * @throws Exception
	 */
	public String getGoogleAuthorizationUrl(User user, String currentUrl) {
		String url = "";
		try{
			StringBuilder content = new StringBuilder();
			content.append(AUTHORIZATION_URL);
			content.append("response_type=code");
			content.append("&access_type=offline");
			content.append("&client_id=").append(CLIENT_ID);
			content.append("&redirect_uri=").append(URLEncoder.encode(currentUrl, "UTF-8"));
			content.append("&scope=").append(URLEncoder.encode(SCOPE_DRIVE_URL + " " + SCOPE_USERINFO_EMAIL + " " + SCOPE_USERINFO_PROFILE, "UTF-8"));
			content.append("&login_hint=").append(user.getGoogleAppsEmail());
			url = content.toString();
			logger.error("MARIELA - Authorization URL: " + url);
		} catch(Exception e){
			e.printStackTrace();
		}
		return url;
	} 
	
		
	/**
	 * This method obtain the token and refresh token of the user to access to his/her data without login
	 * @param user owner of the tokens
	 * @param code code obtained from Google Authorization page
	 * @param currentUrl url to come back
	 * @return user wiht his/her tokens
	 * @throws Exception
	 */
	public User getUserTokens (User user, String authCode, String currentUrl) throws Exception {
		try{
			HttpClient httpclient = new HttpClient();
			PostMethod post = new PostMethod(TOKEN_URL);
			post.addParameter("code",authCode);
			post.addParameter("grant_type","authorization_code");
			post.addParameter("client_id",CLIENT_ID);
			post.addParameter("client_secret",CLIENT_SECRET);
			post.addParameter("redirect_uri",currentUrl);
			httpclient.executeMethod(post);
		    String responseBody = post.getResponseBodyAsString();
		    JSONParser parser = new JSONParser();
		    Object obj = parser.parse(responseBody);
		    logger.error("MARIELA - Tokens response body : "  + obj.toString());    
			JSONObject jsonObject = (JSONObject) obj;
			
			String token = (String) jsonObject.get("access_token");
			logger.error("MARIELA - User : "  + user.getEmail() + " code " + authCode);
			if (!StringUtil.isBlank(token)){
				logger.error("MARIELA - User : "  + user.getEmail() + " token " + user.getGoogleToken());
				user.setGoogleToken(token);
			}
			
			String refreshToken = (String) jsonObject.get("refresh_token");
			if (!StringUtil.isBlank(refreshToken)){
				logger.error("MARIELA - User : "  + user.getEmail() + " refresh token " + user.getGoogleRefreshToken());
				user.setGoogleRefreshToken(refreshToken);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return user; 
	}	
	
	
	public User refreshUserTokens (User user, String currentUrl) throws Exception {
		try{
			HttpClient httpclient = new HttpClient();
			PostMethod post = new PostMethod(TOKEN_URL);
			post.addParameter("refresh_token",user.getGoogleRefreshToken());
			post.addParameter("grant_type","refresh_token");
			post.addParameter("client_id",CLIENT_ID);
			post.addParameter("client_secret",CLIENT_SECRET);
			post.addParameter("redirect_uri",currentUrl);
			
			httpclient.executeMethod(post);
		    String responseBody = post.getResponseBodyAsString();
		    JSONParser parser = new JSONParser();
		    Object obj = parser.parse(responseBody);
		    
			JSONObject jsonObject = (JSONObject) obj;
			String token = (String) jsonObject.get("access_token");
			
			if (!StringUtil.isBlank(token)){
				logger.error("User : "  + user.getEmail() + " token " + user.getGoogleToken());
				user.setGoogleToken(token);
			}
		} catch(Exception e){
			logger.error("MARIELA" + e.getMessage());
		}
		return user; 
	}	
	
}
