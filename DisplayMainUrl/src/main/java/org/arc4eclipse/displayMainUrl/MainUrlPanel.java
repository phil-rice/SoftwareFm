package org.arc4eclipse.displayMainUrl;

import org.arc4eclipse.swtBasics.text.ConfigForTitleAnd;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.eclipse.swt.widgets.Composite;

public class MainUrlPanel extends TitleAndTextField {

	public final String key;

	public MainUrlPanel(ConfigForTitleAnd config, Composite parent, String key) {
		super(config, parent, key);
		this.key = key;
	}

}
