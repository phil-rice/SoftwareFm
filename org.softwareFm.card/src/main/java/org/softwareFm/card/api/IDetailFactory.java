package org.softwareFm.card.api;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.composites.IHasControl;

public interface IDetailFactory {

	IHasControl makeDetail(Composite parentComposite, ICard parentCard, CardConfig cardConfig, KeyValue keyValue, ICardSelectedListener listener);
}
