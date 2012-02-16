/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.timeline;

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.softwareFm.common.tests.Tests;

public class IPlayListTest extends TestCase {
	PlayItem playItem1 = new PlayItem("ft1", "url1");
	PlayItem playItem2 = new PlayItem("ft2", "url2");

	public void testPlayItems() {
		checkPlayItems(playItem1, playItem2);
		checkPlayItems(IPlayList.Utils.make("title", "ft1", "url1", "ft2", "url2"), playItem1, playItem2);
	}

	public void testMustHaveSomeContent() {
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				IPlayList.Utils.make("someTitle", new ArrayList<PlayItem>());
			}
		});
		assertEquals("Play list someTitle must have some content", e.getMessage());
	}

	private void checkPlayItems(PlayItem... items) {
		IPlayList playList = IPlayList.Utils.make("title", Arrays.asList(items));
		checkPlayItems(playList, items);
	}

	private void checkPlayItems(IPlayList playList, PlayItem... expected) {
		for (int i = 0; i < 3; i++)
			for (PlayItem item : expected)
				assertEquals(item, playList.next());
	}

}