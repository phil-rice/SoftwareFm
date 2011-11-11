package org.softwareFm.display.timeline;

import org.softwareFm.utilities.tests.Tests;

public class TimeLineDataTest extends PlayListTest {

	public void testConstructor() {
		final TimeLineData timeLineData = new TimeLineData();
		assertFalse(timeLineData.hasPrevious());
		IllegalStateException e = Tests.assertThrows(IllegalStateException.class, new Runnable() {
			@Override
			public void run() {
				timeLineData.next();
			}
		});
		assertEquals("Time line must have a selected play list", e.getMessage());
		checkCannotPrevious(timeLineData);
	}

	public void testSimpleNExtPrevious() {
		final TimeLineData timeLineData = makePlayListWithTwoLists();
		timeLineData.select("list1");
		assertEquals(playItem1_1, timeLineData.next());
		assertFalse(timeLineData.hasPrevious());
		assertEquals(playItem1_2, timeLineData.next());
		assertTrue(timeLineData.hasPrevious());
		assertEquals(playItem1_1, timeLineData.previous());

	}

	public void testNextComesFromSelectedPlayList() {
		final TimeLineData timeLineData = makePlayListWithTwoLists();
		timeLineData.select("list1");
		assertFalse(timeLineData.hasPrevious());
		checkTimeLine(timeLineData, 6, playItem1_1, playItem1_2, playItem1_3, playItem1_1, playItem1_2, playItem1_3);
		timeLineData.select("list2");
		checkTimeLine(timeLineData, 10, playItem2_1, playItem2_2, playItem2_1, playItem2_2);
		rewind(timeLineData);
		checkTimeLine(timeLineData, 10, playItem1_2, playItem1_3, playItem1_1, playItem1_2, playItem1_3, playItem2_1, playItem2_2, playItem2_1, playItem2_2);
	}

	public void testSelectMovesToEndOfHistory() {
		final TimeLineData timeLineData = makePlayListWithTwoLists();
		timeLineData.select("list1");
		checkTimeLine(timeLineData, 6, playItem1_1, playItem1_2, playItem1_3, playItem1_1, playItem1_2, playItem1_3);
		rewind(timeLineData);
		timeLineData.select("list2");
		checkTimeLine(timeLineData, 10, playItem2_1, playItem2_2, playItem2_1, playItem2_2);
	}

	private TimeLineData makePlayListWithTwoLists() {
		final TimeLineData timeLineData = new TimeLineData();
		timeLineData.addPlayList("list1", list1);
		timeLineData.addPlayList("list2", list2);
		return timeLineData;
	}

	private void rewind(TimeLineData timeLineData) {
		int i = 0;
		while (timeLineData.hasPrevious()) {
			if (i++ > 10000)
				fail();
			timeLineData.previous();
		}
	}

	private void checkTimeLine(TimeLineData timeLineData, int expectedSize, PlayItem... expected) {
		for (int j = 0; j < expected.length; j++) {
			assertEquals(expected[j], timeLineData.next());
		}
		assertEquals(expectedSize, timeLineData.size());
		assertTrue(timeLineData.hasPrevious());
		for (int j = expected.length - 2; j >= 0; j--)
			assertEquals(expected[j], timeLineData.previous());
		assertEquals(expectedSize, timeLineData.size());
		for (int j = 1; j < expected.length; j++) {
			assertEquals(expected[j], timeLineData.next());
		}
		assertEquals(expectedSize, timeLineData.size());
	}

	private void checkCannotPrevious(final TimeLineData timeLineData) {
		IllegalStateException e = Tests.assertThrows(IllegalStateException.class, new Runnable() {
			@Override
			public void run() {
				timeLineData.previous();
			}
		});
		assertEquals("No previous item in play list", e.getMessage());
	}

}
