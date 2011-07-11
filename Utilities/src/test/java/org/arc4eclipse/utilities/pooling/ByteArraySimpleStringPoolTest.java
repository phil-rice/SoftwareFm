package org.arc4eclipse.utilities.pooling;

import org.arc4eclipse.utilities.pooling.IPool;
import org.arc4eclipse.utilities.pooling.PoolOptions;
import org.arc4eclipse.utilities.strings.ByteArraySimpleString;
import org.arc4eclipse.utilities.strings.ISimpleStringWithSetters;

public class ByteArraySimpleStringPoolTest extends AbstractStringPoolTest<ISimpleStringWithSetters> {

	
	protected IPool<ISimpleStringWithSetters> makePool(PoolOptions poolOptions) {
		return IPool.Utils.makeArrayStringPool(poolOptions, 20);
	}

	
	protected Class<ByteArraySimpleString> classBeingTested() {
		return ByteArraySimpleString.class;
	}

}
