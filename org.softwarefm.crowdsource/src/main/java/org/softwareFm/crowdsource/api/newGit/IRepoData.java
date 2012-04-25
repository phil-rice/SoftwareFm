package org.softwareFm.crowdsource.api.newGit;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.newGit.internal.Sources;

/**
 * Each transaction will have its own unique IRepoData<br />
 * 
 * These methods must all be called in a transaction, or they will fail. None of the changes will take place until the transaction is committed.<br />
 * 
 * Other transactions are locked out from messing with the repositories accessed by these methods
 */
public interface IRepoData {

	List<String> readRaw(ISingleSource singleSource);

	Map<String, Object> readRow(ISingleSource singleSource, int row);

	Map<String, Object> readFirstRow(ISingleSource singleSource);

	List<Map<String, Object>> readAllRows(ISingleSource singleSource);

	/** Start from offset, returns empty iterator if offset is too big */
	Iterable<SourcedMap> read(Sources sources, int offset);

	/** Start from offset, returns empty iterator if offset is too big */
	Iterable<SourcedMap> read(ISingleSource source, int offset);

	int countLines(ISingleSource source);

	Map<ISingleSource, Integer> countLines(ISources sources);

	/** The current value is merged with the existing map at that index */
	void setProperty(ISingleSource source, int index, String name, Object value);

	RepoLocation findRepository(ISingleSource singleSource);

	Collection<RepoLocation> findRepositories(ISources sources);

	String readPropertyFromFirstLine(IRepoData reader, ISingleSource singleSource, String cryptoKey);

	void append(ISingleSource source, Map<String, Object> newItem);

	void change(ISingleSource source, int index, Map<String, Object> newMap);

	void delete(ISingleSource source, int index);

}
