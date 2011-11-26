/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.softwareFm.card.card.internal.Card;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.strings.Strings;

public class CardMock implements ICard {

	private final CardConfig cardConfig;
	private final String url;
	public final Map<String, Object> map;
	public final List<Object> keys = Lists.newList();
	public final List<Object> newValues = Lists.newList();
	private final Map<String, Object> data;
	private final Composite mockComposite;

	public CardMock(ICardHolder cardHolder, CardConfig cardConfig, String url, Map<String, Object> map) {
		mockComposite = cardHolder == null?null:new Card.CardComposite(cardHolder.getComposite(), cardConfig, url, map, null, TitleSpec.noTitleSpec(cardHolder.getControl().getBackground()));
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
		return Strings.nullSafeToString(map.get(CardConstants.slingResourceType));
	}

	@Override
	public void addValueChangedListener(ICardValueChangedListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Table getTable() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addMenuDetectListener(Listener listener) {
		throw new UnsupportedOperationException();
	}

}