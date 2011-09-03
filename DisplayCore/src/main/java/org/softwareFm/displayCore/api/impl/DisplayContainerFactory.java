package org.softwareFm.displayCore.api.impl;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.arc4eclipseRepository.api.IUrlGenerator;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.IDisplayContainer;
import org.softwareFm.displayCore.api.IDisplayContainerFactory;
import org.softwareFm.displayCore.api.IDisplayer;
import org.softwareFm.displayCore.api.IEditor;
import org.softwareFm.displayCore.api.ILineEditor;
import org.softwareFm.displayCore.api.IRegisteredItems;
import org.softwareFm.displayCore.api.IValidator;
import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.utilities.collections.Lists;

public class DisplayContainerFactory implements IDisplayContainerFactory, IRegisteredItems {

	private final String entity;
	private final Map<String, IDisplayer<?, ?>> registeredDisplayers;
	private final Map<String, IEditor> registeredEditors;
	private final Map<String, ILineEditor<?>> registeredLineEditors;
	private final Map<String, IValidator> registeredValidators;
	private final Map<String, IUrlGenerator> registeredUrlGenerators;
	private final List<Map<String, String>> displayDefinitions = Lists.newList();

	public DisplayContainerFactory(String entity, Map<String, IDisplayer<?, ?>> registeredDisplayers, Map<String, IEditor> registeredEditors, Map<String, ILineEditor<?>> registeredLineEditors, Map<String, IValidator> registeredValidators, Map<String, IUrlGenerator> registeredUrlGenerators) {
		this.entity = entity;
		this.registeredDisplayers = registeredDisplayers;
		this.registeredEditors = registeredEditors;
		this.registeredLineEditors = registeredLineEditors;
		this.registeredValidators = registeredValidators;
		this.registeredUrlGenerators = registeredUrlGenerators;
	}

	@Override
	public IDisplayContainer create(DisplayerContext displayerContext, Composite parent) {
		return new DisplayContainer(displayerContext, parent, SWT.NULL, entity, this, displayDefinitions);
	}

	@Override
	public IValidator getValidator(String validatorName) {
		return checkAndGet(registeredValidators, validatorName);
	}

	@Override
	public IEditor getEditor(String editorName) {
		return checkAndGet(registeredEditors, editorName);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> ILineEditor<T> getLineEditor(String lineEditorName) {
		return checkAndGet((Map) registeredLineEditors, lineEditorName);
	}

	@Override
	public IUrlGenerator getUrlGenerator(String generatorName) {
		return checkAndGet(registeredUrlGenerators, generatorName);
	}

	@Override
	public IDisplayer<?, ?> getDisplayer(String displayName) {
		return checkAndGet(registeredDisplayers, displayName);
	}

	@Override
	public void register(Map<String, String> map) {
		checkKeyPresent(map, DisplayCoreConstants.key);
		checkKeyPresent(map, DisplayCoreConstants.displayer);
		String displayerName = map.get(DisplayCoreConstants.displayer);
		if (!registeredDisplayers.containsKey(displayerName))
			throw new IllegalArgumentException(MessageFormat.format(DisplayCoreConstants.displayerNotFound, displayerName, Lists.sort(registeredDisplayers.keySet()), map));
		displayDefinitions.add(map);
	}

	private void checkKeyPresent(Map<String, String> map, String key) {
		if (!map.containsKey(key))
			throw new IllegalArgumentException(MessageFormat.format(DisplayCoreConstants.missingValueInMap, key, map));
	}

	private <V> V checkAndGet(Map<String, V> map, String name) {
		if (map.containsKey(name))
			return map.get(name);
		throw new IllegalArgumentException(MessageFormat.format(DisplayCoreConstants.illegalKey, name, entity, map.keySet()));
	}

}
