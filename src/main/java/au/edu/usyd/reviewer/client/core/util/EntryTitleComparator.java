package au.edu.usyd.reviewer.client.core.util;

import java.util.Comparator;

import au.edu.usyd.reviewer.client.core.Entry;

public class EntryTitleComparator implements Comparator<Entry> {

	@Override
	public int compare(Entry e1, Entry e2) {
		return e1.getTitle().compareTo(e2.getTitle());
	}	
}
