/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.dataStore;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.swt.card.ICard;

public class CardAndCollectionsStatus {
	public final ITransaction<Void> mainTransaction;
	public final ITransaction<ICard> initialTransaction;
	public final List<ITransaction<Object>> keyValueTransactions;
	public final AtomicInteger count;

	public CardAndCollectionsStatus(ITransaction<Void> mainTransaction, ITransaction<ICard> initialTransaction, List<ITransaction<Object>> keyValueTransactions, AtomicInteger count) {
		this.mainTransaction = mainTransaction;
		this.initialTransaction = initialTransaction;
		this.keyValueTransactions = keyValueTransactions;
		this.count = count;
	}

}