/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.timeline;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.softwareFm.common.history.HistoryListenerMock;

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