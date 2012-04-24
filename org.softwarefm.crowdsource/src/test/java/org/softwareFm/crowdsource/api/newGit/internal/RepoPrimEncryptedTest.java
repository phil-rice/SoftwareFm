package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.List;

import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.strings.Strings;

public class RepoPrimEncryptedTest extends AbstractRepoPrimTest {

	private final String crypto = Crypto.makeKey();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		abxFile = "a/b/x/data.txt";
		acxFile = "a/c/x/data.txt";
		abxSource = new EncryptedSingleSource(abxFile, crypto);
		acxSouce = new EncryptedSingleSource(acxFile, crypto);
	}

	@Override
	protected void putFile(String rl, String lines) {
		List<String> encodedList = Lists.map(Strings.splitIgnoreBlanks(lines, "\n"), Crypto.encryptFn(crypto));
		gitFacard.putFileReturningRepoRl(rl, Strings.join(encodedList, "\n"));
	}
}
