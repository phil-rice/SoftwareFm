package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.softwareFm.crowdsource.api.newGit.IRepoData;
import org.softwareFm.crowdsource.api.newGit.IRepoPrim;
import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.RepoLocation;
import org.softwareFm.crowdsource.api.newGit.SourcedMap;
import org.softwareFm.crowdsource.utilities.collections.AbstractFindNextIterable;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.collections.Sets;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;

public class RepoData implements IRepoData {

	public final Map<RepoLocation, RepositoryChanges> repositories = Maps.newMap();
	public final String commitComment;
	private final IRepoPrim delegate;

	public RepoData(IRepoPrim delegate, String commitComment) {
		this.commitComment = commitComment;
		this.delegate = delegate;
	}

	@Override
	public Iterable<SourcedMap> read(final Sources sources, final int offset) {
		return new AbstractFindNextIterable<SourcedMap, List<Iterator<SourcedMap>>>() {
			@Override
			protected SourcedMap findNext(List<Iterator<SourcedMap>> context) throws Exception {
				return null;
			}

			@Override
			protected List<Iterator<SourcedMap>> reset() throws Exception {
				List<Iterator<SourcedMap>> context = Lists.map(sources, new IFunction1<ISingleSource, Iterator<SourcedMap>>() {
					int sum = 0;
					@Override
					public Iterator<SourcedMap> apply(ISingleSource from) throws Exception {
						String[] read = delegate.read(from);
						if (read.length+sum<offset)
							return Collections.<SourcedMap>emptyList().iterator();
						Iterator<SourcedMap> result = new AbstractFindNextIterable<SourcedMap, Iterator<SourcedMap>>() {
							@Override
							protected SourcedMap findNext(Iterator<SourcedMap> context) throws Exception {
								return null;
							}

							@Override
							protected Iterator<SourcedMap> reset() throws Exception {
								// TODO Auto-generated method stub
								return null;
							}
						}.iterator();
						return result;
					}
				});
				return context;
			}

		};
	}

	@Override
	public List<String> read(ISingleSource singleSource) {
		return delegate.read(singleSource);
	}

	@Override
	public void append(ISingleSource source, String newLine) {
		delegate.append(source, newLine);
	}

	@Override
	public void change(ISingleSource source, int index, String newLine) {
		delegate.change(source, index, newLine);
	}

	@Override
	public void delete(ISingleSource source, int index) {
		delegate.delete(source, index);
	}

	public static class RepositoryChanges {
		Map<String, FileChanges> fileChanges = Maps.newMap();
	}

	public static class FileChanges {
		Set<Integer> deleteIndicies = Sets.newSet();
		Map<Integer, String> changesValues = Maps.newMap();
		List<String> appendValues = Lists.newList();
	}

}
