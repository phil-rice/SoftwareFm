package org.softwareFm.display.timeline;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.utilities.collections.Lists;

public interface IPlayList {

	PlayItem next();

	public static class Utils {
		public static IPlayList make(String title, final String... feedTypeAndUrls) {
			assert feedTypeAndUrls.length % 2 == 0;
			List<PlayItem> playItems = Lists.newList();
			for (int i = 0; i < feedTypeAndUrls.length; i += 2)
				playItems.add(new PlayItem(feedTypeAndUrls[i + 0], feedTypeAndUrls[i + 1]));
			return make(title, playItems);
		}

		public static IPlayList make(String title, final List<PlayItem> playItems) {
			if (playItems.size() == 0)
				throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.playListMustHaveSomeContent, title));
			return new IPlayList() {
				private Iterator<PlayItem> iterator = playItems.iterator();

				@Override
				public PlayItem next() {
					if (iterator.hasNext())
						return iterator.next();
					iterator = playItems.iterator();
					return iterator.next();
				}
			};

		}
	}

}
