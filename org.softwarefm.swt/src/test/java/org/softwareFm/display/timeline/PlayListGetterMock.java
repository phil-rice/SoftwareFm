/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.display.timeline;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.future.Futures;

public class PlayListGetterMock implements IPlayListGetter {

	private List<IPlayList> playLists = Lists.newList();
	private int index;
	public final List<String> playListNames = Lists.newList();

	public PlayListGetterMock(IPlayList... playLists) {
		this.playLists = Arrays.asList(playLists);
	}

	@Override
	public Future<IPlayList> getPlayListFor(String playListName, ICallback<IPlayList> iCallback) {
		try {
			playListNames.add(playListName);
			IPlayList playList = playLists.get(index++);
			iCallback.process(playList);
			return Futures.doneFuture(playList);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

}