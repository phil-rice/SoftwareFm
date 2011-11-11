package org.softwareFm.display.timeline;

import java.util.Map;

import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.utilities.history.History;
import org.softwareFm.utilities.maps.Maps;

public class TimeLineData extends History<PlayItem> {

	private IPlayList selected;
	private final Map<String, IPlayList> nameToPlayList = Maps.newMap();

	@Override
	public PlayItem next() {
		if (selected == null)
			throw new IllegalStateException(DisplayConstants.mustHaveSelectedPlayList);
		synchronized (lock) {
			if (hasNext())
				return super.next();
			PlayItem result = selected.next();
			push(result);
			return result;
		}
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

	public void forgetPlayList(String playListName) {
		synchronized (lock) {
			nameToPlayList.remove(playListName);
		}
	}

}
