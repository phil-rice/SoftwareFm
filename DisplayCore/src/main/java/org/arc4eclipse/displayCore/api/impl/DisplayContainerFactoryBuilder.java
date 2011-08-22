package org.arc4eclipse.displayCore.api.impl;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.IDisplayContainer;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactory;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactoryBuilder;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.api.IEditor;
import org.arc4eclipse.displayCore.api.ILineEditor;
import org.arc4eclipse.displayCore.api.IValidator;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.utilities.collections.Sets;
import org.arc4eclipse.utilities.constants.UtilityMessages;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.arc4eclipse.utilities.maps.Maps;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DisplayContainerFactoryBuilder implements IDisplayContainerFactoryBuilder {

	@SuppressWarnings("rawtypes")
	private final Map<String, IDisplayer<?, ?>> registeredDisplayers = Maps.newMap();
	private final Map<String, IEditor> registeredEditors = Maps.newMap();
	private final Map<String, ILineEditor> registeredLineEditors = Maps.newMap();
	private final Map<String, IValidator> registeredValidators = Maps.newMap();

	private final Set<String> entities = Sets.newSet();

	static class KeyTitleHelpAndImage {
		final String key;
		final String title;
		final String help;
		final IFunction1<Device, Image> imageMaker;

		public KeyTitleHelpAndImage(String key, String title, String help, IFunction1<Device, Image> imageMaker) {
			this.key = key;
			this.title = title;
			this.help = help;
			this.imageMaker = imageMaker;
		}

		@Override
		public String toString() {
			return "KeyTitleHelpAndImage [key=" + key + "]";
		}
	}

	private final Map<String, List<KeyTitleHelpAndImage>> entityToKeysMap = Maps.newMap();

	@Override
	public void registerForEntity(String entity, String key, String title, String help) {
		checkEntity(entity);
		registerForEntity(entity, key, title, help, null);
	}

	@Override
	public void registerForEntity(String entity, String key, String title, String help, IFunction1<Device, Image> imageMaker) {
		checkEntity(entity);
		Maps.addToList(entityToKeysMap, entity, new KeyTitleHelpAndImage(key, title, help, imageMaker));
	}

	@Override
	public <L extends Control, C extends Control> void registerDisplayer(String displayerName, IDisplayer<L, C> displayer) {
		checkAndPut(registeredDisplayers, displayerName, displayer);
	}

	@Override
	public void registerEditor(String editorName, IEditor editor) {
		checkAndPut(registeredEditors, editorName, editor);
	}

	@Override
	public void registerLineEditor(String lineEditorName, ILineEditor editor) {
		checkAndPut(registeredLineEditors, lineEditorName, editor);
	}

	@Override
	public void registerValidator(String validatorName, IValidator validator) {
		checkAndPut(registeredValidators, validatorName, validator);
	}

	private <V> void checkAndPut(Map<String, V> map, String name, V value) {
		if (map.containsKey(name))
			throw new IllegalArgumentException(MessageFormat.format(UtilityMessages.duplicateKey, name, map.get(name), value));
		map.put(name, value);
	}

	@Override
	public IDisplayContainerFactory build(String entity) {
		checkEntity(entity);
		return new DisplayContainerFactory(entity, registeredDisplayers, registeredEditors, registeredLineEditors, registeredValidators);
	}

	@Override
	public void registerEntity(String string) {
		entities.add(string);
	}

	private void checkEntity(String entity) {
		if (!entities.contains(entity))
			throw new IllegalArgumentException(MessageFormat.format(DisplayCoreConstants.illegalEntityName, entity, entities));
	}

}
