package org.softwareFm.crowdsource.api.newGit;

import java.util.List;

import org.softwareFm.crowdsource.api.newGit.internal.Sources;

public interface ISources{
	String rl();

	String file();
	
	List<ISingleSource> singleSources(IRepoReader reader);

	public static class Utils {
		public static ISources make(List<ISourceType> sourceTypes, String rl, String file, String userId, String userCrypto, String cryptoKey) {
			return new Sources(rl, file, sourceTypes, userId, userCrypto, cryptoKey);
		}
	}

}
