/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.dataStore;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.swt.card.ICard;

public class CardAndCollectionsStatus {
	public final Future<Void> mainFuture;
	public final Future<ICard> initialFuture;
	public final List<Future<Object>> keyValueFutures;
	public final AtomicInteger count;

	public CardAndCollectionsStatus(Future<Void> mainFuture, Future<ICard> future, List<Future<Object>> keyValueFutures, AtomicInteger count) {
		this.mainFuture = mainFuture;
		this.initialFuture = future;
		this.keyValueFutures = keyValueFutures;
		this.count = count;
	}

}