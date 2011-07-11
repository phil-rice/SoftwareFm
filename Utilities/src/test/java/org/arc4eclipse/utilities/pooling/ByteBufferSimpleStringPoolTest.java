package org.arc4eclipse.utilities.pooling;

import org.arc4eclipse.utilities.pooling.IPool;
import org.arc4eclipse.utilities.pooling.PoolOptions;
import org.arc4eclipse.utilities.strings.ByteBufferSimpleString;
import org.arc4eclipse.utilities.strings.ISimpleStringWithSetters;

public class ByteBufferSimpleStringPoolTest extends AbstractStringPoolTest<ISimpleStringWithSetters>{

	
	protected IPool<ISimpleStringWithSetters> makePool(PoolOptions poolOptions) {
		return IPool.Utils.makeArrayStringPool(poolOptions, 20);
	}

	
	protected Class<ByteBufferSimpleString> classBeingTested() {
		return ByteBufferSimpleString.class;
	}


}
