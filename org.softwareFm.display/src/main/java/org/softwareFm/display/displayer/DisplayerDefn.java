package org.softwareFm.display.displayer;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.ActionDefn;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.actions.Actions;
import org.softwareFm.display.actions.IAction;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.simpleButtons.IButtonParent;
import org.softwareFm.display.smallButtons.IImageButtonListener;
import org.softwareFm.display.smallButtons.SimpleImageControl;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.strings.NameAndValue;
import org.softwareFm.utilities.strings.Strings;

public class DisplayerDefn {
	private final static String noActionKey = "action.no.operation";

	@Override
	public String toString() {
		return "DisplayerDefn [title=" + title + ", dataKey=" + dataKey + ", defaultAction=" + defaultAction + ", tooltip=" + tooltip + ", editorId=" + editorId + ", listEditorId=" + listEditorId + ", listActionDefns=" + listActionDefns + ", guardKeys=" + guardKeys + ", iconImageId=" + iconImageId + ", iconOverlayId=" + iconOverlayId + ", actionDefns=" + actionDefns + "]";
	}

	public final IDisplayerFactory displayerFactory;
	public List<ActionDefn> actionDefns = Lists.newList();
	public ActionDefn defaultAction;
	public String dataKey;
	public String title;
	public String tooltip;

	public String editorId;
	public String listEditorId;
	public List<ActionDefn> listActionDefns;
	public List<NameAndValue> guardKeys;
	public String iconImageId;
	public String iconOverlayId;
	public boolean noIcon;
	public String helpKey;
	public String editorIgnoresGuardKey;

	public DisplayerDefn(IDisplayerFactory displayer) {
		this.displayerFactory = displayer;
	}

	public DisplayerDefn(IDisplayerFactory displayer, String listEditorId) {
		this.displayerFactory = displayer;
		this.listEditorId = listEditorId;
	}

	public DisplayerDefn title(String title) {
		if (this.title != null)
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "title", this.title, title));
		this.title = title;
		return this;

	}

	public DisplayerDefn editor(String editorId) {
		if (this.editorId != null)
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.cannotSetEditorTwice, dataKey, this.editorId, editorId));
		this.editorId = editorId;
		return this;
	}

	public DisplayerDefn guard(String... guardKeys) {
		if (this.guardKeys != null)
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "guard", this.guardKeys, Arrays.asList(guardKeys)));
		if (guardKeys.length % 2 != 0)
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.guardMustHaveEvenParameters, dataKey, guardKeys.length, Arrays.asList(guardKeys)));
		this.guardKeys = Lists.newList();
		for (int i = 0; i < guardKeys.length; i += 2)
			this.guardKeys.add(new NameAndValue(guardKeys[i + 0], guardKeys[i + 1]));
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
		if (!this.actionDefns.isEmpty())
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.cannotDefineActionsTwice, this.dataKey, this.actionDefns, Arrays.asList(actionDefns)));
		this.actionDefns = Lists.fromArray(actionDefns);
		for (ActionDefn actionDefn : actionDefns)
			if (actionDefn.defaultAction)
				if (defaultAction != null)
					throw new IllegalStateException(MessageFormat.format(DisplayConstants.cannotHaveTwoDefaultActions, this.dataKey, defaultAction, actionDefn));
				else
					defaultAction = actionDefn;
		if (defaultAction == null)
			defaultAction = actionDefns[0];
		return this;
	}

	public IDisplayer createDisplayer(Composite parent, final ActionContext actionContext) {
		CompositeConfig compositeConfig = actionContext.compositeConfig;
		final IDisplayer displayer = displayerFactory.create(parent, this, SWT.NULL, compositeConfig, actionContext);
		createDefaultAction(actionContext, displayer, displayer, -1);
		displayer.addClickListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				System.out.println("Clicked");
				actionContext.editorFactory.displayEditor(actionContext, DisplayerDefn.this, displayer);
			}
		});
		return displayer;
	}

	public void createDefaultAction(final ActionContext actionContext, final IDisplayer displayer, final IButtonParent buttonParent, final int index) {

		if (defaultAction == null)
			if (iconImageId == null)
				if (noIcon)
					return;
				else
					throw new IllegalStateException(MessageFormat.format(DisplayConstants.mustHaveA, "iconImage", this));
			else {
				IHasControl control = createButtonForAction(buttonParent, actionContext, displayer, index, new ActionDefn(noActionKey, iconImageId, iconOverlayId));
				control.getControl().addMouseListener(new MouseAdapter() {
					@Override
					public void mouseDown(MouseEvent e) {
						actionContext.editorFactory.displayEditor(actionContext, DisplayerDefn.this, displayer);
					}
				});
			}
		else
			createButtonForAction(buttonParent, actionContext, displayer, index, defaultAction);
	}

	public void createButtons(final IButtonParent buttonParent, final ActionContext actionContext, final IDisplayer displayer) {
		for (ActionDefn actionDefn : actionDefns)
			createButtonForAction(buttonParent, actionContext, displayer, -1, actionDefn);
	}

	public IHasControl createButtonForAction(final IButtonParent buttonParent, final ActionContext actionContext, final IDisplayer displayer, final int index, final ActionDefn actionDefn) {
		CompositeConfig compositeConfig = actionContext.compositeConfig;
		final ActionStore actionStore = actionContext.actionStore;
		return actionDefn.createButton(compositeConfig.imageButtonConfig, buttonParent, new IImageButtonListener() {
			private final IAction action = actionStore.get(actionDefn.id);
			{
				if (action == null)
					throw new NullPointerException(MessageFormat.format(DisplayConstants.cannotFindValueForKey, actionDefn.id, actionStore));
			}

			@Override
			public void buttonPressed(IHasControl button) throws Exception {
				ActionData actionData = actionContext.dataGetter.getActionDataFor(actionDefn.params);
				String actionDataKey = Actions.getDataKey(DisplayerDefn.this, actionData.formalParams);
				if (actionDefn.ignoreGuard || Actions.guardConditionPresent(actionContext.dataGetter, DisplayerDefn.this, actionDataKey) == null)
					action.execute(actionContext, DisplayerDefn.this, displayer, index, actionData);
			}
		});
	}

	public void data(ActionContext actionContext, DisplayerDefn defn, IDisplayer displayer, String entity, String url) {
		IDataGetter dataGetter = actionContext.dataGetter;
		displayerFactory.data(actionContext, this, displayer, entity, url);
		String tooltip = defn.tooltip == null ? "" : Strings.nullSafeToString(dataGetter.getDataFor(defn.tooltip));
		displayer.getControl().setToolTipText(tooltip);
		Control[] children = displayer.getButtonComposite().getChildren();
		sendDataToActionButton(dataGetter, children, defn.defaultAction, 0);
	}

	private void sendDataToActionButton(IDataGetter dataGetter, Control[] children, ActionDefn actionDefn, int i) {
		if (actionDefn != null) {
			SimpleImageControl control = (SimpleImageControl) children[i++];
			String actionDataKey = Actions.getDataKey(this, actionDefn.params);
			boolean canUseAction = actionDefn.ignoreGuard || Actions.guardConditionPresent(dataGetter, this, actionDataKey) == null;
			control.setEnabled(canUseAction);
			if (actionDefn.tooltip != null) {
				Object tooltipValue = dataGetter.getDataFor(actionDefn.tooltip);
				control.setToolTipText(Strings.nullSafeToString(tooltipValue));
			}
		}
	}

	public DisplayerDefn listActions(ActionDefn... listActionDefns) {
		this.listActionDefns = Lists.fromArray(listActionDefns);
		return this;
	}

	public ActionDefn getDefaultActionDefn() {
		return defaultAction;
	}

	public DisplayerDefn noIcon() {
		noIcon = true;
		return this;
	}

	public DisplayerDefn icon(String iconId) {
		if (iconImageId != null)
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "icon", this.iconImageId, iconId));
		iconImageId = iconId;
		return this;
	}

	public DisplayerDefn icon(String iconId, String overlayId) {
		if (iconImageId != null)
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "icon", this.iconImageId, iconId));
		iconImageId = iconId;
		iconOverlayId = overlayId;
		return this;
	}

	public DisplayerDefn help(String helpKey) {
		this.helpKey = helpKey;
		return this;
	}

	public DisplayerDefn editorIgnoreGuard(String editorIgnoresGuardKey) {
		if (this.editorIgnoresGuardKey != null)
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "editorIgnoreGuard", this.editorIgnoresGuardKey, editorIgnoresGuardKey));
		this.editorIgnoresGuardKey = editorIgnoresGuardKey;
		return this;
	}

}
