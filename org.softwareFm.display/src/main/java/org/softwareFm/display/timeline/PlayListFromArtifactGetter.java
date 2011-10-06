package org.softwareFm.display.timeline;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.future.Futures;

/** Makes playlists based on the url of a project */
public class PlayListFromArtifactGetter implements IPlayListFromEntityMapGetter {

	private final GuiDataStore guiDataStore;
	private final List<IPlayListContributor> contributors;
	private final String entityName;

	public PlayListFromArtifactGetter(GuiDataStore guiDataStore, String entityName, List<IPlayListContributor> contributors) {
		this.guiDataStore = guiDataStore;
		this.entityName = entityName;
		this.contributors = contributors;
	}

	@Override
	public Future<IPlayList> getPlayListFor(String playListName, ICallback<IPlayList> iCallback) throws Exception {
		Map<String, Object> data = guiDataStore.getAnyExistingDataFor(entityName, playListName);
		if (data == null)
			return Futures.doneFuture(null);
		List<PlayItem> items = Lists.newList();
		for (IPlayListContributor contributor: contributors)
			items.addAll(contributor.items(data));
				
		IPlayList result = IPlayList.Utils.make(playListName, Lists.shuffle(items));
				iCallback.process(result);
		return Futures.doneFuture(result);
	}

	@Override
	public String entity() {
		return entityName;
	}

}
