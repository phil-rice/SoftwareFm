package org.softwareFm.card.dataStore;

import java.util.List;

import org.softwareFm.utilities.collections.Lists;

public class MemoryAfterEditCallback implements IAfterEditCallback {
	public final List<String> urls = Lists.newList();

	@Override
	public void afterEdit(String url) {
		urls.add(url);
	}

}
