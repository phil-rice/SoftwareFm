package org.softwareFm.card.internal;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.card.api.IDetailFactory;
import org.softwareFm.card.api.KeyValue;

public class DetailFactory implements IDetailFactory {

	@Override
	public Composite makeDetail(Composite parentComposite, ICard parentCard, CardConfig cardConfig, KeyValue keyValue, ICardSelectedListener listener) {
		throw new UnsupportedOperationException();
	}

}
