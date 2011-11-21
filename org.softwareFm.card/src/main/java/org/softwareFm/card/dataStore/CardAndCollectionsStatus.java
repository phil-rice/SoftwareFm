package org.softwareFm.card.dataStore;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.card.card.ICard;

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
