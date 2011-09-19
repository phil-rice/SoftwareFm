package org.softwarefm.display.lists;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swtBasics.text.IButtonParent;
import org.softwareFm.utilities.strings.Strings;
import org.softwarefm.display.composites.CompositeConfig;
import org.softwarefm.display.composites.TitleAndText;
import org.softwarefm.display.impl.DisplayerDefn;

public class ValueListEditor implements IListEditor{

	private final String lineTitleKey;

	public ValueListEditor(String lineTitleKey) {
		this.lineTitleKey = lineTitleKey;
	}

	@Override
	public IButtonParent makeLineHasControl(DisplayerDefn displayDefn, CompositeConfig config, Composite listComposite, int index, Object value) {
		TitleAndText result = new TitleAndText(config, listComposite, lineTitleKey, true);
		result.setText(Strings.nullSafeToString(value));
		return result;
	}

}
