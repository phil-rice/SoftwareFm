package org.softwareFm.configuration.playlists;

import java.util.List;
import java.util.Map;

import org.softwareFm.display.timeline.PlayItem;

public interface IPlayListContributor {

	List<PlayItem> items(Map<String, Object> dataAboutProject);
}
