package org.softwareFm.display.timeline;

import java.util.concurrent.Future;

import org.softwareFm.utilities.callbacks.ICallback;

public interface IPlayListGetter {

	Future<IPlayList> getPlayListFor(String playListName,  ICallback<IPlayList> iCallback) ;

}
