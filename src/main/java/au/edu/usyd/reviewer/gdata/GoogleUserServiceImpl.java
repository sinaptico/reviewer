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

import com.google.gdata.client.appsforyourdomain.UserService;

import com.google.gdata.data.appsforyourdomain.AppsForYourDomainException;
import com.google.gdata.data.appsforyourdomain.Login;
import com.google.gdata.data.appsforyourdomain.Name;
import com.google.gdata.data.appsforyourdomain.provisioning.UserEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
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

    public UserEntry createUser(String username, String firstname, String lastname, String password) throws AppsForYourDomainException, ServiceException, IOException {
//        logger.info("Creating user: id='" + username + "' firstname='" + firstname + "' lastname='" + lastname + "'");
        UserEntry entry = new UserEntry();
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
        return userService.insert(insertUrl, entry);
    }

    public void deleteUser(String username) throws AppsForYourDomainException, ServiceException, IOException {
        logger.info("Deleting user: id='" + username + "'");
        URL deleteUrl = new URL(domainUrlBase + "user/" + SERVICE_VERSION + "/" + username);
        userService.delete(deleteUrl);
    }

    public String getDomain() {
        return domain;
    }

    public UserEntry retrieveUser(String username) throws AppsForYourDomainException, ServiceException, IOException {
//        logger.info("Retrieving user: id='" + username + "'");
        URL retrieveUrl = new URL(domainUrlBase + "user/" + SERVICE_VERSION + "/" + username);
        return userService.getEntry(retrieveUrl, UserEntry.class);
    }
}
