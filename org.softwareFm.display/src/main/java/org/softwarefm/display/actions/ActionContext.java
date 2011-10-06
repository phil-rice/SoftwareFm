package org.softwareFm.display.actions;

import org.softwareFm.display.IHasRightHandSide;
import org.softwareFm.display.browser.IBrowser;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.editor.IEditorFactory;
import org.softwareFm.display.editor.IUpdateStore;
import org.softwareFm.display.lists.ListEditorStore;
import org.softwareFm.utilities.callbacks.ICallback;

public class ActionContext {
	public final IHasRightHandSide rightHandSide;
	public final ActionStore actionStore;
	public final IDataGetter dataGetter;
	public final CompositeConfig compositeConfig;
	public final IEditorFactory editorFactory;
	public final IUpdateStore updateStore;
	public final ListEditorStore listEditorStore;
	public final IBrowser internalBrowser;
	public final ICallback<Throwable> exceptionHandler;

	public ActionContext(IHasRightHandSide rightHandSide, ActionStore actionStore, IDataGetter dataGetter, CompositeConfig compositeConfig, IEditorFactory editorFactory, IUpdateStore updateStore, ListEditorStore listEditorStore, IBrowser internalBrowser, ICallback<Throwable> exceptionHandler) {
		this.rightHandSide = rightHandSide;
		this.actionStore = actionStore;
		this.dataGetter = dataGetter;
		this.compositeConfig = compositeConfig;
		this.editorFactory = editorFactory;
		this.updateStore = updateStore;
		this.listEditorStore = listEditorStore;
		this.internalBrowser = internalBrowser;
		this.exceptionHandler = exceptionHandler;
	}

}
