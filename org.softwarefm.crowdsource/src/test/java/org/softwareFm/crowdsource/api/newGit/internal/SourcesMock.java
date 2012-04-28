package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.softwareFm.crowdsource.api.newGit.IRepoData;
import org.softwareFm.crowdsource.api.newGit.IRepoReader;
import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.ISources;

public class SourcesMock implements ISources {

	private final List<ISingleSource> singleSources;
	private final IRepoData expectedRepoData;
	private final String rl;
	private final String file;

	public SourcesMock(IRepoData expectedRepoData, String rl, String file, ISingleSource... singleSources) {
		this.expectedRepoData = expectedRepoData;
		this.rl = rl;
		this.file = file;
		this.singleSources = Arrays.asList(singleSources);

	}

	@Override
	public String rl() {
		return rl;
	}

	@Override
	public String file() {
		return file;
	}

	@Override
	public List<ISingleSource> singleSources(IRepoReader repoData) {
		Assert.assertSame(expectedRepoData, repoData);
		return singleSources;
	}

}
