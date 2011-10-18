package org.softwareFm.card.api;

import java.util.List;
import java.util.Map;

import org.softwareFm.display.composites.IHasComposite;

public interface ICard extends IHasComposite {

	void addLineSelectedListener(ILineSelectedListener listener);

	CardConfig cardConfig();

	String url();

	Map<String,Object> rawData();
	List<KeyValue> data();

}
