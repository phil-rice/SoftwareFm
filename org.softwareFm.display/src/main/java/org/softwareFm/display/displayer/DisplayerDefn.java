package org.softwareFm.display.displayer;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.ActionDefn;
import org.softwareFm.display.IAction;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.data.DisplayConstants;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.lists.IListEditor;
import org.softwareFm.display.smallButtons.IImageButtonListener;
import org.softwareFm.display.smallButtons.SimpleImageControl;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.swtBasics.text.IButtonParent;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.strings.Strings;

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
	public List<ActionDefn> listActionDefns;

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
		final IDisplayer displayer = displayerFactory.create(parent, this, SWT.NULL, compositeConfig,actionStore, actionContext);
		createActions(actionStore, actionContext, displayer, displayer, actionDefns, -1);
		return displayer;
	}
	public void createActions(final ActionStore actionStore,final ActionContext actionContext, final IDisplayer displayer, final IButtonParent buttonParent, List<ActionDefn> actionDefns, final int index) {
		CompositeConfig compositeConfig = actionContext.compositeConfig;
		for (final ActionDefn actionDefn : actionDefns)
			actionDefn.createButton(compositeConfig.imageButtonConfig, buttonParent, new IImageButtonListener() {
				private final IAction action = actionStore.get(actionDefn.id);

				@Override
				public void buttonPressed(IHasControl button) throws Exception {
					ActionData actionData= actionContext.dataGetter.getActionDataFor(actionDefn.params);
					action.execute(actionContext, displayer, index, actionData);
				}
			});
	}

	public void data(IDataGetter dataGetter, DisplayerDefn defn, IDisplayer displayer, String entity, String url) {
		displayerFactory.data(dataGetter, this, displayer, entity, url);
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
	public DisplayerDefn listActions(ActionDefn...listActionDefns) {
		this.listActionDefns = Lists.fromArray(listActionDefns);
		return this;
	}

}
