package org.softwareFm.card.internal;

import java.util.List;

import org.softwareFm.card.api.ILineSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.utilities.collections.Lists;

public class LineSelectedListenerMock implements ILineSelectedListener {

	public final List<KeyValue> keyValues = Lists.newList();

	@Override
	public void selected(KeyValue keyValue) {
		keyValues.add(keyValue);
	}

}
