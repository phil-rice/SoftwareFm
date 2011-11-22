package org.softwareFm.card.card.internal;

import junit.framework.TestCase;

import org.softwareFm.display.data.ResourceGetterMock;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

abstract public class AbstractResourceGetterFnTest extends TestCase {

	protected IFunction1<String, IResourceGetter> resourceGetterFn;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		resourceGetterFn = new IFunction1<String, IResourceGetter>() {
			@Override
			public IResourceGetter apply(String from) throws Exception {
				if ("one".equals(from))
					return new ResourceGetterMock("xa", "a{0}1", "xb", "b{1}1", "xwi_th", "x{0},{1}2");
				else if ("two".equals(from))
					return new ResourceGetterMock("xa", "a{0}2", "xb", "b{1}2");
				else
					return new ResourceGetterMock("xa", "a{0}x", "xb", "b{1}x");
			}
		};

	}

}
