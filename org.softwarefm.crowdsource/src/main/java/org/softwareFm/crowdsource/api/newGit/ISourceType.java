package org.softwareFm.crowdsource.api.newGit;

import java.util.List;

public interface ISourceType {

	List<ISingleSource> makeSourcesFor(IRepoReader reader, String rl, String file, String userId, String userCrypto, String cryptoKey);

}
