package org.softwarefm.display.impl;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwarefm.display.ActionDefn;
import org.softwarefm.display.IAction;
import org.softwarefm.display.actions.ActionContext;
import org.softwarefm.display.actions.ActionStore;
import org.softwarefm.display.composites.CompositeConfig;
import org.softwarefm.display.data.DisplayConstants;
import org.softwarefm.display.displayer.IDisplayer;
import org.softwarefm.display.displayer.IDisplayerFactory;
import org.softwarefm.display.smallButtons.IImageButtonListener;

public class DisplayerDefn {

	@Override
	public String toString() {
		return "DisplayerDefn [displayer=" + displayerFactory + ", defns=" + actionDefns + ", dataKey=" + dataKey + ", title=" + title + ", tooltip=" + tooltip + "]";
	}

	public final IDisplayerFactory displayerFactory;
	public List<ActionDefn> actionDefns = Lists.newList();

	public String dataKey;
	public String title;
	public String tooltip;

	public DisplayerDefn(IDisplayerFactory displayer) {
		this.displayerFactory = displayer;
	}

	public DisplayerDefn title(String title) {
		if (this.title != null)
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "title", this.title, title));
		this.title = title;
		return this;

	}

	public DisplayerDefn data(String dataKey) {
		if (this.dataKey != null)
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "data", this.dataKey, dataKey));
		this.dataKey = dataKey;
		return this;
	}

	public DisplayerDefn tooltip(String tooltip) {
		if (this.tooltip != null)
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "tooltip", this.tooltip, tooltip));
		this.tooltip = tooltip;
		return this;
	}

	// public DisplayerDefn action(String actionId, String data, String mainImage) {
	// IAction action = actionStore.get(actionId);
	// defns.add(new ImageButtonDefn(mainImage, null, data, Maps.<SmallIconPosition, String> newMap(), action));
	// return this;
	// }

	public DisplayerDefn actions(ActionDefn... actionDefns) {
		this.actionDefns = Lists.fromArray(actionDefns);
		return this;
	}

	public IDisplayer createDisplayer(Composite parent, final ActionStore actionStore, final ActionContext actionContext) {
		CompositeConfig compositeConfig = actionContext.compositeConfig;
		final IDisplayer displayer = displayerFactory.create(parent, this, SWT.NULL, compositeConfig);
		for (final ActionDefn actionDefn : actionDefns)
			actionDefn.createButton(compositeConfig.imageButtonConfig, displayer, new IImageButtonListener() {
				private final IAction action = actionStore.get(actionDefn.id);
				@Override
				public void buttonPressed(IHasControl button) throws Exception {
					List<Object> actualParams = Lists.map(actionDefn.params, new IFunction1<String	, Object>() {
						@Override
						public Object apply(String key) throws Exception {
							return actionContext.dataGetter.getDataFor(key);
						}
					});
					action.execute(displayer, actionDefn.params, actualParams);
				}
			});
		return displayer;
	}

}
