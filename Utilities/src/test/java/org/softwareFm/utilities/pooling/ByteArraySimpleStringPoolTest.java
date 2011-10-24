package org.softwareFm.utilities.pooling;

import org.softwareFm.utilities.strings.ByteArraySimpleString;
import org.softwareFm.utilities.strings.ISimpleStringWithSetters;

public class ByteArraySimpleStringPoolTest extends AbstractStringPoolTest<ISimpleStringWithSetters> {

	protected IPool<ISimpleStringWithSetters> makePool(PoolOptions poolOptions) {
		return IPool.Utils.makeArrayStringPool(poolOptions, 20);
	}

	protected Class<ByteArraySimpleString> classBeingTested() {
		return ByteArraySimpleString.class;
	}

}
