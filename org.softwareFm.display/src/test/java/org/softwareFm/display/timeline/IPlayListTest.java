package org.softwareFm.display.timeline;

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.softwareFm.utilities.tests.Tests;

public class IPlayListTest extends TestCase {
	PlayItem playItem1 = new PlayItem("ft1", "url1");
	PlayItem playItem2 = new PlayItem("ft2", "url2");

	public void testPlayItems() {
		checkPlayItems(playItem1, playItem2);
		checkPlayItems(IPlayList.Utils.make("title", "ft1", "url1", "ft2", "url2"), playItem1, playItem2);
	}

	public void testMustHaveSomeContent(){
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
