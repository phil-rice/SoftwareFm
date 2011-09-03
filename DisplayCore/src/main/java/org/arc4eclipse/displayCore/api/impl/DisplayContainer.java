package org.arc4eclipse.displayCore.api.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.DisplayerDetails;
import org.arc4eclipse.displayCore.api.IDisplayContainerForTests;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.api.IRegisteredItems;
import org.arc4eclipse.displayCore.api.ITopButtonState;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.utilities.collections.Lists;
import org.arc4eclipse.utilities.maps.Maps;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DisplayContainer implements IDisplayContainerForTests, ITopButtonState {

	private final Composite content;
	private final Composite compButtons;
	private final Map<String, Boolean> topButtonState = Maps.newMap();
	private final IRegisteredItems registeredItems;
	private final List<Map<String, String>> displayDefinitions;
	private final List<Control> smallControls;
	private final List<Control> largeControls;
	private final String entity;

	public DisplayContainer(DisplayerContext displayerContext, Composite parent, int style, String entity, IRegisteredItems registeredItems, List<Map<String, String>> displayDefinitions) {
		this.entity = entity;
		this.registeredItems = registeredItems;
		this.displayDefinitions = displayDefinitions;
		content = new Composite(parent, style);
		this.compButtons = new Composite(content, SWT.NULL);

		GridLayout compButtonsLayout = new GridLayout(displayDefinitions.size(), false);
		compButtonsLayout.marginWidth = 0;
		compButtons.setLayout(compButtonsLayout);

		GridLayout contentLayout = new GridLayout();
		contentLayout.marginWidth = 0;
		content.setLayout(contentLayout);

		smallControls = new ArrayList<Control>();
		largeControls = new ArrayList<Control>();
		for (Map<String, String> map : displayDefinitions) {
			String key = map.get(DisplayCoreConstants.key);
			if (key == null)
				throw new NullPointerException(MessageFormat.format(DisplayCoreConstants.mustHaveKey, map));
			String displayerName = map.get(DisplayCoreConstants.displayer);
			if (displayerName == null)
				throw new NullPointerException(MessageFormat.format(DisplayCoreConstants.mustHaveDisplayer, map));
			IDisplayer<?, ?> displayer = registeredItems.getDisplayer(displayerName);

			DisplayerDetails displayerDetails = new DisplayerDetails(entity, map);

			smallControls.add(displayer.createSmallControl(displayerContext, registeredItems, this, compButtons, displayerDetails));
			largeControls.add(displayer.createLargeControl(displayerContext, registeredItems, content, displayerDetails));
		}
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(content);
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	@Override
	public void setValues(final BindingContext bindingContext) {
		content.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				process(new IDisplayContainerCallback() {
					@Override
					public <L extends Control, S extends Control> void process(int index, String key, IDisplayer<L, S> displayer, L largeControl, S smallControl) throws Exception {
						Object value = findValue(bindingContext, key);
						displayer.populateLargeControl(bindingContext, largeControl, value);
						displayer.populateSmallControl(bindingContext, smallControl, value);
					}

					@Override
					public String toString() {
						return "DisplayContainer.setValues";
					}
				});
			}
		});
	}

	private Object findValue(BindingContext bindingContext, String key) {
		switch (bindingContext.status) {
		case FOUND:
			Object value = bindingContext.data.get(key);
			return value;
		}
		return null;
	}

	@Override
	public boolean state(String key) {
		return Maps.booleanFor(topButtonState, key, true);
	}

	@Override
	public boolean toogleState(String key) {
		boolean result = !state(key);
		topButtonState.put(key, result);
		sortOutOrderVisibilityAndLayout();
		return result;
	}

	private void sortOutOrderVisibilityAndLayout() {
		final List<Control> visibleControls = Lists.newList();
		final List<Control> invisibleControls = Lists.newList();
		content.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				process(new IDisplayContainerCallback() {
					@Override
					public <L extends Control, S extends Control> void process(int index, String key, IDisplayer<L, S> displayer, L largeControl, S smallControl) throws Exception {
						boolean state = state(key);
						largeControl.setVisible(state);
						if (state)
							visibleControls.add(largeControl);
						else
							invisibleControls.add(largeControl);
					}
				});
				setAfter(invisibleControls, setAfter(visibleControls, compButtons));
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(content);
				content.layout();
				content.getParent().layout();
				content.getParent().redraw();
			}
		});
	}

	private Control setAfter(List<Control> controls, Control firstControl) {
		for (Control control : controls) {
			control.moveBelow(firstControl);
			firstControl = control;
		}
		return firstControl;
	}

	@SuppressWarnings({ "unchecked" })
	private void process(final IDisplayContainerCallback displayContainerCallback) {
		int i = 0;
		for (Map<String, String> map : displayDefinitions) {
			String displayerName = map.get(DisplayCoreConstants.displayer);
			try {
				if (displayerName == null)
					throw new NullPointerException(MessageFormat.format(DisplayCoreConstants.mustHaveDisplayer, map));
				IDisplayer<Control, Control> displayer = (IDisplayer<Control, Control>) registeredItems.getDisplayer(displayerName);
				Control largeControl = largeControls.get(i);
				Control smallControl = smallControls.get(i++);
				String key = map.get(DisplayCoreConstants.key);
				displayContainerCallback.process(i, key, displayer, largeControl, smallControl);
			} catch (Exception e) {
				throw new RuntimeException(MessageFormat.format(DisplayCoreConstants.exceptionInProcess, i, displayerName, displayContainerCallback), e);
			}

		}
	}

	@Override
	public Composite compButtons() {
		return compButtons;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <L> L getLargeControlFor(String key) {
		int index = indexOf(key);
		if (index == -1)
			return null;
		else
			return (L) largeControls.get(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <C> C getSmallControlFor(String key) {
		int index = indexOf(key);
		if (index == -1)
			return null;
		else
			return (C) smallControls.get(index);
	}

	public int indexOf(String key) {
		int i = 0;
		for (Map<String, String> map : displayDefinitions) {
			String thisKey = map.get(DisplayCoreConstants.key);
			if (thisKey.equals(key))
				return i;
		}
		return -1;
	}

	@Override
	public void statusChanged(String url, RepositoryDataItemStatus status, Map<String, Object> item, Map<String, Object> context) throws Exception {
		Object actualEntity = context.get(RepositoryConstants.entity);
		if (entity.equals(actualEntity))
			setValues(new BindingContext(status, url, item, context));
	}

	@Override
	public IRegisteredItems getRegisteredItems() {
		return registeredItems;
	}
}
