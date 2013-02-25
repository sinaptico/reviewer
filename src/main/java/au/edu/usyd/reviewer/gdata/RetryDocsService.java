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

import com.google.gdata.client.docs.DocsService;

import com.google.gdata.data.IEntry;
import com.google.gdata.data.IFeed;
import com.google.gdata.data.media.IMediaContent;
import com.google.gdata.data.media.MediaSource;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class RetryDocsService extends DocsService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private int maxRetryAttempts = 5;

	public RetryDocsService(String applicationName) {
		super(applicationName);
	}

	@Override
	public <E extends IEntry> E getEntry(URL url, Class<E> clazz) throws IOException, ServiceException {
		
		int retryAttempts = 0;
		while (retryAttempts < maxRetryAttempts) {
			retryAttempts++;
			try {
				return super.getEntry(url, clazz);
			} catch (ServiceException e) {
				handleException(retryAttempts, e);
			} catch (IOException e) {
				handleException(retryAttempts, e);
			} catch (RuntimeException e) {
				handleException(retryAttempts, e);
			}
		}
		throw new RuntimeException("Failed to get entry");
	}

	@Override
	public <F extends IFeed> F getFeed(URL url, Class<F> clazz) throws IOException, ServiceException {
		int retryAttempts = 0;
		while (retryAttempts < maxRetryAttempts) {
			retryAttempts++;
			try {
				return super.getFeed(url, clazz);
			} catch (ServiceException e) {
				handleException(retryAttempts, e);
			} catch (IOException e) {
				handleException(retryAttempts, e);
			} catch (RuntimeException e) {
				handleException(retryAttempts, e);
			}
		}
		throw new RuntimeException("Failed to get feed");
	}

	public int getMaxRetryAttempts() {
		return maxRetryAttempts;
	}

	@Override
	public MediaSource getMedia(IMediaContent mc) throws IOException, ServiceException {
		int retryAttempts = 0;
		while (retryAttempts < maxRetryAttempts) {
			retryAttempts++;
			try {
				return super.getMedia(mc);
			} catch (ServiceException e) {
				handleException(retryAttempts, e);
			} catch (IOException e) {
				handleException(retryAttempts, e);
			} catch (RuntimeException e) {
				handleException(retryAttempts, e);
			}
		}
		throw new RuntimeException("Failed to get media");
	}

	protected <E extends Exception> void handleException(int retryAttempts, E e) throws E, ServiceException {
		if (retryAttempts >= maxRetryAttempts) {
//            logger.error("Service call aborted after failed attempt " + retryAttempts, e);
			throw e;
		} else if (e instanceof AuthenticationException) {	
			if (e.getMessage() != null && e.getMessage().contains("Token expired")) {
				this.handleSessionExpiredException(new SessionExpiredException(e.getMessage()));
				requestFactory.setAuthToken(getAuthTokenFactory().getAuthToken());
			}
		}
//        logger.info("Retrying service call after failed attempt " + retryAttempts, e);
	}

	@Override
	public <E extends IEntry> E insert(URL url, E entry) throws IOException, ServiceException {
		int retryAttempts = 0;
		while (retryAttempts < maxRetryAttempts) {
			retryAttempts++;
			try {
				return super.insert(url, entry);
			} catch (ServiceException e) {
				handleException(retryAttempts, e);
			} catch (IOException e) {
				handleException(retryAttempts, e);
			} catch (RuntimeException e) {
				handleException(retryAttempts, e);
			}
		}
		throw new RuntimeException("Failed to insert entry");
	}

	public void setMaxRetryAttempts(int maxRetryAttempts) {
		this.maxRetryAttempts = maxRetryAttempts;
	}

	@Override
	public <E extends IEntry> E update(URL url, E entry) throws IOException, ServiceException {
		int retryAttempts = 0;
		while (retryAttempts < maxRetryAttempts) {
			retryAttempts++;
			try {
				return super.update(url, entry);
			} catch (ServiceException e) {
				handleException(retryAttempts, e);
			} catch (IOException e) {
				handleException(retryAttempts, e);
			} catch (RuntimeException e) {
				handleException(retryAttempts, e);
			}
		}
		update(url, entry);
		throw new RuntimeException("Failed to update entry");

	}

	@Override
	public <E extends IEntry> E updateMedia(URL mediaUrl, Class<E> entryClass, MediaSource media) throws IOException, ServiceException {
		int retryAttempts = 0;
		while (retryAttempts < maxRetryAttempts) {
			retryAttempts++;
			try {
				getRequestFactory().setHeader("If-Match", media.getEtag());
				E entry = super.updateMedia(mediaUrl, entryClass, media);
				setHeader("If-Match", null);
				return entry;
			} catch (ServiceException e) {
				handleException(retryAttempts, e);
			} catch (IOException e) {
				handleException(retryAttempts, e);
			} catch (RuntimeException e) {
				handleException(retryAttempts, e);
			}
		}
		throw new RuntimeException("Failed to update media");
	}
}
