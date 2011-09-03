package org.softwareFm.utilities.pooling;

import org.softwareFm.utilities.strings.ByteBufferSimpleString;
import org.softwareFm.utilities.strings.ISimpleStringWithSetters;

public class ByteBufferSimpleStringPoolTest extends AbstractStringPoolTest<ISimpleStringWithSetters>{

	
	protected IPool<ISimpleStringWithSetters> makePool(PoolOptions poolOptions) {
		return IPool.Utils.makeArrayStringPool(poolOptions, 20);
	}

	
	protected Class<ByteBufferSimpleString> classBeingTested() {
		return ByteBufferSimpleString.class;
	}


}
