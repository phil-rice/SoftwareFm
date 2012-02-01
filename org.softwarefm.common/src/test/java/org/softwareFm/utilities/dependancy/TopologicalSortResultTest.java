/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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

	@Override
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