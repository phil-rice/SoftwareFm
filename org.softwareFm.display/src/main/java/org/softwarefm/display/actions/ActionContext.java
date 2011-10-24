package org.softwareFm.display.actions;

import org.softwareFm.display.IHasRightHandSide;
import org.softwareFm.display.browser.IBrowser;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.editor.IEditorFactory;
import org.softwareFm.display.editor.IUpdateStore;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.IFunction1;

public class ActionContext {
	public final IHasRightHandSide rightHandSide;
	public final ActionStore actionStore;
	public final IDataGetter dataGetter;
	public final CompositeConfig compositeConfig;
	public final IEditorFactory editorFactory;
	public final IUpdateStore updateStore;
	public final IBrowser internalBrowser;
	public final ICallback<Throwable> exceptionHandler;
	public final IFunction1<String, String> entityToUrlGetter;

	public ActionContext(IHasRightHandSide rightHandSide, ActionStore actionStore, IDataGetter dataGetter, IFunction1<String, String> entityToUrlGetter, CompositeConfig compositeConfig, IEditorFactory editorFactory, IUpdateStore updateStore, IBrowser internalBrowser, ICallback<Throwable> exceptionHandler) {
		this.rightHandSide = rightHandSide;
		this.actionStore = actionStore;
		this.dataGetter = dataGetter;
		this.entityToUrlGetter = entityToUrlGetter;
		this.compositeConfig = compositeConfig;
		this.editorFactory = editorFactory;
		this.updateStore = updateStore;
		this.internalBrowser = internalBrowser;
		this.exceptionHandler = exceptionHandler;
	}

}
