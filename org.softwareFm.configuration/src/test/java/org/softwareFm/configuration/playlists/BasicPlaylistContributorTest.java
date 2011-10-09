package org.softwareFm.configuration.playlists;

import java.util.Arrays;
import java.util.Collections;

import junit.framework.TestCase;

import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.timeline.PlayItem;
import org.softwareFm.utilities.maps.Maps;

public class BasicPlaylistContributorTest extends TestCase {
	private final BasicPlaylistContributor contributor = new BasicPlaylistContributor();

	public void testOnNullData() {
		assertEquals(Collections.emptyList(), contributor.items(null));
	}

	public void testDoesntAddJunk() {
		assertEquals(Collections.emptyList(), contributor.items(Maps.<String, Object> makeMap("a", 1, "b", 2)));
	}

//	public void testAddsProjectUrl() {
//		List<PlayItem> actual = contributor.items(Maps.<String, Object> makeMap("project.url", "purl"));
//		assertEquals(Arrays.asList(new PlayItem(DisplayConstants.browserFeedType, "purl")), actual);
//	}

	public void testAddsRssFeeds() {
		assertEquals(Arrays.asList(new PlayItem(DisplayConstants.rssFeedType, "rss1"),//
				new PlayItem(DisplayConstants.rssFeedType, "rss2"),//
				new PlayItem(DisplayConstants.rssFeedType, "rss3")//
				), contributor.items(Maps.<String, Object> makeMap("rss", Arrays.asList("rss1", "rss2", "rss3"))));
	}

//	public void testAddsTweetFeeds() {
//		assertEquals(Arrays.asList(new PlayItem(DisplayConstants.browserFeedType, "http://mobile.twitter.com/tweet1"),//
//				new PlayItem(DisplayConstants.browserFeedType, "http://mobile.twitter.com/tweet2"),//
//				new PlayItem(DisplayConstants.browserFeedType, "http://mobile.twitter.com/tweet3")//
//				), contributor.items(Maps.<String, Object> makeMap("tweet", Arrays.asList("tweet1", "tweet2", "tweet3"))));
//	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

}
