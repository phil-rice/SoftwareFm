package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.ISources;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;

/** All the data needed to access the data about an item or a collection */
public class Sources implements ISources {

	public final String url;
	public final List<String> sources;
	public final String userId;
	public final String userCrypto;
	private List<ISingleSource> sourcesList;

	public Sources(String url, List<String> sources, String userId, String userCrypto) {
		this.url = url;
		this.sources = Collections.unmodifiableList(sources);
		this.userId = userId;
		this.userCrypto = userCrypto;
	}

	@Override
	public Iterator<ISingleSource> iterator() {
		if (sourcesList == null)
			synchronized (this) {
				if (sourcesList == null)
					sourcesList = Lists.map(sources, new IFunction1<String, ISingleSource>() {
						@Override
						public ISingleSource apply(String from) throws Exception {
							return new SingleSource(url, from, userId, userCrypto);
						}
					});
			}
		return sourcesList.iterator();
	}

	@Override
	public String toString() {
		return "Sources [url=" + url + ", sources=" + sources + ", userId=" + userId + ", userCrypto=" + userCrypto + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sourcesList == null) ? 0 : sourcesList.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
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
	public String url() {
		return url;
	}

	@Override
	public List<String> sources() {
		return sources;
	}

	@Override
	public String userId() {
		return userId;
	}

	@Override
	public String userCrypto() {
		return userCrypto;
	}
}
