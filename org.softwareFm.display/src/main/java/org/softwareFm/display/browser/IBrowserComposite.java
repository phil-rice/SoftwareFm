package org.softwareFm.display.browser;

import java.util.concurrent.Future;

import org.softwareFm.display.composites.IHasControl;

public interface IBrowserComposite extends IHasControl{

	Future<String> processUrl(String feedType, String url);

}
