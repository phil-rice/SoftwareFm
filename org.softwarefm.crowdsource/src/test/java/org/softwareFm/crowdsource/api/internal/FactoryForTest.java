package org.softwareFm.crowdsource.api.internal;

import java.util.List;

import org.softwareFm.crowdsource.api.IFactory;
import org.softwareFm.crowdsource.utilities.collections.Lists;

public class FactoryForTest implements IFactory<MadeObjectForTest> {

	public final List<MadeObjectForTest> list = Lists.newList();
	private final boolean transactional;

	public FactoryForTest(boolean transactional) {
		this.transactional = transactional;
	}

	@Override
	public MadeObjectForTest build() {
		MadeObjectForTest madeObjectForTest = transactional ? new MadeObjectForTestTransactional() : new MadeObjectForTest();
		list.add(madeObjectForTest);
		return madeObjectForTest;
	}

}
