package org.softwareFm.crowdsource.api.newGit;

import java.util.Map;

/**
 * Each transaction will have its own unique IRepoData<br />
 * 
 * These methods must all be called in a transaction, or they will fail. None of the changes will take place until the transaction is committed.<br />
 * 
 * The interface doesn't define whether it is using pessimistic or optimistic locking. In either case a RedoTransactionException maybe thrown (although it will be thrown from different methods: pessimistic can throw from any method, while optimistic only from the prepare phase)
 */
public interface IRepoData extends IRepoReader {

	/** The current value is merged with the existing map at that index */
	void setProperty(ISingleSource source, int index, String name, Object value);

	void append(ISingleSource source, Map<String, Object> newItem);

	void change(ISingleSource source, int index, Map<String, Object> newMap);

	void delete(ISingleSource source, int index);

	void setCommitMessage(String commitMessage);

}
