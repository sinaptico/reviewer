package au.edu.usyd.reviewer.client.core.util;

import java.util.Comparator;

import au.edu.usyd.reviewer.client.core.Entry;

/**
 * <p>Class used to compare 2 Doc entries by their titles.</p>
 */
public class EntryTitleComparator implements Comparator<Entry> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Entry e1, Entry e2) {
		return e1.getTitle().compareTo(e2.getTitle());
	}	
}
