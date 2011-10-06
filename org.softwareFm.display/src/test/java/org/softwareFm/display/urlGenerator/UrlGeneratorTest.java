package org.softwareFm.display.urlGenerator;

import java.util.Map;

import junit.framework.TestCase;

import org.softwareFm.utilities.maps.Maps;

public class UrlGeneratorTest extends TestCase{

private final Map<String,Object> data = Maps.makeMap("zero", "zero0", "one", "one1", "two", "two2");
	
	public void testZerothItemIsHashOfFirstValue() {
		UrlGenerator generator = new UrlGenerator("p{0}", "zero", "one", "two");
		assertEquals("p" + Math.abs("zero0".hashCode() %1000), generator.findUrlFor(data));
	}
	public void testFirstItemIsFirstThreeLettersOfFirstValue() {
		UrlGenerator generator = new UrlGenerator("p{1}", "zero");
		assertEquals("pzer", generator.findUrlFor(data));
	}

	public void testRestOfItemsAreValues(){
		UrlGenerator generator = new UrlGenerator("p{2}/{3}/{4}", "zero", "one", "two");
		assertEquals("pzero0/one1/two2", generator.findUrlFor(data));
	}
}
