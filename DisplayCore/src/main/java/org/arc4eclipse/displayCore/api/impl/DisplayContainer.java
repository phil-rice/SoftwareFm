package org.arc4eclipse.displayCore.api.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.DisplayerDetails;
import org.arc4eclipse.displayCore.api.IDisplayContainer;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.utilities.collections.Lists;
import org.arc4eclipse.utilities.maps.Maps;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DisplayContainer implements IDisplayContainer, ITopButtonState {

	private final Composite content;
	private final Composite compButtons;

	@SuppressWarnings("rawtypes")
	private final Map<NameSpaceAndName, IDisplayer> toDisplayerMap;
	private final Map<NameSpaceAndName, Control> smallControlMap = Maps.newMap();
	private final Map<NameSpaceAndName, Control> largeControlMap = Maps.newMap();
	private final Map<NameSpaceAndName, Boolean> topButtonState = Maps.newMap();

	@SuppressWarnings("rawtypes")
	public DisplayContainer(final DisplayerContext displayerContext, final Composite parent, int style, final String entity, final Map<NameSpaceAndName, IDisplayer> toDisplayers, final Map<NameSpaceAndName, String> toTitle, final Map<NameSpaceAndName, String> toHelp) {
		this.toDisplayerMap = Maps.copyMap(toDisplayers);
		this.content = new Composite(parent, SWT.NULL);
		this.compButtons = new Composite(content, SWT.NULL);

		GridLayout compButtonsLayout = new GridLayout(toDisplayers.size(), false);
		compButtonsLayout.marginWidth = 0;
		compButtons.setLayout(compButtonsLayout);

		GridLayout contentLayout = new GridLayout();
		contentLayout.marginWidth = 0;
		content.setLayout(contentLayout);

		process(new IDisplayContainerCallback() {
			@Override
			public <L extends Control, S extends Control> void process(NameSpaceAndName nameSpaceAndName, IDisplayer<L, S> displayer) {
				String title = toTitle.get(nameSpaceAndName);
				String help = toHelp.get(nameSpaceAndName);
				DisplayerDetails displayerDetails = new DisplayerDetails(entity, nameSpaceAndName, title, help);
				smallControlMap.put(nameSpaceAndName, displayer.createSmallControl(displayerContext, DisplayContainer.this, compButtons, displayerDetails));
				largeControlMap.put(nameSpaceAndName, displayer.createLargeControl(displayerContext, content, displayerDetails));
			}
		});
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(content);
		// Swts.addGrabHorizontalAndFillGridDataToAllChildren(compButtons);
	}

	@Override
	public void dispose() {
		content.dispose();
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
					@SuppressWarnings("unchecked")
					@Override
					public <L extends Control, S extends Control> void process(NameSpaceAndName nameSpaceAndName, IDisplayer<L, S> displayer) {
						Map<String, Object> data = bindingContext.data;
						Object value = data == null ? null : data.get(nameSpaceAndName.key);
						S smallControl = (S) smallControlMap.get(nameSpaceAndName);
						displayer.populateSmallControl(bindingContext, smallControl, value);

						L largeControl = (L) largeControlMap.get(nameSpaceAndName);
						displayer.populateLargeControl(bindingContext, largeControl, value);
					}
				});
				sortOutOrderVisibilityAndLayout();
			}

		});

	}

	private void sortOutOrderVisibilityAndLayout() {
		final List<Control> visibleControls = Lists.newList();
		final List<Control> invisibleControls = Lists.newList();
		process(new IDisplayContainerCallback() {
			@Override
			public <L extends Control, S extends Control> void process(NameSpaceAndName nameSpaceAndName, IDisplayer<L, S> displayer) {
				boolean state = state(nameSpaceAndName);
				@SuppressWarnings("unchecked")
				L largeControl = (L) largeControlMap.get(nameSpaceAndName);
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

	private Control setAfter(List<Control> controls, Control firstControl) {
		for (Control control : controls) {
			control.moveBelow(firstControl);
			firstControl = control;
		}
		return firstControl;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void process(IDisplayContainerCallback displayContainerCallback) {
		for (Entry<NameSpaceAndName, IDisplayer> entry : toDisplayerMap.entrySet()) {
			NameSpaceAndName nameSpaceAndName = entry.getKey();
			IDisplayer displayer = entry.getValue();
			displayContainerCallback.process(nameSpaceAndName, displayer);
		}
	}

	@Override
	public boolean state(NameSpaceAndName nameSpaceAndName) {
		return Maps.booleanFor(topButtonState, nameSpaceAndName, true);
	}

	@Override
	public boolean toogleState(NameSpaceAndName nameSpaceAndName) {
		boolean result = !state(nameSpaceAndName);
		topButtonState.put(nameSpaceAndName, result);
		sortOutOrderVisibilityAndLayout();
		return result;
	}

}
