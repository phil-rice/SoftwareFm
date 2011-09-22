package org.softwareFm.configuration.configurators;

import org.softwareFm.configuration.actions.JavadocOrSourceViewAction;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.actions.IActionStoreConfigurator;

public class JavadocAndSourceActionStoreConfigurator implements IActionStoreConfigurator {

	@Override
	public void process(ActionStore actionStore) throws Exception {
		actionStore.//
				action("action.javadoc.view", new JavadocOrSourceViewAction("javadoc")).//
				action("action.source.view", new JavadocOrSourceViewAction("source"));
	}//
}
