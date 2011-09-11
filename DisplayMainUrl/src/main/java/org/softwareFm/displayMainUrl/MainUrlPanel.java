package org.softwareFm.displayMainUrl;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;
import org.softwareFm.swtBasics.text.TitleAndTextField;

public class MainUrlPanel extends TitleAndTextField {

	public final String key;
	private final String entity;

	public MainUrlPanel(ConfigForTitleAnd config, Composite parent, String entity, String key) {
		super(config, parent, key);
		this.entity = entity;
		this.key = key;
	}

	public String getEntity() {
		return entity;
	}

}
