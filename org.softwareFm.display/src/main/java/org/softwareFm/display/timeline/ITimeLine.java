package org.softwareFm.display.timeline;

import java.util.concurrent.Future;

public interface ITimeLine {

	void next();

	void previous();

	Future<?> selectAndNext(String playListName);

}
