package org.softwareFm.display.urlGenerator;

import java.util.Map;

import junit.framework.TestCase;

import org.softwareFm.utilities.maps.Maps;

public class UrlGeneratorTest extends TestCase{

private final Map<String,Object> data = Maps.makeMap("zero", "zero0", "one", "one1", "two", "two2");
private final Map<String,Object> dataWithDots = Maps.makeMap("zero", "zero0.p0", "one", "one1.p1.q1", "two", "two2.p2.q2");
	
	public void testItem0IsFirstTwoLettersOfFirstValue() {
		UrlGenerator generator = new UrlGenerator("p{0}", "zero");
		assertEquals("pze", generator.findUrlFor(data));
		assertEquals("pze", generator.findUrlFor(dataWithDots));
	}
	public void testItem1IsNextTwoLettersOfFirstValue() {
		UrlGenerator generator = new UrlGenerator("p{0}/{1}", "zero");
		assertEquals("pze/ro", generator.findUrlFor(data));
		assertEquals("pze/ro", generator.findUrlFor(dataWithDots));
	}

	public void testEvenItemsAreValues(){
		UrlGenerator generator = new UrlGenerator("p{2}/{4}/{6}", "zero", "one", "two");
		assertEquals("pzero0/one1/two2", generator.findUrlFor(data));
		assertEquals("pzero0.p0/one1.p1.q1/two2.p2.q2", generator.findUrlFor(dataWithDots));
	}
	
	public void testOddItemsTurnsDotsIntoSlashes(){
		UrlGenerator generator = new UrlGenerator("p{3}/{5}/{7}", "zero", "one", "two");
		assertEquals("pzero0/one1/two2", generator.findUrlFor(data));
		assertEquals("pzero0/p0/one1/p1/q1/two2/p2/q2", generator.findUrlFor(dataWithDots));
	}
}
