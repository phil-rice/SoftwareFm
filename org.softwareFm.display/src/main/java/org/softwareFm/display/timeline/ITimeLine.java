package org.softwareFm.display.timeline;

import java.util.concurrent.Future;

import org.softwareFm.utilities.history.IHistory;

public interface ITimeLine extends IHistory<PlayItem> {

	Future<?> selectAndNext(String playListName);

}
