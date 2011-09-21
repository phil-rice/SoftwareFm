package org.softwareFm.configuration.configurators;

import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.actions.BrowseAction;
import org.softwareFm.display.actions.IActionStoreConfigurator;
import org.softwareFm.display.actions.ListAddAction;
import org.softwareFm.display.actions.ListDeleteAction;
import org.softwareFm.display.actions.ListEditAction;
import org.softwareFm.display.actions.TextEditAction;
import org.softwareFm.display.actions.ViewTweetsAction;

public class ActionStoreConfigurator implements IActionStoreConfigurator {

	@Override
	public void process(ActionStore actionStore) throws Exception {
		actionStore.//
				action("action.text.edit", new TextEditAction()).//
				action("action.text.browse", new BrowseAction()).//
				action("action.list.add", new ListAddAction()).//
				action("action.list.edit", new ListEditAction()).//
				action("action.list.delete", new ListDeleteAction()).//
				action("action.list.viewTweets", new ViewTweetsAction());
	}
}
