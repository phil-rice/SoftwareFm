/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.timeline;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

import org.softwareFm.common.collections.Lists;
import org.softwareFm.swt.constants.DisplayConstants;

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