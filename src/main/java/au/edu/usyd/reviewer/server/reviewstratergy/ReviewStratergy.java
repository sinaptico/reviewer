package au.edu.usyd.reviewer.server.reviewstratergy;

import java.util.Map;
import java.util.Set;

import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.User;

public interface ReviewStratergy {
	public Map<DocEntry, Set<User>> allocateReviews() throws Exception;
}
