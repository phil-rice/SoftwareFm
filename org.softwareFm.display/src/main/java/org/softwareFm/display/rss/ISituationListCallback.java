package org.softwareFm.display.rss;

import org.softwareFm.display.composites.IHasControl;


public interface ISituationListCallback<T extends IHasControl>  {

	void selected(T hasControl, String value) throws Exception;
	
}
