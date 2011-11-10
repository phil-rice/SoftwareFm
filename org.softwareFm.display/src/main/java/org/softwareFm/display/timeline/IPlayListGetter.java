package org.softwareFm.display.timeline;

import java.util.concurrent.Future;

import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.future.Futures;

public interface IPlayListGetter {

	Future<IPlayList> getPlayListFor(String playListName, ICallback<IPlayList> callback) throws Exception;

	public static class Utils {

		public static IPlayListGetter noPlayListGetter() {
			return new IPlayListGetter() {
				
				@Override
				public Future<IPlayList> getPlayListFor(String playListName, ICallback<IPlayList> callback) throws Exception {
					return Futures.doneFuture(null);
				}
			};
		}
	}

}
