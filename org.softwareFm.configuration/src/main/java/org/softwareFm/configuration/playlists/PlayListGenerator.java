package org.softwareFm.configuration.playlists;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.timeline.IPlayList;
import org.softwareFm.display.timeline.IPlayListGetter;
import org.softwareFm.display.timeline.PlayItem;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.future.Futures;

/** Makes playlists based on the url of a project */
public class PlayListGenerator implements IPlayListGetter {

	private final GuiDataStore guiDataStore;
	private final List<IPlayListContributor> contributors;

	public PlayListGenerator(GuiDataStore guiDataStore, List<IPlayListContributor> contributors) {
		this.guiDataStore = guiDataStore;
		this.contributors = contributors;
	}

	@Override
	public Future<IPlayList> getPlayListFor(String playListName, ICallback<IPlayList> iCallback) {
		Map<String, Object> data = guiDataStore.getAnyExistingDataFor("project", playListName);
		if (data == null)
			return Futures.doneFuture(null);
		List<PlayItem> items = Lists.newList();
		for (IPlayListContributor contributor: contributors)
			items.addAll(contributor.items(data));
				
		return Futures.doneFuture(IPlayList.Utils.make(playListName, Lists.shuffle(items)));
	}

}
