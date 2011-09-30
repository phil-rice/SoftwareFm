package org.softwareFm.display.timeline;

import java.util.List;
import java.util.Map;

import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;

public class TimeLineData {

	private IPlayList selected;
	private final List<PlayItem> history = Lists.newList();
	private int index;
	private final Object lock = new Object();
	private final Map<String, IPlayList> nameToPlayList = Maps.newMap();
	private boolean lastWasNext = true;

	public PlayItem next() {
		if (selected == null)
			throw new IllegalStateException(DisplayConstants.mustHaveSelectedPlayList);
		synchronized (lock) {
			if (index < history.size())
				if (lastWasNext)
					return history.get(index++);
				else {
					lastWasNext = true;
					index++;
					return next();
				}
			PlayItem result = selected.next();
			history.add(result);
			index = history.size();
			lastWasNext = true;
			return result;
		}
	}

	public PlayItem previous() {
		if (!hasPrevious())
			throw new IllegalStateException(DisplayConstants.noPreviousItemInPlayList);
		synchronized (lock) {
			if (lastWasNext) {
				--index;
				lastWasNext = false;
				return previous();
			} else
				return history.get(--index);
		}
	}

	public boolean hasPrevious() {
		synchronized (lock) {
			if (lastWasNext)
				return index > 1;
			else
				return index > 0;
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
			index = history.size();
			return selected = Maps.getOrException(nameToPlayList, playListName);
		}
	}

	public int historySize() {
		return history.size();
	}

	public void forgetPlayList(String playListName) {
		synchronized (lock) {
			nameToPlayList.remove(playListName);
		}
	}

}
