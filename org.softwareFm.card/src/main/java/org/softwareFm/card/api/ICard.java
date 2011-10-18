package org.softwareFm.card.api;

import java.util.List;

import org.softwareFm.display.composites.IHasComposite;

public interface ICard extends IHasComposite {

	void addLineSelectedListener(ILineSelectedListener listener);

	CardConfig cardConfig();

	String url();

	List<KeyValue> data();

}
