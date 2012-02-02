/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.dependancy;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;

import junit.framework.TestCase;

import org.softwareFm.common.collections.Sets;
import org.softwareFm.common.constants.UtilityMessages;
import org.softwareFm.common.dependancy.IDependancyBuilder;
import org.softwareFm.common.dependancy.ITopologicalSortResult;
import org.softwareFm.common.exceptions.LoopException;
import org.softwareFm.common.tests.Tests;

public class DependancyBuilderTest extends TestCase {

	private IDependancyBuilder<String> dependancy;

	public void testDirectDependancies() {
		assertTrue(dependancy.dependsOn("cat", "mammel"));
		assertTrue(dependancy.dependsOn("dog", "mammel"));
		assertTrue(dependancy.dependsOn("mammel", "animal"));
		assertTrue(dependancy.dependsOn("toyCat", "cat"));
		assertTrue(dependancy.dependsOn("toyCat", "toy"));
	}

	public void testDependanciesPropogate() {
		checkDependany("cat", "animal");
		checkDependany("toyCat", "mammel");
		checkDependany("toyCat", "animal");
	}

	private void checkDependany(String child, String parent) {
		assertTrue(dependancy.dependsOn(child, parent));
		assertFalse(dependancy.dependsOn(parent, child));
	}

	public void testPath() {
		assertEquals(Arrays.asList("cat", "mammel"), dependancy.path("cat", "mammel"));
		assertEquals(Arrays.asList("cat", "mammel", "animal"), dependancy.path("cat", "animal"));
		assertEquals(Arrays.asList("toyCat", "cat", "mammel", "animal"), dependancy.path("toyCat", "animal"));
		assertEquals(Arrays.asList("toyCat", "toy"), dependancy.path("toyCat", "toy"));
		assertEquals(Collections.EMPTY_LIST, dependancy.path("cat", "toy"));
		assertEquals(Collections.EMPTY_LIST, dependancy.path("notIn", "toy"));
		assertEquals(Collections.EMPTY_LIST, dependancy.path("cat", "notIn"));
		assertEquals(Collections.EMPTY_LIST, dependancy.path("notIn", "notIn"));

	}

	public void testAvoidsLoops() {
		checkLoopException("mammel", "cat", new String[] { "cat", "mammel" });
		checkLoopException("animal", "toyCat", new String[] { "toyCat", "cat", "mammel", "animal" });
		checkLoopException("mammel", "toyCat", new String[] { "toyCat", "cat", "mammel" });
		checkLoopException("cat", "toyCat", new String[] { "toyCat", "cat" });
	}

	private void checkLoopException(final String newChild, final String newParent, String[] path) {
		LoopException e = Tests.assertThrows(LoopException.class, new Runnable() {
			@Override
			public void run() {
				dependancy.parent(newChild, newParent);
			}
		});
		assertEquals(MessageFormat.format(UtilityMessages.loopException, newChild, newParent, Arrays.asList(path)), e.getMessage());
	}

	public void testTopologicalSort() {
		ITopologicalSortResult<String> result = dependancy.sort();
		assertEquals(Sets.makeSet("animal", "toy"), Sets.from(result.get(0)));
		assertEquals(Sets.makeSet("mammel"), Sets.from(result.get(1)));
		assertEquals(Sets.makeSet("cat", "dog"), Sets.from(result.get(2)));
		assertEquals(Sets.makeSet("toyCat"), Sets.from(result.get(3)));
		assertEquals(4, result.size());
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