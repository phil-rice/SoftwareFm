package org.softwareFm.crowdsource.api.newGit;

import java.util.List;

import org.softwareFm.crowdsource.api.newGit.internal.Sources;

public interface ISources extends Iterable<ISingleSource> {
	String url();

	List<String> sources();

	String userId();

	String userCrypto();

	public static class Utils {
		public static ISources make(String url, List<String> sources, String userId, String userCrypto){
			return new Sources(url, sources, userId, userCrypto);
		}
	}
}
