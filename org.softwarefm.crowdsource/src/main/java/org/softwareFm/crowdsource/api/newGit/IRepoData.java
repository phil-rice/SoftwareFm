package org.softwareFm.crowdsource.api.newGit;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.softwareFm.crowdsource.utilities.collections.Sets;
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

	/** If true methods can be called on it */
	boolean usable();

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

	}
}
