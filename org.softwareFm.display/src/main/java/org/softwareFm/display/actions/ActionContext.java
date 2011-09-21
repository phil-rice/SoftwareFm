package org.softwareFm.display.actions;

import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.editor.IEditorFactory;

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
