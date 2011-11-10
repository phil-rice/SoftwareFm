package org.softwareFm.configuration.configurators;

import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.actions.BrowseAction;
import org.softwareFm.display.actions.BrowseRssAction;
import org.softwareFm.display.actions.IActionStoreConfigurator;
import org.softwareFm.display.actions.NoOperationAction;
import org.softwareFm.display.actions.SearchForAction;
import org.softwareFm.display.actions.ViewTweetsAction;

public class ActionStoreConfigurator implements IActionStoreConfigurator {

	@Override
	public void process(ActionStore actionStore) throws Exception {
		actionStore.//
				action("action.no.operation", new NoOperationAction()).//
				action("action.url.search", new SearchForAction()).//
				action("action.rss.browse", new BrowseRssAction()).//
				action("action.text.browse", new BrowseAction()).//

				action("action.list.viewTweets", new ViewTweetsAction());
	}
}
