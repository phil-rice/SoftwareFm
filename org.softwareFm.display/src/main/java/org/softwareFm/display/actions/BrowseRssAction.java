package org.softwareFm.display.actions;

import org.softwareFm.display.browser.IBrowserCallback;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.exceptions.WrappedException;

public class BrowseRssAction implements IAction {


	public BrowseRssAction() {
	}

	@Override
	public void execute(final ActionContext actionContext, DisplayerDefn displayerDefn, IDisplayer displayer, int index, ActionData actionData) {
		final String key = Actions.getDataKey(displayerDefn, actionData.formalParams);
		final String param = Actions.getString(actionContext, key, index);
		if (param != null) {
			actionContext.browserService.processUrl(DisplayConstants.rssFeedType, param, new IBrowserCallback() {
				@Override
				public void process(int statusCode, String page) {
					try {
						actionContext.internalBrowser.process(page);
					} catch (Exception e) {
						throw WrappedException.wrap(e);
					}
				}

			});
		}
	}

	
}
