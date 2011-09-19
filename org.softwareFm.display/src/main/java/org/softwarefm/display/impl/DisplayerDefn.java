package org.softwarefm.display.impl;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.strings.Strings;
import org.softwarefm.display.ActionDefn;
import org.softwarefm.display.IAction;
import org.softwarefm.display.actions.ActionContext;
import org.softwarefm.display.actions.ActionStore;
import org.softwarefm.display.composites.CompositeConfig;
import org.softwarefm.display.data.DisplayConstants;
import org.softwarefm.display.data.IDataGetter;
import org.softwarefm.display.displayer.IDisplayer;
import org.softwarefm.display.displayer.IDisplayerFactory;
import org.softwarefm.display.lists.IListEditor;
import org.softwarefm.display.smallButtons.IImageButtonListener;
import org.softwarefm.display.smallButtons.SimpleImageControl;

public class DisplayerDefn {

	@Override
	public String toString() {
		return "DisplayerDefn [dataKey=" + dataKey + ", listEditor=" + this.listEditor + ", title=" + title + ", tooltip=" + tooltip + ", displayerFactory=" + displayerFactory + ", actionDefns=" + actionDefns + "]";
	}

	public final IDisplayerFactory displayerFactory;
	public List<ActionDefn> actionDefns = Lists.newList();

	public String dataKey;
	public String title;
	public String tooltip;
	public IListEditor listEditor;

	public DisplayerDefn(IDisplayerFactory displayer) {
		this.displayerFactory = displayer;
	}
	public DisplayerDefn(IDisplayerFactory displayer, IListEditor listEditor) {
		this.displayerFactory = displayer;
		this.listEditor = listEditor;
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
					List<Object> actualParams = Lists.map(Lists.nullSafe(actionDefn.params), new IFunction1<String, Object>() {
						@Override
						public Object apply(String key) throws Exception {
							return actionContext.dataGetter.getDataFor(key);
						}
					});
					action.execute(actionContext, displayer, actionDefn.params, actualParams);
				}
			});
		return displayer;
	}

	public void data(IDataGetter dataGetter, DisplayerDefn defn, IDisplayer displayer, String entity, String url, Map<String, Object> context, Map<String, Object> data) {
		displayerFactory.data(dataGetter, this, displayer, entity, url, context, data);
		String tooltip = defn.tooltip == null ? "" : Strings.nullSafeToString(dataGetter.getDataFor(defn.tooltip));
		displayer.getControl().setToolTipText(tooltip);
		int i = 0;
		Control[] children = displayer.getButtonComposite().getChildren();
		for (ActionDefn actionDefn : defn.actionDefns)
			if (actionDefn.tooltip != null) {
				SimpleImageControl control = (SimpleImageControl) children[i];
				Object tooltipValue = dataGetter.getDataFor(actionDefn.tooltip);
				control.setToolTipText(Strings.nullSafeToString(tooltipValue));
				i++;
			}
	}

}
