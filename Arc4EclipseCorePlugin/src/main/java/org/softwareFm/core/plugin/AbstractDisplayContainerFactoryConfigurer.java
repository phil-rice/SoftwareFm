package org.softwareFm.core.plugin;

import java.util.Map;

import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.repository.constants.RepositoryConstants;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.utilities.maps.Maps;

public abstract class AbstractDisplayContainerFactoryConfigurer implements IDisplayContainerFactoryConfigurer {
	protected Map<String, String> makeMap(String entity, String key, String displayer) {
		return makeMap(entity, key, displayer, ArtifactsAnchor.documentKey);
	}

	protected Map<String, String> makeMap(String entity, String key, String displayer, String smallImageKey) {
		return Maps.<String, String> makeLinkedMap(//
				RepositoryConstants.entity, entity, //
				DisplayCoreConstants.key, key, //
				DisplayCoreConstants.displayer, displayer, //
				DisplayCoreConstants.smallImageKey, smallImageKey);
	}

	protected Map<String, String> makeMapWithView(String entity, String key, String displayer, String smallImageKey, String viewName) {
		return Maps.<String, String> makeLinkedMap(//
				RepositoryConstants.entity, entity, //
				DisplayCoreConstants.key, key, //
				DisplayCoreConstants.displayer, displayer, //
				DisplayCoreConstants.smallImageKey, smallImageKey,//
				DisplayCoreConstants.viewKey, viewName);
	}

	protected Map<String, String> makeMapWithLineEditor(String entity, String key, String displayer, String smallImageKey, String lineEditor) {
		return Maps.<String, String> makeLinkedMap(//
				RepositoryConstants.entity, entity,//
				DisplayCoreConstants.key, key, //
				DisplayCoreConstants.displayer, displayer, //
				DisplayCoreConstants.smallImageKey, smallImageKey,//
				DisplayCoreConstants.lineEditorKey, lineEditor);
	}
}
