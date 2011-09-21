package org.softwareFm.display.lists;

import java.text.MessageFormat;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.composites.TitleAndText;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.simpleButtons.IButtonParent;
import org.softwareFm.utilities.strings.Strings;

public class ValueListEditor implements IListEditor{

	private final String lineTitleKey;

	public ValueListEditor(String lineTitleKey) {
		this.lineTitleKey = lineTitleKey;
		if (lineTitleKey==null||lineTitleKey.length()==0)
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.mustHaveA, "lineTitleKey", getClass().getSimpleName()));
	}

	@Override
	public IButtonParent makeLineHasControl(DisplayerDefn displayDefn, CompositeConfig config, Composite listComposite, int index, Object value) {
		TitleAndText result = new TitleAndText(config, listComposite, lineTitleKey, true);
		result.setText(Strings.nullSafeToString(value));
		result.setEditable(false);
		return result;
	}

}
