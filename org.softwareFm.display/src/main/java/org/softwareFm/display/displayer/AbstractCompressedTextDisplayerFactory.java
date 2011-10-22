package org.softwareFm.display.displayer;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.Actions;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.utilities.strings.Strings;

public abstract class AbstractCompressedTextDisplayerFactory<T extends AbstractCompressedText<? extends Control>> implements IDisplayerFactory {

	@Override
	public IDisplayer create(Composite parent, final DisplayerDefn defn, int style, CompositeConfig compositeConfig, ActionContext actionContext) {
		if (defn.title == null)
			throw new NullPointerException(MessageFormat.format(DisplayConstants.mustHaveA, "title", getClass().getSimpleName()));
		T text = makeText(parent, SWT.NULL, compositeConfig);
		return text;
	}

	abstract protected T makeText(Composite parent, int null1, CompositeConfig compositeConfig);

	@Override
	public void data(ActionContext actionContext, DisplayerDefn defn, IDisplayer displayer, String entity, String url) {
		IDataGetter dataGetter=actionContext.dataGetter;
		String guard = Actions.guardConditionPresent(dataGetter, defn);
		if (defn.dataKey == null)
			throw new NullPointerException(MessageFormat.format(DisplayConstants.mustHaveA, "dataKey", defn));
		Object value = guard == null? dataGetter.getDataFor(defn.dataKey): dataGetter.getDataFor(guard);
		AbstractCompressedText<?> titleAndText = (AbstractCompressedText<?>) displayer;
		titleAndText.setEnabled(Actions.guardConditionPresent(dataGetter, defn, defn.editorIgnoresGuardKey)==null);
//		System.out.println("Enabled: " + titleAndText.getControl().isEnabled());
		String title = Strings.nullSafeToString(dataGetter.getDataFor(defn.title));
		titleAndText.setText (Strings.nullSafeToString(value));
		titleAndText.setTitle(title);
		String tooltip = defn.tooltip == null ? "" : Strings.nullSafeToString(dataGetter.getDataFor(defn.tooltip));
		titleAndText.setTooltip(tooltip);
	}

}
