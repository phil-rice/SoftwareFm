package org.softwareFm.card.api;

import org.eclipse.swt.widgets.Composite;

public interface ICardFactory extends ILineFactory {

	ICard makeCard(Composite parent, ICardDataStore cardDataStore, String url);
	
	
}
