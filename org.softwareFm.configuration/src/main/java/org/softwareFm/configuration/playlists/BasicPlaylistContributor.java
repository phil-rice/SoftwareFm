package org.softwareFm.configuration.playlists;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.timeline.IPlayListContributor;
import org.softwareFm.display.timeline.PlayItem;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.strings.Strings;

public class BasicPlaylistContributor implements IPlayListContributor {

	@Override
	public List<PlayItem> items(Map<String, Object> dataAboutProject) {
		if (dataAboutProject == null)
			return Collections.EMPTY_LIST;
		List<PlayItem> result = Lists.newList();
		addItem(result, dataAboutProject, DisplayConstants.browserFeedType, "project.url"); //<--- won't work
		addList(result, dataAboutProject, DisplayConstants.browserFeedType, "tweets", "http://mobile.twitter.com/{0}");
		addList(result, dataAboutProject, DisplayConstants.rssFeedType, "rss", "{0}");
		return result;
	}

	private void addList(List<PlayItem> result, Map<String, Object> data, String feedtype, String key, String pattern) {
		Object value = data.get(key);
		if (value != null && value instanceof List)
			for (Object item : ((List<Object>) value)) {
				String string = Strings.nullSafeToString(item);
				if (string.trim().length() > 0)
					result.add(new PlayItem(feedtype, MessageFormat.format(pattern, string)));
			}

	}

	private void addItem(List<PlayItem> result, Map<String, Object> data, String feedtype, String key) {
		Object value = data.get(key);
		if (value != null)
			result.add(new PlayItem(feedtype, value.toString()));

	}

}
