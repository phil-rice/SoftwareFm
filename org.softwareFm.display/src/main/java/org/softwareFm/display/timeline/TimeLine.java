package org.softwareFm.display.timeline;

import java.util.concurrent.Future;

import org.softwareFm.display.browser.IBrowser;
import org.softwareFm.display.constants.DisplayConstants;
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
		timeLineData.addPlayList(DisplayConstants.defaultPlayListName, IPlayList.Utils.make(DisplayConstants.defaultPlayListName, DisplayConstants.defaultPlayList));
		timeLineData.select(DisplayConstants.defaultPlayListName);
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
				try {
					return playListGetter.getPlayListFor(playListName, new ICallback<IPlayList>() {
						@Override
						public void process(IPlayList t) throws Exception {
							if (t == null) {
								selectAndNext(DisplayConstants.defaultPlayListName);
							}
							timeLineData.addPlayList(playListName, t);
							timeLineData.select(playListName);
							next();
						}
					});
				} catch (Exception e) {
					return selectAndNext(DisplayConstants.defaultPlayListName);
				}
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
