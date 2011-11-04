package org.softwareFm.card.api;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.utilities.collections.Lists;

public class CardMock implements ICard {

	private final CardConfig cardConfig;
	private final String url;
	public final Map<String, Object> map;
	public final List<Object> keys = Lists.newList();
	public final List<Object> newValues = Lists.newList();
	private final Map<String, Object> data;
	private final Composite mockComposite;

	public CardMock(ICardHolder cardHolder, CardConfig cardConfig, String url, Map<String, Object> map) {
		mockComposite = cardHolder == null?null:new Composite(cardHolder.getComposite(), SWT.NULL);
		this.cardConfig = cardConfig;
		this.url = url;
		this.map = map;
		data = cardConfig.modify(url, map);
	}

	@Override
	public void valueChanged(String  key, Object newValue) {
		keys.add(key);
		newValues.add(newValue);
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
	public Map<String, Object> data() {
		return data;
	}

	@Override
	public String cardType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addValueChangedListener(ICardValueChangedListener listener) {
		throw new UnsupportedOperationException();
	}

}
