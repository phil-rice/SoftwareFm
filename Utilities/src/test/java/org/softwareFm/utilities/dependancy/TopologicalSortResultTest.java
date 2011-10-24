package org.softwareFm.utilities.dependancy;

import java.util.Arrays;
import java.util.Map;

import junit.framework.TestCase;

import org.softwareFm.utilities.dependancy.impl.RememberTopologicalSortVisitor;
import org.softwareFm.utilities.maps.Maps;

public class TopologicalSortResultTest extends TestCase {

	private IDependancyBuilder<String> dependancy;

	public void testWalker() {
		Map<String, Integer> expected = Maps.makeMap("animal", 0, "toy", 0, "mammel", 1, "cat", 2, "dog", 2, "toyCat", 3);
		RememberTopologicalSortVisitor<String> visitor = new RememberTopologicalSortVisitor<String>();
		ITopologicalSortResult.Utils.walk(dependancy.sort(), visitor);
		assertEquals(expected, visitor.getActual());
		assertEquals(Arrays.asList(0, 0, 1, 2, 2, 3), visitor.getGenerations());
	}

	public void testInverseWalkerWalker() {
		Map<String, Integer> expected = Maps.makeMap("animal", 0, "toy", 0, "mammel", 1, "cat", 2, "dog", 2, "toyCat", 3);
		RememberTopologicalSortVisitor<String> visitor = new RememberTopologicalSortVisitor<String>();
		ITopologicalSortResult.Utils.inverseWalk(dependancy.sort(), visitor);
		assertEquals(expected, visitor.getActual());
		assertEquals(Arrays.asList(3, 2, 2, 1, 0, 0), visitor.getGenerations());
	}

	protected void setUp() throws Exception {
		super.setUp();
		dependancy = IDependancyBuilder.Utils.<String> newBuilder().//
				parent("cat", "mammel").//
				parent("dog", "mammel").//
				parent("mammel", "animal").//
				parent("toyCat", "cat").//
				parent("toyCat", "toy");

	}
}
