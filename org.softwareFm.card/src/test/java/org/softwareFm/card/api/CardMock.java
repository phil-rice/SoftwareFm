package org.softwareFm.card.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.functions.Functions;

public class CardMock implements ICard {

	private final ICardHolder cardHolder;
	private final CardConfig cardConfig;
	private final String url;
	private final Map<String, Object> map;
	public final List<KeyValue> keyValues = Lists.newList();
	public final List<Object> newValues = Lists.newList();
	private final List<KeyValue> data;
	private final Composite mockComposite;

	public CardMock(ICardHolder cardHolder, CardConfig cardConfig, String url, Map<String, Object> map) {
		mockComposite = new Composite(cardHolder.getComposite(), SWT.NULL);
		this.cardHolder = cardHolder;
		this.cardConfig = cardConfig;
		this.url = url;
		this.map = map;
		data = Functions.call(cardConfig.aggregator, map);
		Collections.sort(data, cardConfig.comparator);
	}

	@Override
	public KeyValue valueChanged(KeyValue keyValue, Object newValue) {
		keyValues.add(keyValue);
		newValues.add(newValue);
		return keyValue;
	}

	@Override
	public Composite getComposite() {
		return mockComposite;
	}

	@Override
	public Control getControl() {
		return mockComposite;
	}

	@Override
	public void addLineSelectedListener(ILineSelectedListener listener) {
	}

	@Override
	public CardConfig cardConfig() {
		return cardConfig;
	}

	@Override
	public String url() {
		return url;
	}

	@Override
	public Map<String, Object> rawData() {
		return map;
	}

	@Override
	public List<KeyValue> data() {
		return data;
	}

}
