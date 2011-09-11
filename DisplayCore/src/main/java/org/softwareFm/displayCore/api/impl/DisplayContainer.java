package org.softwareFm.displayCore.api.impl;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.api.IDisplayContainerButtons;
import org.softwareFm.displayCore.api.IDisplayContainerFactoryGetter;
import org.softwareFm.displayCore.api.IDisplayContainerForTests;
import org.softwareFm.displayCore.api.IDisplayer;
import org.softwareFm.displayCore.api.IRegisteredItems;
import org.softwareFm.displayCore.api.ITopButtonState;
import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.repository.api.RepositoryDataItemStatus;
import org.softwareFm.repository.constants.RepositoryConstants;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;

public class DisplayContainer implements IDisplayContainerForTests, ITopButtonState {

	private final Composite content;
	private final Composite compButtons;
	private final Map<String, Boolean> topButtonState = Maps.newMap();
	private final IRegisteredItems registeredItems;
	private final List<Map<String, String>> displayDefinitions;
	private final List<IHasControl> smallControls;
	private final List<IHasControl> largeControls;

	public DisplayContainer(DisplayerContext displayerContext, Composite parent, IDisplayContainerButtons displayContainerButtons, int style, IRegisteredItems registeredItems, IDisplayContainerFactoryGetter displayContainerFactoryGetter, List<Map<String, String>> displayDefinitions) {
		this.registeredItems = registeredItems;
		this.displayDefinitions = displayDefinitions;
		content = new Composite(parent, style);
		this.compButtons = displayContainerButtons.getComposite();

		GridLayout contentLayout = new GridLayout();
		contentLayout.marginWidth = 0;
		content.setLayout(contentLayout);

		smallControls = Lists.newList();
		largeControls = Lists.newList();
		for (Map<String, String> map : displayDefinitions) {
			String key = map.get(DisplayCoreConstants.key);
			if (key == null)
				throw new NullPointerException(MessageFormat.format(DisplayCoreConstants.mustHaveKey, map));
			String displayerName = map.get(DisplayCoreConstants.displayer);
			String entity = map.get(RepositoryConstants.entity);
			if (entity == null)
				throw new NullPointerException(MessageFormat.format(DisplayCoreConstants.mustHaveEntity, map));
			if (displayerName == null)
				throw new NullPointerException(MessageFormat.format(DisplayCoreConstants.mustHaveDisplayer, map));
			IDisplayer<?, ?> displayer = registeredItems.getDisplayer(displayerName);
			DisplayerDetails displayerDetails = new DisplayerDetails(entity, map);

			smallControls.add(displayer.createSmallControl(displayerContext, registeredItems, this, compButtons, displayerDetails));
			largeControls.add(displayer.createLargeControl(content, displayerContext, registeredItems, displayContainerFactoryGetter, displayerDetails));
		}
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(content);
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	@Override
	public void setValues(final BindingContext bindingContext) {
		Swts.asyncExec(this, new Runnable() {
			@Override
			public void run() {
				process(new IDisplayContainerCallback() {
					@Override
					public <L extends IHasControl, S extends IHasControl> void process(int index, String key, IDisplayer<L, S> displayer, L largeControl, S smallControl) throws Exception {
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
		final List<IHasControl> visibleControls = Lists.newList();
		final List<IHasControl> invisibleControls = Lists.newList();
		if (!content.isDisposed())
			content.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					process(new IDisplayContainerCallback() {
						@Override
						public <L extends IHasControl, S extends IHasControl> void process(int index, String key, IDisplayer<L, S> displayer, L largeControl, S smallControl) throws Exception {
							boolean state = state(key);
							largeControl.getControl().setVisible(state);
							if (state)
								visibleControls.add(largeControl);
							else
								invisibleControls.add(largeControl);
						}
					});
					setAfter(invisibleControls, setAfter(visibleControls, IHasControl.Utils.toHasControl(compButtons)));
					Swts.addGrabHorizontalAndFillGridDataToAllChildren(content);
					content.layout();
					content.getParent().layout();
					content.getParent().redraw();
				}
			});
	}

	private IHasControl setAfter(List<IHasControl> controls, IHasControl firstControl) {
		for (IHasControl control : controls) {
			control.getControl().moveBelow(firstControl.getControl());
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
				IDisplayer<IHasControl, IHasControl> displayer = (IDisplayer<IHasControl, IHasControl>) registeredItems.getDisplayer(displayerName);
				IHasControl largeControl = largeControls.get(i);
				IHasControl smallControl = smallControls.get(i++);
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
		setValues(new BindingContext(status, url, item, context));
	}

	@Override
	public IRegisteredItems getRegisteredItems() {
		return registeredItems;
	}

	@Override
	public Control getControl() {
		return content;
	}
}
