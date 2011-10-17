package org.softwareFm.card.api;

import java.util.List;
import java.util.concurrent.Future;

import org.softwareFm.display.composites.IHasComposite;

public interface ICard extends IHasComposite {

	void addLineSelectedListener(ILineSelectedListener listener);

	Future<?> getPopulateFuture();

	String url();

	List<KeyValue> data();

}
