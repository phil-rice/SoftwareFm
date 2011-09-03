package org.softwareFm.core.plugin;

import java.util.Map;

import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.utilities.maps.Maps;

public abstract class AbstractDisplayContainerFactoryConfigurer implements IDisplayContainerFactoryConfigurer {
	protected Map<String, String> makeMap(String key, String displayer) {
		return makeMap(key, displayer, "Clear");
	}

	protected Map<String, String> makeMap(String key, String displayer, String smallImageKey) {
		return Maps.<String, String> makeLinkedMap(DisplayCoreConstants.key, key, DisplayCoreConstants.displayer, displayer, DisplayCoreConstants.smallImageKey, smallImageKey);
	}

	protected Map<String, String> makeMapWithLineEditor(String key, String displayer, String smallImageKey, String lineEditor) {
		return Maps.<String, String> makeLinkedMap(DisplayCoreConstants.key, key, //
				DisplayCoreConstants.displayer, displayer, //
				DisplayCoreConstants.smallImageKey, smallImageKey,//
				DisplayCoreConstants.lineEditorKey, lineEditor);
	}
}
