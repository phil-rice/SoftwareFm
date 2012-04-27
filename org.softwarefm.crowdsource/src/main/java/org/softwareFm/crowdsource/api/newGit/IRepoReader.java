package org.softwareFm.crowdsource.api.newGit;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Each transaction will have its own unique IRepoData<br />
 * 
 * These methods must all be called in a transaction, or they will fail. None of the changes will take place until the transaction is committed.<br />
 * 
 * The interface doesn't define whether it is using pessimistic or optimistic locking. In either case a RedoTransactionException maybe thrown (although it will be thrown from different methods: pessimistic can throw from any method, while optimistic only from the prepare phase)
 */
public interface IRepoReader extends ISingleRowReader{

	List<String> readRaw(ISingleSource singleSource);


	Map<String, Object> readFirstRow(ISingleSource singleSource);

	List<Map<String, Object>> readAllRows(ISingleSource singleSource);

	/** Start from offset, returns empty iterator if offset is too big */
	Iterable<SourcedMap> read(ISources sources, int offset);

	/** Start from offset, returns empty iterator if offset is too big */
	Iterable<SourcedMap> read(ISingleSource source, int offset);

	int countLines(ISingleSource source);

	Map<ISingleSource, Integer> countLines(ISources sources);

	RepoLocation findRepository(ISingleSource singleSource);

	Collection<RepoLocation> findRepositories(ISources sources);

	String readPropertyFromFirstLine(IRepoReader reader, ISingleSource singleSource, String cryptoKey);

}
