package org.softwareFm.card.internal.details;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.display.composites.IHasControl;

public interface IDetailAdder {

	IHasControl add(Composite parentComposite, ICard parentCard, CardConfig cardConfig, KeyValue keyValue, ICardSelectedListener listener, Runnable afterEdit);

}
