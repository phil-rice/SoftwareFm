package org.arc4eclipse.displayCore.api.impl;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.IDisplayContainer;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactory;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.api.IEditor;
import org.arc4eclipse.displayCore.api.ILineEditor;
import org.arc4eclipse.displayCore.api.IRegisteredItems;
import org.arc4eclipse.displayCore.api.IValidator;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.utilities.collections.Lists;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class DisplayContainerFactory implements IDisplayContainerFactory, IRegisteredItems {

	private final String entity;
	private final Map<String, IDisplayer<?, ?>> registeredDisplayers;
	private final Map<String, IEditor> registeredEditors;
	private final Map<String, ILineEditor> registeredLineEditors;
	private final Map<String, IValidator> registeredValidators;
	private final List<Map<String, String>> displayDefinitions = Lists.newList();

	public DisplayContainerFactory(String entity, Map<String, IDisplayer<?, ?>> registeredDisplayers, Map<String, IEditor> registeredEditors, Map<String, ILineEditor> registeredLineEditors, Map<String, IValidator> registeredValidators) {
		this.entity = entity;
		this.registeredDisplayers = registeredDisplayers;
		this.registeredEditors = registeredEditors;
		this.registeredLineEditors = registeredLineEditors;
		this.registeredValidators = registeredValidators;
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

	@Override
	public ILineEditor getLineEditor(String lineEditorName) {
		return checkAndGet(registeredLineEditors, lineEditorName);
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
