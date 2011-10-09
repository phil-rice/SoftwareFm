package org.softwareFm.display.displayer;

import java.text.MessageFormat;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.Actions;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.utilities.strings.Strings;

public class AggregateTextDisplayerFactory implements IDisplayerFactory {

	private final String patternKey;
	private final String[] keys;

	public AggregateTextDisplayerFactory(String patternKey, String... keys) {
		this.patternKey = patternKey;
		this.keys = keys;
	}

	@Override
	public IDisplayer create(Composite largeButtonComposite, DisplayerDefn defn, int style, CompositeConfig compositeConfig, ActionContext actionContext) {
		return new CompressedText(largeButtonComposite, style, compositeConfig);
	}

	@Override
	public void data(ActionContext actionContext, DisplayerDefn defn, IDisplayer displayer, String entity, String url) {
		IDataGetter dataGetter = actionContext.dataGetter;
		Object[] args = new Object[keys.length];
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			Object value = dataGetter.getDataFor(key);
			args[i] = value;
		}
		CompressedText compressedText = (CompressedText) displayer;
		String title = Strings.nullSafeToString(dataGetter.getDataFor(defn.title));
		compressedText.setTitle(title);
		String guardCondition = Actions.guardConditionPresent(dataGetter, defn);
		if (guardCondition != null)
			compressedText.setText(Strings.nullSafeToString(dataGetter.getDataFor(guardCondition)));
		else {
			String pattern = Strings.nullSafeToString(dataGetter.getDataFor(patternKey));
			String text = MessageFormat.format(pattern, args);
			compressedText.setText(text);
		}
	}

}
