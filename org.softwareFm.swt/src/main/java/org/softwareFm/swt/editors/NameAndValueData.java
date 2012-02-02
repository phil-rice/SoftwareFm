package org.softwareFm.swt.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.common.functions.IFunction1;

public class NameAndValueData {
	public final String key;
	public final IFunction1<Composite, Control> editorCreator;

	public NameAndValueData(String key, IFunction1<Composite, Control> editorCreator) {
		this.key = key;
		this.editorCreator = editorCreator;
	}

}
