package org.softwareFm.display.timeline;

import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.history.History;
import org.softwareFm.utilities.maps.Maps;

public class TimeLine extends History<PlayItem> implements ITimeLine {
	private IPlayList selected;
	private final Map<String, IPlayList> nameToPlayList = Maps.newMap();
	private final IPlayListGetter playListGetter;

	@Override
	public PlayItem next() {
		if (selected == null)
			throw new IllegalStateException(DisplayConstants.mustHaveSelectedPlayList);
		synchronized (lock) {
			if (hasNextInHistory())
				return super.next();
			PlayItem result = selected.next();
			push(result);
			return result;
		}
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	public TimeLine(IPlayListGetter playListGetter) {
		this.playListGetter = playListGetter;
		// timeLineData.addPlayList(DisplayConstants.defaultPlayListName, IPlayList.Utils.make(DisplayConstants.defaultPlayListName, DisplayConstants.defaultPlayList));
		// timeLineData.select(DisplayConstants.defaultPlayListName);
	}

	@Override
	public Future<?> selectAndNext(final String playListName) {
		try {
			if (hasPlayListName(playListName)) {
				IPlayList playList = select(playListName);
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
							addPlayList(playListName, t);
							select(playListName);
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

	public void forgetPlayList(String playListName) {
		nameToPlayList.remove(playListName);

	}

	public boolean hasPlayListName(String playListName) {
		synchronized (lock) {
			return nameToPlayList.containsKey(playListName);
		}
	}

	public void addPlayList(String playListName, IPlayList playList) {
		synchronized (lock) {
			nameToPlayList.put(playListName, playList);
		}
	}

	public IPlayList select(String playListName) {
		synchronized (lock) {
			return selected = Maps.getOrException(nameToPlayList, playListName);
		}
	}

}
