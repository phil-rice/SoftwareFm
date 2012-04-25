package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.ISourceType;
import org.softwareFm.crowdsource.api.newGit.ISources;
import org.softwareFm.crowdsource.utilities.collections.Iterables;

public class SourcesTest extends TestCase {

	ISourceType type12 = new SourceTypeMock("1", "2");
	ISourceType type3 = new SourceTypeMock("3");
	ISourceType type = new SourceTypeMock();

	public void testBasicAccessor() {
		assertEquals("someRl", ISources.Utils.make(Arrays.asList(type, type12, type3), "someRl", "someFile", "userId", "crypto", "cryptoKey").rl());
	}

	public void testSourcesReturnsSingleSources() {
		String rl = "someRl";
		EncryptedSingleSource one = new EncryptedSingleSource("userId/" + rl + "/1/someFile/cryptoKey", "crypto");
		EncryptedSingleSource two = new EncryptedSingleSource("userId/" + rl + "/2/someFile/cryptoKey", "crypto");
		EncryptedSingleSource three = new EncryptedSingleSource("userId/" + rl + "/3/someFile/cryptoKey", "crypto");

		checkIterator(rl, Arrays.<ISourceType> asList());
		checkIterator(rl, Arrays.asList(type12), one, two);
		checkIterator(rl, Arrays.asList(type3), three);
		checkIterator(rl, Arrays.asList(type12, type3), one, two, three);
		checkIterator(rl, Arrays.asList(type12, type, type3), one, two, three);
	}

	private void checkIterator(String rl, List<ISourceType> sourceTypes, ISingleSource... expected) {
		ISources actual = ISources.Utils.make(sourceTypes, rl, "someFile", "userId", "crypto", "cryptoKey");
		assertEquals(Arrays.asList(expected), Iterables.list(actual.singleSources(null)));
		assertEquals(Arrays.asList(expected), Iterables.list(actual.singleSources(null)));
	}

}
