package org.arc4eclipse.core.plugin;

import java.util.Map;

import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.utilities.maps.Maps;

public abstract class AbstractDisplayContainerFactoryConfigurer implements IDisplayContainerFactoryConfigurer {
	protected Map<String, String> makeMap(String key, String displayer) {
		return makeMap(key, displayer, "Clear");
	}

	protected Map<String, String> makeMap(String key, String displayer, String smallImageKey) {
		return Maps.<String, String> makeLinkedMap(DisplayCoreConstants.key, key, DisplayCoreConstants.displayer, displayer, DisplayCoreConstants.smallImageKey, smallImageKey);
	}
}
