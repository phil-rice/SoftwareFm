package org.softwareFm.card.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.card.api.ICardFactoryWithAggregateAndSort;
import org.softwareFm.card.api.ILine;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;

public class CardFactory implements ICardFactoryWithAggregateAndSort {

	private final String tagName;
	final Comparator<KeyValue> comparator;
	private final CardConfig cardConfig;
	private final IFunction1<String, String> urlToTitle;

	public CardFactory() {
		this(new CardConfig(), null, Strings.lastSegmentFn("/"));
	}

	@Override
	public CardConfig getCardConfig() {
		return cardConfig;
	}

	public CardFactory(CardConfig cardConfig, String tagName, IFunction1<String, String> urlToTitle, String... order) {
		this.cardConfig = cardConfig;
		this.tagName = tagName;
		this.urlToTitle = urlToTitle;
		this.comparator = comparator(order);
	}

	@Override
	public ICard makeCard(Composite parent, final ICardDataStore cardDataStore, final String url) {
		try {
			String title = urlToTitle.apply(url);
			final Card card = new Card(parent, cardConfig, url, title);
			Future<?> future = cardDataStore.processDataFor(url, new ICardDataStoreCallback() {
				@Override
				public void process(String url, final Map<String, Object> result) {
					if (result == null)
						throw new NullPointerException(url);
					final List<KeyValue> sorted = aggregateAndSort(result);
					Swts.asyncExec(card, new Runnable() {
						@Override
						public void run() {
							card.populate(CardFactory.this, sorted);
						}
					});
				}

				@Override
				public void noData(String url) {
					process(url, Collections.<String, Object> emptyMap());
				}
			});
			card.setFuture(future);
			return card;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
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
				@SuppressWarnings("unchecked")
				Map<String, Object> valueMap = (Map<String, Object>) value;
				String tag = (String) valueMap.get(tagName);
				if (tag == null)
					result.add(new KeyValue(key, value));
				else
					Maps.addToList(aggregates, tag, value);
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

	@Override
	public ILine make(Composite parent, KeyValue keyValue) {
		NameValue result = new NameValue(parent, cardConfig, keyValue.key, Strings.nullSafeToString(keyValue.value));
		return result;
	}

}
