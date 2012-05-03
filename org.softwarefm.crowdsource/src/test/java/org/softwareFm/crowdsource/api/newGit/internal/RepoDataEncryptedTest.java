package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.List;

import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.strings.Strings;

public class RepoDataEncryptedTest extends AbstractRepoDataTest {

	private final String crypto = Crypto.makeKey();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		abxSource = new EncryptedSingleSource(abxFile, crypto);
		abySource = new EncryptedSingleSource(abyFile, crypto);
		acxSource = new EncryptedSingleSource(acxFile, crypto);
		acySource = new EncryptedSingleSource(acyFile, crypto);
	}

	@Override
	protected void putFilePrim(String rl, String lines) {
		List<String> encodedList = Lists.map(Strings.splitIgnoreBlanks(lines, "\n"), Crypto.encryptFn(crypto));
		String repoRl = remoteFacard.putFileReturningRepoRl(rl, Strings.join(encodedList, "\n"));
		addAllAndCommit(remoteFacard, repoRl);
	}
}
