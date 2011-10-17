package org.softwareFm.card.internal;

import java.util.List;

import org.softwareFm.card.api.ILine;
import org.softwareFm.card.api.ILineSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.utilities.collections.Lists;

public class LineSelectedListenerMock implements ILineSelectedListener {

	public final List<String> names = Lists.newList();
	public final List<KeyValue> keyValues = Lists.newList();

	@Override
	public void selected(KeyValue keyValue, ILine line) {
		NameValue nameValue = (NameValue) line;
		names.add(nameValue.content.name);
		keyValues.add(keyValue);
	}

}
