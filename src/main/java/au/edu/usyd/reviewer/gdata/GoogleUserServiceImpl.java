/*******************************************************************************
 * Copyright 2010, 2011. The University of Sydney
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * - Contributors
 * 	Stephen O'Rourke
 * 	Jorge Villalon (CMM)
 * 	Ming Liu (AQG)
 * 	Rafael A. Calvo
 * 	Marco Garcia
 ******************************************************************************/
package au.edu.usyd.reviewer.gdata;

import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

import com.google.gdata.client.appsforyourdomain.UserService;

import com.google.gdata.data.appsforyourdomain.AppsForYourDomainErrorCode;
import com.google.gdata.data.appsforyourdomain.AppsForYourDomainException;
import com.google.gdata.data.appsforyourdomain.Login;
import com.google.gdata.data.appsforyourdomain.Name;
import com.google.gdata.data.appsforyourdomain.provisioning.UserEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.ServiceForbiddenException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

/**
 * User management with Google Apps
 * @author rafa
 *
 */
public class GoogleUserServiceImpl {

    private static final String SERVICE_VERSION = "2.0";
    private static final String APPS_FEEDS_URL = "https://apps-apis.google.com/a/feeds/";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String domain;
    private final String domainUrlBase;
    private UserService userService;

    public GoogleUserServiceImpl(String adminUsername, String adminPassword, String domain) throws AuthenticationException {
        this.domain = domain;
        this.domainUrlBase = APPS_FEEDS_URL + domain + "/";
        userService = new UserService("UserService");
        userService.setAuthSubToken(null);
        userService.setUserCredentials(adminUsername, adminPassword);
    }

    public UserEntry createUser(String username, String firstname, String lastname, String password) throws MessageException { 
    	UserEntry entry = new UserEntry();
    	try{
	        Login login = new Login();
	        login.setAgreedToTerms(true);
	        login.setChangePasswordAtNextLogin(false);
	        login.setUserName(username);
	        login.setPassword(password);
	        entry.addExtension(login);
	        Name name = new Name();
	        name.setGivenName(firstname);
	        name.setFamilyName(lastname);
	        entry.addExtension(name);
	        URL insertUrl = new URL(domainUrlBase + "user/" + SERVICE_VERSION);
	       	entry = userService.insert(insertUrl, entry);
    	} catch(Exception e){
    		if (e instanceof AppsForYourDomainException){
				AppsForYourDomainException afyde = (AppsForYourDomainException) e;
				if (afyde.getErrorCode().equals(AppsForYourDomainErrorCode.EntityExists)) {
					// continue
				} else if (afyde.getErrorCode().equals(AppsForYourDomainErrorCode.UserDeletedRecently)) {
					throw new MessageException(Constants.EXCEPTION_GOOGLE_USER_DELETED_RECENTLY + "\n" +  "Username: " + username + "\n" + Constants.EXCEPTION_GOOGLE_APPS  + e.getMessage());
				} else if (afyde.getErrorCode().equals(AppsForYourDomainErrorCode.UserSuspended)) {
					throw new MessageException(Constants.EXCEPTION_GOOGLE_USER_SUSPENDED + "\n" +  "Username: " + username + "\n" + Constants.EXCEPTION_GOOGLE_APPS  + e.getMessage());
				} else if (afyde.getErrorCode().equals(AppsForYourDomainErrorCode.DomainUserLimitExceeded)) {
					throw new MessageException(Constants.EXCEPTION_GOOGLE_DOMAIN_USER_LIMIT_EXCEEDED + "\n" + "Username: " + username + "\n" + Constants.EXCEPTION_GOOGLE_APPS  + e.getMessage());
				} else if (afyde.getErrorCode().equals(AppsForYourDomainErrorCode.DomainSuspended)) {
					throw new MessageException(Constants.EXCEPTION_GOOGLE_DOMAIN_SUSPENDED + "\n" + "Username: " + username + "\n" + Constants.EXCEPTION_GOOGLE_APPS  + e.getMessage());
				} else {
					throw new MessageException (Constants.EXCEPTION_FAILED_CREATE_USER + "\n" + "Username: " + username + "\n" + Constants.EXCEPTION_GOOGLE_APPS  + e.getMessage());
				}
			} else if (e instanceof ServiceForbiddenException){
				throw new  MessageException (Constants.EXCEPTION_FAILED_CREATE_USER + "\n" + "Username: " + username + "\n" + Constants.EXCEPTION_GOOGLE_APPS  + e.getMessage());
			} else  {
				e.printStackTrace();
				throw new MessageException (Constants.EXCEPTION_FAILED_CREATE_USER + "\n" + "Username: " + username );
			}
    	} 
        return entry;
    }

    public void deleteUser(String username) throws MessageException {
   	try{
    		URL deleteUrl = new URL(domainUrlBase + "user/" + SERVICE_VERSION + "/" + username);
    		userService.delete(deleteUrl);
    	} catch(Exception e){
    		if (e instanceof AppsForYourDomainException){
				AppsForYourDomainException afyde = (AppsForYourDomainException) e;
				if (afyde.getErrorCode().equals(AppsForYourDomainErrorCode.EntityDoesNotExist)) {
					throw new MessageException("Username: " + username + " - " + Constants.EXCEPTION_GOOGLE_ENTITY_NOT_EXIST);
				} else if (afyde.getErrorCode().equals(AppsForYourDomainErrorCode.UserDeletedRecently)) {
					// continue
				} else if (afyde.getErrorCode().equals(AppsForYourDomainErrorCode.UserSuspended)) {
					// continue
				} else if (afyde.getErrorCode().equals(AppsForYourDomainErrorCode.DomainSuspended)) {
					throw new MessageException("Username: " + username + " - " + Constants.EXCEPTION_GOOGLE_DOMAIN_SUSPENDED);
				} else {
					throw new MessageException ("Username: " + username + " - " + Constants.EXCEPTION_FAILED_DELETE_USER);
				}
			}  else {
				e.printStackTrace();
				throw new MessageException ("Username: " + username + " - " + Constants.EXCEPTION_FAILED_DELETE_USER);
			}
    	}
    }

    public String getDomain() {
        return domain;
    }

    public UserEntry retrieveUser(String username) throws MessageException {
    	UserEntry userEntry = null;
    	try{
    		URL retrieveUrl = new URL(domainUrlBase + "user/" + SERVICE_VERSION + "/" + username);
        	userEntry = userService.getEntry(retrieveUrl, UserEntry.class);
    	} catch (Exception e){
    		if (e instanceof AppsForYourDomainException){
				AppsForYourDomainException afyde = (AppsForYourDomainException) e;
				if (afyde.getErrorCode().equals(AppsForYourDomainErrorCode.EntityDoesNotExist)) {
					throw new MessageException("Username: " + username + " - " + Constants.EXCEPTION_GOOGLE_ENTITY_NOT_EXIST);
				} else if (afyde.getErrorCode().equals(AppsForYourDomainErrorCode.UserDeletedRecently)) {
					throw new MessageException("Username: " + username + " - " + Constants.EXCEPTION_GOOGLE_USER_DELETED_RECENTLY);
				} else if (afyde.getErrorCode().equals(AppsForYourDomainErrorCode.UserSuspended)) {
					throw new MessageException("Username: " + username + " - " + Constants.EXCEPTION_GOOGLE_USER_SUSPENDED);
				} else if (afyde.getErrorCode().equals(AppsForYourDomainErrorCode.DomainSuspended)) {
					throw new MessageException("Username: " + username + " - " + Constants.EXCEPTION_GOOGLE_DOMAIN_SUSPENDED);
				} else {
					e.printStackTrace();
					throw new MessageException ("Username: " + username + " - " + Constants.EXCEPTION_FAILED_RETRIEVE_USER);
				}
			}
    	}
    	return userEntry;
    }
}
