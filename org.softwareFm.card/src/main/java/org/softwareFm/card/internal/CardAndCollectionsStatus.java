package org.softwareFm.card.internal;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.KeyValue;

public class CardAndCollectionsStatus {
	public final Future<Void> mainFuture;
	public final Future<ICard> initialFuture;
	public final List<Future<KeyValue>> keyValueFutures;
	public final AtomicInteger count;

	public CardAndCollectionsStatus(Future<Void> mainFuture, Future<ICard> future, List<Future<KeyValue>> keyValueFutures, AtomicInteger count) {
		this.mainFuture = mainFuture;
		this.initialFuture = future;
		this.keyValueFutures = keyValueFutures;
		this.count = count;
	}

}
