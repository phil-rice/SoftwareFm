package org.softwareFm.display.timeline;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.softwareFm.utilities.history.HistoryListenerMock;

public class TimeLineTest extends PlayListTest {

	private PlayListGetterMock playListGetterMock;

	public void testSelectAndNext() throws InterruptedException, ExecutionException {
		TimeLine timeLine = new TimeLine(playListGetterMock);
		HistoryListenerMock<PlayItem> listener = new HistoryListenerMock<PlayItem>();
		timeLine.addHistoryListener(listener);
		checkListener(listener);
		assertEquals(list1, timeLine.selectAndNext("list1").get());
		checkListener(listener, playItem1_1);
		timeLine.next();
		checkListener(listener, playItem1_1, playItem1_2);
		timeLine.next();
		checkListener(listener, playItem1_1, playItem1_2, playItem1_3);
	}

	public void testPrevious() throws InterruptedException, ExecutionException {
		TimeLine timeLine = new TimeLine(playListGetterMock);
		HistoryListenerMock<PlayItem> listener = new HistoryListenerMock<PlayItem>();
		timeLine.addHistoryListener(listener);
		checkListener(listener);
		assertEquals(list1, timeLine.selectAndNext("list1").get());
		checkListener(listener, playItem1_1);
		timeLine.next();
		checkListener(listener, playItem1_1, playItem1_2);
		timeLine.next();
		checkListener(listener, playItem1_1, playItem1_2, playItem1_3);

		timeLine.previous();
		checkListener(listener, playItem1_1, playItem1_2, playItem1_3, playItem1_2);
		timeLine.previous();
		checkListener(listener, playItem1_1, playItem1_2, playItem1_3, playItem1_2, playItem1_1);
	}

	public void testNextPreviousRepeated() throws Exception {
		TimeLine timeLine = new TimeLine(playListGetterMock);
		HistoryListenerMock<PlayItem> listener = new HistoryListenerMock<PlayItem>();
		timeLine.addHistoryListener(listener);
		assertEquals(list1, timeLine.selectAndNext("list1").get());
		timeLine.next();
		timeLine.next();

		timeLine.previous();
		timeLine.previous();
		checkListener(listener, playItem1_1, playItem1_2, playItem1_3, playItem1_2, playItem1_1);

		timeLine.next();
		checkListener(listener, playItem1_1, playItem1_2, playItem1_3, playItem1_2, playItem1_1, playItem1_2);

		timeLine.previous();
		checkListener(listener, playItem1_1, playItem1_2, playItem1_3, playItem1_2, playItem1_1, playItem1_2, playItem1_1);
	}

	private void checkListener(HistoryListenerMock<PlayItem> listener, PlayItem... playitems) {
		assertEquals(Arrays.asList(playitems), listener.values);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		playListGetterMock = new PlayListGetterMock(list1, list2);
	}
	
}
