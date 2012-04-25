package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.Collections;
import java.util.List;

import org.softwareFm.crowdsource.api.newGit.IRepoData;
import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.ISourceType;
import org.softwareFm.crowdsource.api.newGit.ISources;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;

/** All the data needed to access the data about an item or a collection */
public class Sources implements ISources {

	private final String rl;
	private final List<ISourceType> sourceTypes;
	private final String userId;
	private final String userCrypto;
	private List<ISingleSource> sourcesList;
	private final String file;
	private final String cryptoKey;

	public Sources(String rl, String file, List<ISourceType> sourceTypes, String userId, String userCrypto, String cryptoKey) {
		this.rl = rl;
		this.file = file;
		this.cryptoKey = cryptoKey;
		this.sourceTypes = Collections.unmodifiableList(sourceTypes);
		this.userId = userId;
		this.userCrypto = userCrypto;
	}

	@Override
	public List<ISingleSource> singleSources(final IRepoData repoData) {
		if (sourcesList == null)
			synchronized (this) {
				if (sourcesList == null)
					sourcesList = Collections.unmodifiableList(Lists.flatMap(sourceTypes, new IFunction1<ISourceType, List<ISingleSource>>() {
						@Override
						public List<ISingleSource> apply(ISourceType from) throws Exception {
							return from.makeSourcesFor(repoData, rl, file, userId, userCrypto, cryptoKey);
						}
					}));
			}
		return sourcesList;
	}

	@Override
	public String toString() {
		return "Sources [url=" + rl + ", sources=" + sourceTypes + ", userId=" + userId + ", userCrypto=" + userCrypto + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sourcesList == null) ? 0 : sourcesList.hashCode());
		result = prime * result + ((rl == null) ? 0 : rl.hashCode());
		result = prime * result + ((userCrypto == null) ? 0 : userCrypto.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sources other = (Sources) obj;
		if (sourcesList == null) {
			if (other.sourcesList != null)
				return false;
		} else if (!sourcesList.equals(other.sourcesList))
			return false;
		if (rl == null) {
			if (other.rl != null)
				return false;
		} else if (!rl.equals(other.rl))
			return false;
		if (userCrypto == null) {
			if (other.userCrypto != null)
				return false;
		} else if (!userCrypto.equals(other.userCrypto))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String rl() {
		return rl;
	}

	@Override
	public String file() {
		return file;
	}

}
