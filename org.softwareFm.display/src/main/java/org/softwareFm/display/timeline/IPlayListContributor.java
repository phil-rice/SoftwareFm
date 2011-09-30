package org.softwareFm.display.timeline;

import java.util.List;
import java.util.Map;


public interface IPlayListContributor {

	List<PlayItem> items(Map<String, Object> dataAboutProject);
}
