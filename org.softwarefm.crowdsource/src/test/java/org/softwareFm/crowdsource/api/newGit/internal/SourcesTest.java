package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.ISources;
import org.softwareFm.crowdsource.api.newGit.SourceType;
import org.softwareFm.crowdsource.utilities.collections.Iterables;

public class SourcesTest extends TestCase {

	public void testBasicAccessors() {
		List<String> sourceStrings = Arrays.asList(SourceType.global, SourceType.me, SourceType.global);
		ISources sources = ISources.Utils.make("someUrl", sourceStrings, "userId", "userCrypto");
		assertEquals("someUrl", sources.url());
		assertEquals("someUrl", sources.url());
		assertEquals(sourceStrings, sources.sources());
		assertEquals("userCrypto", sources.userCrypto());
	}

	public void testSourcesReturnsSingleSources() {
		RawSingleSource global = new RawSingleSource("someUrl");
		RawSingleSource my = new RawSingleSource("someUrl");
		RawSingleSource group = new RawSingleSource("someUrl");

		checkIterator(Arrays.<String> asList());
		checkIterator(Arrays.asList("one"), one);
		checkIterator(Arrays.asList("one", "two"), one, two);
		checkIterator(Arrays.asList("one", "one", "two"), one, one, two);
	}

	private void checkIterator(List<String> sourceNames, ISingleSource... expected) {
		ISources sources = ISources.Utils.make("someUrl", sourceNames, "userId", "userCrypto");
		assertEquals(Arrays.asList(expected), Iterables.list(sources.iterator()));
		assertEquals(Arrays.asList(expected), Iterables.list(sources.iterator()));
	}

}
