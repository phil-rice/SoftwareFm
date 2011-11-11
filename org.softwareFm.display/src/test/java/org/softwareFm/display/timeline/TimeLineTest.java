package org.softwareFm.display.timeline;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class TimeLineTest extends PlayListTest {

	private BrowserMock browserMock;
	private PlayListGetterMock playListGetterMock;

	public void testSelectAndNext() throws InterruptedException, ExecutionException {
		TimeLine timeLine = new TimeLine(playListGetterMock);
		checkBrowser();
		assertEquals(list1, timeLine.selectAndNext("list1").get());
		checkBrowser(playItem1_1);
		timeLine.next();
		checkBrowser(playItem1_1, playItem1_2);
		timeLine.next();
		checkBrowser(playItem1_1, playItem1_2, playItem1_3);
	}

	public void testPrevious() throws InterruptedException, ExecutionException {
		TimeLine timeLine = new TimeLine(playListGetterMock);
		checkBrowser();
		assertEquals(list1, timeLine.selectAndNext("list1").get());
		checkBrowser(playItem1_1);
		timeLine.next();
		checkBrowser(playItem1_1, playItem1_2);
		timeLine.next();
		checkBrowser(playItem1_1, playItem1_2, playItem1_3);

		timeLine.previous();
		checkBrowser(playItem1_1, playItem1_2, playItem1_3, playItem1_2);
		timeLine.previous();
		checkBrowser(playItem1_1, playItem1_2, playItem1_3, playItem1_2, playItem1_1);
	}

	public void testNextPreviousRepeated() throws Exception {
		TimeLine timeLine = new TimeLine(playListGetterMock);
		assertEquals(list1, timeLine.selectAndNext("list1").get());
		timeLine.next();
		timeLine.next();

		timeLine.previous();
		timeLine.previous();
		checkBrowser(playItem1_1, playItem1_2, playItem1_3, playItem1_2, playItem1_1);

		timeLine.next();
		checkBrowser(playItem1_1, playItem1_2, playItem1_3, playItem1_2, playItem1_1, playItem1_2);

		timeLine.previous();
		checkBrowser(playItem1_1, playItem1_2, playItem1_3, playItem1_2, playItem1_1, playItem1_2, playItem1_1);
	}

	private void checkBrowser(PlayItem... playitems) {
		assertEquals(Arrays.asList(playitems), browserMock.playItems);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		browserMock = new BrowserMock();
		playListGetterMock = new PlayListGetterMock(list1, list2);
	}
}
