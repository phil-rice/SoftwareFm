package org.softwarefm.display.actions;

import org.softwarefm.display.composites.CompositeConfig;
import org.softwarefm.display.data.IDataGetter;
import org.softwarefm.display.editor.IEditorFactory;

public class ActionContext {
	public final IDataGetter dataGetter;
	public final CompositeConfig compositeConfig;
	public final IEditorFactory editorFactory;

	public ActionContext(IDataGetter dataGetter, CompositeConfig compositeConfig, IEditorFactory editorFactory) {
		this.dataGetter = dataGetter;
		this.compositeConfig = compositeConfig;
		this.editorFactory = editorFactory;
	}

}
