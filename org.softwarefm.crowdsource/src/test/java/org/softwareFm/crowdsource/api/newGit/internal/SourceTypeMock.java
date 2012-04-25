package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.Arrays;
import java.util.List;

import org.softwareFm.crowdsource.api.newGit.IRepoData;
import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.ISourceType;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;

public class SourceTypeMock implements ISourceType {

	private final List<String> suffixes;

	public SourceTypeMock(String... suffixes) {
		this.suffixes = Arrays.asList(suffixes);
	}

	@Override
	public List<ISingleSource> makeSourcesFor(IRepoData repoData, final String rl, final String file, String userId, final String userCrypto, final String cryptoKey) {
		return Lists.map(suffixes, new IFunction1<String, ISingleSource>() {
			@Override
			public ISingleSource apply(String from) throws Exception {
				return new EncryptedSingleSource("userId" + "/" + rl + "/" + from + "/" + file +"/"+cryptoKey, userCrypto);
			}
		});
	}

}
