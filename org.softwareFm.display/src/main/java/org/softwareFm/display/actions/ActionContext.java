package org.softwareFm.display.actions;

import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.editor.IEditorFactory;
import org.softwareFm.display.editor.IUpdateStore;
import org.softwareFm.display.lists.ListEditorStore;

public class ActionContext {
	public final IDataGetter dataGetter;
	public final CompositeConfig compositeConfig;
	public final IEditorFactory editorFactory;
	public final IUpdateStore updateStore;
	public final ListEditorStore listEditorStore;
	public ActionContext(IDataGetter dataGetter, CompositeConfig compositeConfig, IEditorFactory editorFactory, IUpdateStore updateStore, ListEditorStore listEditorStore) {
		this.dataGetter = dataGetter;
		this.compositeConfig = compositeConfig;
		this.editorFactory = editorFactory;
		this.updateStore = updateStore;
		this.listEditorStore = listEditorStore;
	}



}
