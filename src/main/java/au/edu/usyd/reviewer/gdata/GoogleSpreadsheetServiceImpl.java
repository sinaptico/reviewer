/*******************************************************************************
 * Copyright 2010, 2011. Stephen O'Rouke. The University of Sydney
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

import com.google.gdata.client.spreadsheet.SpreadsheetService;

import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.docs.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
/**
 * Implements interaction with Google services, particulalry the spreadsheets using gdata
 * @author rafa
 *
 */

public class GoogleSpreadsheetServiceImpl {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private SpreadsheetService spreadsheetService;

    public GoogleSpreadsheetServiceImpl(String sessionToken) throws AuthenticationException, MalformedURLException {
		setAuthSubToken(sessionToken);
	}

	public GoogleSpreadsheetServiceImpl(String username, String password) throws AuthenticationException, MalformedURLException {
		setUserCredentials(username, password);		
	}

	public void addWorksheetRow(WorksheetEntry worksheetEntry, ListEntry listEntry) throws IOException, ServiceException {
		spreadsheetService.insert(worksheetEntry.getListFeedUrl(), listEntry);
	}
	
	public void addWorksheet(SpreadsheetEntry spreadsheetEntry, WorksheetEntry worksheetEntry, String title) throws IOException, ServiceException {
		WorksheetEntry worksheet = new WorksheetEntry();
		worksheet.setTitle(new PlainTextConstruct(title));

		spreadsheetService.insert(spreadsheetEntry.getWorksheetFeedUrl(), worksheet);		
	}	

	public List<WorksheetEntry> getSpreadsheetWorksheets(SpreadsheetEntry spreadsheetEntry) throws IOException, ServiceException {
//		logger.info("Getting spreadsheet worksheets: " + spreadsheetEntry.getResourceId());
		WorksheetFeed worksheetFeed = spreadsheetService.getFeed(spreadsheetEntry.getWorksheetFeedUrl(), WorksheetFeed.class);
		return worksheetFeed.getEntries();
	}

	public List<ListEntry> getWorksheetRows(WorksheetEntry worksheetEntry) throws IOException, ServiceException {
		ListFeed listFeed = spreadsheetService.getFeed(worksheetEntry.getListFeedUrl(), ListFeed.class);
		return listFeed.getEntries();
	}

	public void setAuthSubToken(String sessionToken) throws AuthenticationException, MalformedURLException {
		spreadsheetService = new SpreadsheetService("AuthSub - Spreadsheet Service");
		spreadsheetService.setAuthSubToken(sessionToken);
	}

	public void setUserCredentials(String username, String password) throws AuthenticationException, MalformedURLException {
		spreadsheetService = new SpreadsheetService("Client - Spreadsheet Service");
		spreadsheetService.setAuthSubToken(null);
		spreadsheetService.setUserCredentials(username, password);
	}
	
	public SpreadsheetService getSpreadsheetService(){
		return spreadsheetService;
	}
}
