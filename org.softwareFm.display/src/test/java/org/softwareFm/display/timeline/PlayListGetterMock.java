package org.softwareFm.display.timeline;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.future.Futures;

public class PlayListGetterMock implements IPlayListGetter {

	private List<IPlayList> playLists = Lists.newList();
	private int index;
	public final List<String> playListNames = Lists.newList();

	public PlayListGetterMock(IPlayList... playLists) {
		this.playLists = Arrays.asList(playLists);
	}

	@Override
	public Future<IPlayList> getPlayListFor(String playListName, ICallback<IPlayList> iCallback) {
		try {
			playListNames.add(playListName);
			IPlayList playList = playLists.get(index++);
			iCallback.process(playList);
			return Futures.doneFuture(playList);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

}
