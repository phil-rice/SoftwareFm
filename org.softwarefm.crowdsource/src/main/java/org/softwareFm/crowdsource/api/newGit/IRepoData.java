package org.softwareFm.crowdsource.api.newGit;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.softwareFm.crowdsource.api.IContainerBuilder;
import org.softwareFm.crowdsource.api.IFactory;
import org.softwareFm.crowdsource.api.newGit.facard.ILinkedGitFacard;
import org.softwareFm.crowdsource.api.newGit.internal.LinkedRepoReader;
import org.softwareFm.crowdsource.api.newGit.internal.RepoData;
import org.softwareFm.crowdsource.api.newGit.internal.SimpleRepoReader;
import org.softwareFm.crowdsource.utilities.collections.ITransactionalMutableSimpleSet;
import org.softwareFm.crowdsource.utilities.collections.Sets;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;

/**
 * Each transaction will have its own unique IRepoData<br />
 * 
 * These methods must all be called in a transaction, or they will fail. None of the changes will take place until the transaction is committed.<br />
 * 
 * The interface doesn't define whether it is using pessimistic or optimistic locking. In either case a RedoTransactionException maybe thrown (although it will be thrown from different methods: pessimistic can throw from any method, while optimistic only from the prepare phase)
 */
public interface IRepoData extends IRepoReader, IRepoLocator {

	void append(ISingleSource source, Map<String, Object> newItem);

	void change(ISingleSource source, int index, Map<String, Object> newMap);

	void delete(ISingleSource source, int index);

	void setCommitMessage(String commitMessage);

	public static class Utils {

		/** The current value is merged with the existing map at that index */
		public static void setProperty(IRepoData repoData, ISingleSource source, int index, String name, Object value) {
			Map<String, Object> existing = repoData.readRow(source, index);
			Map<String, Object> newMap = Maps.with(existing, name, value);
			repoData.change(source, index, newMap);
		}

		public static Collection<RepoLocation> findRepositories(IRepoData repoData, ISources sources) {
			Set<RepoLocation> result = Sets.newSet();
			for (ISingleSource source : sources.singleSources(repoData)) {
				RepoLocation repoLocation = repoData.findRepository(source);
				result.add(repoLocation);
			}
			return result;
		}

		/** Adds the elements to the container to allow the local repo data to work */
		public static void configureLocalContainer(IContainerBuilder container, final ILinkedGitFacard gitFacard, final IRepoLocator repoLocator, IAccessControlList acl) {
			final Set<String> hasPulled = Sets.newSet();
			container.register(IRepoData.class, new IFactory<IRepoData>() {
				@Override
				public IRepoData build() {
					return new RepoData(gitFacard, new IFunction1<ILinkedGitFacard, IRepoReaderImplementor>() {
						@Override
						public IRepoReaderImplementor apply(ILinkedGitFacard from) throws Exception {
							SimpleRepoReader delegate = new SimpleRepoReader(gitFacard);
							ITransactionalMutableSimpleSet<String> transactionalHasPulled = Sets.asTransactionalSet(hasPulled);
							LinkedRepoReader repoReader = new LinkedRepoReader(delegate, gitFacard, repoLocator, transactionalHasPulled);
							return repoReader;
						}
					}, repoLocator);
				}

			});
		}

	}
}
