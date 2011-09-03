package org.softwareFm.displayMainUrl;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;
import org.softwareFm.swtBasics.text.TitleAndTextField;

public class MainUrlPanel extends TitleAndTextField {

	public final String key;

	public MainUrlPanel(ConfigForTitleAnd config, Composite parent, String key) {
		super(config, parent, key);
		this.key = key;
	}

}
