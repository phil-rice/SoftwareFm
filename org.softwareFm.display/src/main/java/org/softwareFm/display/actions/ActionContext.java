package org.softwareFm.display.actions;

import org.softwareFm.display.IBrowserService;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.editor.IEditorFactory;
import org.softwareFm.display.editor.IUpdateStore;
import org.softwareFm.display.lists.ListEditorStore;
import org.softwareFm.utilities.callbacks.ICallback;

public class ActionContext {
	public final IDataGetter dataGetter;
	public final CompositeConfig compositeConfig;
	public final IEditorFactory editorFactory;
	public final IUpdateStore updateStore;
	public final ListEditorStore listEditorStore;
	public final IBrowserService browserService;
	public final ICallback<String> internalBrowser;
	public ActionContext(IDataGetter dataGetter, CompositeConfig compositeConfig, IEditorFactory editorFactory, IUpdateStore updateStore, ListEditorStore listEditorStore, IBrowserService browserService, ICallback<String> internalBrowser) {
		this.dataGetter = dataGetter;
		this.compositeConfig = compositeConfig;
		this.editorFactory = editorFactory;
		this.updateStore = updateStore;
		this.listEditorStore = listEditorStore;
		this.browserService = browserService;
		this.internalBrowser = internalBrowser;
	}



}
