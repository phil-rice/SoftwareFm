package org.softwarefm.display.displayer;

import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.utilities.strings.Strings;
import org.softwarefm.display.composites.CompositeConfig;
import org.softwarefm.display.composites.TitleAndText;
import org.softwarefm.display.data.DisplayConstants;
import org.softwarefm.display.data.IDataGetter;
import org.softwarefm.display.impl.DisplayerDefn;

public class TextDisplayer implements IDisplayerFactory {


	@Override
	public IDisplayer create(Composite parent, final DisplayerDefn defn, int style, CompositeConfig compositeConfig) {
		if (defn.title == null)
			throw new NullPointerException(MessageFormat.format(DisplayConstants.mustHaveA, "title", getClass().getSimpleName()));
		TitleAndText titleAndText = new TitleAndText(compositeConfig, parent, defn.title, true);
		return titleAndText;
	}

	@Override
	public void data(IDataGetter dataGetter, DisplayerDefn defn, IDisplayer displayer, String entity, String url, Map<String, Object> context, Map<String, Object> data) {
		Object value = dataGetter.getDataFor(defn.dataKey);
		if (value != null)
			System.out.println("Setting text for " + displayer + "\n" + value);
		((TitleAndText) displayer).setText(Strings.nullSafeToString(value));
	}

}
