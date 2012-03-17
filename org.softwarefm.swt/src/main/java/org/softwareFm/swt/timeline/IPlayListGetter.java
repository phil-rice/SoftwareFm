/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.timeline;

import java.util.concurrent.Future;

import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.future.Futures;

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