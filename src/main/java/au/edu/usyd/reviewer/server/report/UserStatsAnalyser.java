package au.edu.usyd.reviewer.server.report;

import java.io.IOException;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.admin.report.UserStats;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.gdata.GoogleDocsServiceImpl;

import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.RevisionEntry;
import com.google.gdata.util.ServiceException;

public class UserStatsAnalyser {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private GoogleDocsServiceImpl googleDocsServiceImpl;

	public UserStatsAnalyser(GoogleDocsServiceImpl googleDocsServiceImpl) {
		this.googleDocsServiceImpl = googleDocsServiceImpl;
	}

	public Collection<UserStats> calculateStats(WritingActivity writingActivity, Collection<User> users) throws IOException, ServiceException {
		Map<String, UserStats> userStats = new HashMap<String, UserStats>();
		for(User user : users) {
			UserStats stats = new UserStats();
			stats = new UserStats();
			stats.setUserId(user.getUsername());
			userStats.put(user.getUsername(), stats);
		}

		for (DocumentListEntry entry : googleDocsServiceImpl.getFolderDocuments(writingActivity.getFolderId())) {
			try {
				int groupRevisions = 0;
				Map<String, Date> previousRevision = new HashMap<String, Date>();
				Map<String, Date> previousDay = new HashMap<String, Date>();
				List<RevisionEntry> revisions = googleDocsServiceImpl.getDocumentRevisions(entry);

				for (RevisionEntry revision : revisions) {
					String userId = revision.getModifyingUser().getName();
					if (!userStats.containsKey(userId)) {
						continue;
					}
					UserStats stats = userStats.get(userId);
					Date date = new Date(revision.getEdited().getValue());
					
					// revisions
					stats.setRevisions(stats.getRevisions() + 1);
					groupRevisions++;

					// start and finish date
					if (stats.getFirstRevision() == null || date.before((stats.getFirstRevision()))) {
						stats.setFirstRevision(date);
					}
					if (stats.getLastRevision() == null || date.after((stats.getLastRevision()))) {
						stats.setLastRevision(date);
					}

					// sessions writing
					if (previousRevision.containsKey(userId)) {
						if (date.getTime() - previousRevision.get(userId).getTime() > 30 * 60 * 1000) {
							stats.setSessionsWriting(stats.getSessionsWriting() + 1);
						}
					} else {
						stats.setSessionsWriting(1);
					}
					previousRevision.put(userId, date);

					// days writing
					if (previousDay.containsKey(userId)) {
						if (date.getTime() - previousDay.get(userId).getTime() > 24 * 60 * 60 * 1000) {
							stats.setDaysWriting(stats.getDaysWriting() + 1);
							previousDay.put(userId, date);
						}
					} else {
						previousDay.put(userId, date);
						stats.setDaysWriting(1);
					}
				}

				for (String userId : previousRevision.keySet()) {
					UserStats stats = userStats.get(userId);
					stats.setGroupRevisions(groupRevisions);
					stats.setGroupContributors(previousRevision.size());
				}

			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error calculating revision stats: document=" + entry.getResourceId(), e);
			}
		}
		return new HashSet<UserStats>(userStats.values());
	}
}
