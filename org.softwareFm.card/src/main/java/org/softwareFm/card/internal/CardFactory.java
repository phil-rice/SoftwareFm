package org.softwareFm.card.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.card.api.ICardFactoryWithAggregateAndSort;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;

public class CardFactory implements ICardFactoryWithAggregateAndSort {

	private final String tagName;
	final Comparator<KeyValue> comparator;
	private final IFunction1<String, String> urlToTitle;
	private final ICardDataStore cardDataStore;

	public CardFactory(ICardDataStore cardDataStore, String tag) {
		this(cardDataStore, tag, Strings.lastSegmentFn("/"), new String[0]);
	}

	public CardFactory(ICardDataStore cardDataStore, String tagName, IFunction1<String, String> urlToTitle, String... order) {
		this.cardDataStore = cardDataStore;
		this.tagName = tagName;
		this.urlToTitle = urlToTitle;
		this.comparator = comparator(order);
	}

	@Override
	public Future<ICard> makeCard(final Composite parent, final int style, final boolean allowSelection, String url, final ICallback<ICard> callback) {
		try {
			Future<ICard> future = cardDataStore.processDataFor(url, new ICardDataStoreCallback<ICard>() {
				@Override
				public ICard process(final String url, final Map<String, Object> result) throws Exception {
					final AtomicReference<ICard> ref = new AtomicReference<ICard>();
					Swts.asyncExec(parent, new Runnable() {
						@Override
						public void run() {
							try {
								ICard card = makeCard(parent, style, allowSelection, url, result);
								callback.process(card);
								ref.set(card);
							} catch (Exception e) {
								throw WrappedException.wrap(e);
							}
						}
					});
					return ref.get();
				}

				@Override
				public ICard noData(String url) throws Exception {
					return process(url, Collections.<String, Object> emptyMap());
				}
			});
			return future;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public ICard makeCard(Composite parent, int style, boolean allowSelection, String url, Map<String, Object> map) {
		try {
			List<KeyValue> keyValues = aggregateAndSort(map);
			if (parent.isDisposed())
				return null;
			else {
				final Card card = new Card(parent, style, allowSelection, cardDataStore, url, keyValues);
				return card;
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public Comparator<KeyValue> comparator() {
		return comparator;
	}

	@Override
	public List<KeyValue> aggregateAndSort(Map<String, Object> raw) {
		List<KeyValue> list = aggregate(raw);
		Collections.sort(list, comparator);
		return list;
	}

	List<KeyValue> aggregate(Map<String, Object> rawMap) {
		List<KeyValue> result = Lists.newList();
		Map<String, List<Object>> aggregates = Maps.newMap(LinkedHashMap.class);
		for (Entry<String, Object> entry : rawMap.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof Map) {
				Map<String, Object> valueMap = (Map<String, Object>) value;
				String tag = (String) valueMap.get(tagName);
				if (tag == null)
					result.add(new KeyValue(key, value));
				else
					Maps.addToList(aggregates, tag, new KeyValue(key, value));
			} else
				result.add(new KeyValue(key, value));

		}
		for (Entry<String, List<Object>> entry : aggregates.entrySet())
			result.add(new KeyValue(entry.getKey(), entry.getValue()));
		return result;
	}

	private Comparator<KeyValue> comparator(String... order) {
		final List<String> list = Arrays.asList(order);
		return new Comparator<KeyValue>() {

			@Override
			public int compare(KeyValue o1, KeyValue o2) {
				String left = o1.key;
				String right = o2.key;
				int leftIndex = list.indexOf(left);
				int rightIndex = list.indexOf(right);
				if (leftIndex == -1)
					if (rightIndex == -1)
						return left.compareTo(right);
					else
						return 1;
				else if (rightIndex == -1)
					return -1;
				else
					return leftIndex - rightIndex;
			}
		};
	}

}
