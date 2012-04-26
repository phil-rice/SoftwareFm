package org.softwareFm.crowdsource.api.internal;

import java.util.List;

import org.softwareFm.crowdsource.api.IFactory;
import org.softwareFm.crowdsource.utilities.collections.Lists;

public class MadeObjectForTestFactory implements IFactory<MadeObject> {

	public final List<MadeObject> list = Lists.newList();
	private final boolean transactional;

	public MadeObjectForTestFactory(boolean transactional) {
		this.transactional = transactional;
	}

	@Override
	public MadeObject build() {
		MadeObject madeObject = transactional ? new MadeObjectForTestTransactional() : new MadeObject();
		list.add(madeObject);
		return madeObject;
	}

}
