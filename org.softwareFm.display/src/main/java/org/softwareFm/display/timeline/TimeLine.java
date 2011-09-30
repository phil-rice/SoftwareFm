package org.softwareFm.display.timeline;

import java.util.concurrent.Future;

import org.softwareFm.display.browser.IBrowser;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.future.Futures;

public class TimeLine implements ITimeLine {

	private final TimeLineData timeLineData = new TimeLineData();
	private final IBrowser browser;
	private final IPlayListGetter playListGetter;

	public TimeLine(IBrowser browser, IPlayListGetter playListGetter) {
		this.browser = browser;
		this.playListGetter = playListGetter;

	}

	@Override
	public void next() {
		PlayItem next = timeLineData.next();
		browser.processUrl(next.feedType, next.url);
	}

	@Override
	public void previous() {
		PlayItem previous = timeLineData.previous();
		browser.processUrl(previous.feedType, previous.url);
	}

	@Override
	public Future<?> selectAndNext(final String playListName) {
		try {
			if (timeLineData.hasPlayListName(playListName)) {
				IPlayList playList = timeLineData.select(playListName);
				next();
				return Futures.doneFuture(playList);
			} else {
				return playListGetter.getPlayListFor(playListName, new ICallback<IPlayList>() {
					@Override
					public void process(IPlayList t) throws Exception {
						timeLineData.addPlayList(playListName, t);
						timeLineData.select(playListName);
						next();
					}
				});
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public boolean hasPrevious() {
		return timeLineData.hasPrevious();
	}

	public void forgetPlayList(String playListName) {
		timeLineData.forgetPlayList(playListName);
		
	}

}
