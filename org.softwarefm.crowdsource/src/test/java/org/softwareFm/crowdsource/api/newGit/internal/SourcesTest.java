package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.softwareFm.crowdsource.api.newGit.ISources;
import org.softwareFm.crowdsource.utilities.collections.Iterables;

public class SourcesTest extends TestCase {

	public void testBasicAccessors() {
		ISources sources = ISources.Utils.make("someUrl", Arrays.asList("one", "two"), "userId", "userCrypto");
		assertEquals("someUrl", sources.url());
		assertEquals("someUrl", sources.url());
		assertEquals(Arrays.asList("one", "two"), sources.sources());
		assertEquals("userCrypto", sources.userCrypto());
	}

	public void testSourcesReturnsSingleSources() {
		SingleSource one = new SingleSource("someUrl", "one", "userId", "userCrypto");
		SingleSource two = new SingleSource("someUrl", "two", "userId", "userCrypto");

		checkIterator(Arrays.<String> asList());
		checkIterator(Arrays.asList("one"), one);
		checkIterator(Arrays.asList("one", "two"), one, two);
		checkIterator(Arrays.asList("one", "one", "two"), one, one, two);
	}

	private void checkIterator(List<String> sourceNames, SingleSource... expected) {
		ISources sources = ISources.Utils.make("someUrl", sourceNames, "userId", "userCrypto");
		assertEquals(Arrays.asList(expected), Iterables.list(sources.iterator()));
		assertEquals(Arrays.asList(expected), Iterables.list(sources.iterator()));
	}

}
