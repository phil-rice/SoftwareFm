package org.softwareFm.card.api;

import org.eclipse.swt.widgets.Composite;

public interface IDetailFactory {

	Composite makeDetail(Composite parentComposite, ICard parentCard, CardConfig cardConfig, KeyValue keyValue, ICardSelectedListener listener);
}
