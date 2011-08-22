package org.arc4eclipse.displayCore.api.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.DisplayerDetails;
import org.arc4eclipse.displayCore.api.IDisplayContainerForTests;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.api.IRegisteredItems;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.utilities.maps.Maps;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DisplayContainer implements IDisplayContainerForTests, ITopButtonState {

	private final Composite content;
	private final Composite compButtons;
	private final Map<NameSpaceAndName, Boolean> topButtonState = Maps.newMap();
	private final IRegisteredItems registeredItems;
	private final List<Map<String, String>> displayDefinitions;
	private final List<Control> smallControls;
	private final List<Control> largeControls;

	public DisplayContainer(DisplayerContext displayerContext, Composite parent, int style, String entity, IRegisteredItems registeredItems, List<Map<String, String>> displayDefinitions) {
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
			String displayerName = map.get(DisplayCoreConstants.displayer);
			if (displayerName == null)
				throw new NullPointerException(MessageFormat.format(DisplayCoreConstants.mustHaveDisplayer, map));
			IDisplayer<?, ?> displayer = registeredItems.getDisplayer(displayerName);
			Image image = null;
			DisplayerDetails displayerDetails = new DisplayerDetails(entity, image, map);
			smallControls.add(displayer.createSmallControl(displayerContext, this, compButtons, displayerDetails));
			largeControls.add(displayer.createLargeControl(displayerContext, content, displayerDetails));
		}
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(content);
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	@Override
	public void setValues(BindingContext bindingContext) {
		int i = 0;
		for (Map<String, String> map : displayDefinitions) {
			String displayerName = map.get(DisplayCoreConstants.displayer);
			if (displayerName == null)
				throw new NullPointerException(MessageFormat.format(DisplayCoreConstants.mustHaveDisplayer, map));
			IDisplayer<Control, Control> displayer = (IDisplayer<Control, Control>) registeredItems.getDisplayer(displayerName);
			Control largeControl = largeControls.get(i);
			Control smallControl = smallControls.get(i++);
			String key = map.get(DisplayCoreConstants.key);
			Object value = bindingContext.data.get(key);
			displayer.populateLargeControl(bindingContext, largeControl, value);
			displayer.populateSmallControl(bindingContext, smallControl, value);
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

	private void sortOutOrderVisibilityAndLayout() {

	}

	@Override
	public Composite compButtons() {
		return compButtons;
	}
}
