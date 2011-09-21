package org.softwareFm.display.displayer;

import java.text.MessageFormat;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.composites.TitleAndText;
import org.softwareFm.display.data.DisplayConstants;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.utilities.strings.Strings;

public class TextDisplayerFactory implements IDisplayerFactory {

	@Override
	public IDisplayer create(Composite parent, final DisplayerDefn defn, int style, CompositeConfig compositeConfig, ActionStore actionStore, ActionContext actionContext) {
		if (defn.title == null)
			throw new NullPointerException(MessageFormat.format(DisplayConstants.mustHaveA, "title", getClass().getSimpleName()));
		TitleAndText titleAndText = new TitleAndText(compositeConfig, parent, defn.title, true);
		return titleAndText;
	}

	@Override
	public void data(IDataGetter dataGetter, DisplayerDefn defn, IDisplayer displayer, String entity, String url) {
		Object value = dataGetter.getDataFor(defn.dataKey);
		((TitleAndText) displayer).setText(Strings.nullSafeToString(value));
	}

}
