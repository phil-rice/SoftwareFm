package org.softwareFm.utilities.resources;

import junit.framework.TestCase;

import org.softwareFm.utilities.functions.IFunction1;

public class IResourceGetterTest extends TestCase {

	public void testMock() {
		IFunction1<String, IResourceGetter> fn = IResourceGetter.Utils.mock(new ResourceGetterMock("a", "da", "b", "db"), "one", new ResourceGetterMock("a", "1a"), "two", new ResourceGetterMock("a", "2a"));
		assertEquals("da", IResourceGetter.Utils.get(fn, null, "a"));
		assertEquals("1a", IResourceGetter.Utils.get(fn, "one", "a"));
		assertEquals("2a", IResourceGetter.Utils.get(fn, "two", "a"));
		assertEquals("db", IResourceGetter.Utils.get(fn, "one", "b"));
	}

}
