package org.arc4eclipse.displayCore.api.impl;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.IDisplayContainer;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactory;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactoryBuilder;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.utilities.constants.UtilityMessages;
import org.arc4eclipse.utilities.maps.Maps;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DisplayContainerFactory implements IDisplayContainerFactoryBuilder, IDisplayContainerFactory {

	@SuppressWarnings("rawtypes")
	private final Map<String, IDisplayer> registeredDisplayers = Maps.newMap();

	static class KeyAndTitle {
		String key;
		String title;

		public KeyAndTitle(String key, String title) {
			this.key = key;
			this.title = title;
		}
	}

	private final Map<String, List<KeyAndTitle>> entityToKeysMap = Maps.newMap();

	@Override
	public void registerForEntity(String entity, String key, String title) {
		Maps.addToList(entityToKeysMap, entity, new KeyAndTitle(key, title));
	}

	@Override
	public <L extends Control, C extends Control> void registerDisplayer(IDisplayer<L, C> displayer) {
		String nameSpace = displayer.getNameSpace();
		if (registeredDisplayers.containsKey(nameSpace))
			throw new IllegalStateException(MessageFormat.format(UtilityMessages.duplicateKey, nameSpace, registeredDisplayers.get(nameSpace), displayer));
		registeredDisplayers.put(nameSpace, displayer);
	}

	@Override
	public IDisplayContainerFactory build() {
		return this;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IDisplayContainer create(DisplayerContext displayerContext, Composite parent, String entity) {
		List<KeyAndTitle> keysForEntity = entityToKeysMap.get(entity);
		if (keysForEntity == null)
			throw new IllegalArgumentException(MessageFormat.format(DisplayCoreConstants.illegalEntityName, entity, entityToKeysMap.keySet()));
		Map<NameSpaceAndName, IDisplayer> mapToDisplayer = Maps.newMap(LinkedHashMap.class);
		Map<NameSpaceAndName, String> mapToTitle = Maps.newMap(LinkedHashMap.class);
		for (KeyAndTitle keyAndTitle : keysForEntity) {
			String key = keyAndTitle.key;
			NameSpaceAndName nameSpaceAndName = NameSpaceAndName.Utils.rip(key);
			IDisplayer displayer = registeredDisplayers.get(nameSpaceAndName.nameSpace);
			if (displayer == null)
				throw new IllegalArgumentException(MessageFormat.format(DisplayCoreConstants.illegalKey, key, entity, keysForEntity));
			else
				mapToDisplayer.put(nameSpaceAndName, displayer);
			mapToTitle.put(nameSpaceAndName, keyAndTitle.title);
		}
		DisplayContainer displayContainer = new DisplayContainer(displayerContext, parent, SWT.NULL, entity, mapToDisplayer, mapToTitle);
		return displayContainer;
	}

}
