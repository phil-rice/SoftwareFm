package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.Collections;
import java.util.List;

import org.softwareFm.crowdsource.api.newGit.IRepoReader;
import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.ISourceType;
import org.softwareFm.crowdsource.utilities.url.Urls;

public class GlobalSourceType implements ISourceType {

	@Override
	public List<ISingleSource> makeSourcesFor(IRepoReader repoData, String rl, String file, String userId, String userCrypto, String cryptoKey) {
		return Collections.<ISingleSource> singletonList(new RawSingleSource(Urls.compose(rl, file)));
	}

}
