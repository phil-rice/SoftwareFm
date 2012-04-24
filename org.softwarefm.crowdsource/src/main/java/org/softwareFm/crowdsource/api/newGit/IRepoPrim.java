package org.softwareFm.crowdsource.api.newGit;

import java.util.List;
import java.util.Map;

/**
 * This is the interface for implementors of the backend data system. <br />
 * 
 * The backend data system is that there are objects referenced by ISingleSource. These objects are represented by a list of strings that represent Map<String,Object>. The effort of hiding this is high, as many queries only need to count the number of maps, especially when paging through the data, and parsing and decrypting the lines is expensive<br />
 * * Implementors should extends ITransactional, and add themselves to the transaction manager. It is expected that the implementors will lock any repositoryLocation that is to be changed. Should that repositoryLocation be locked, a "RetryLaterException" will be raised which will cause the transactional manager to "retry later". Changes to the file system should only take place in the prepare / commit / rollback, and if rollback is called no changes should be apparent to an external observer
 * 
 * */
public interface IRepoPrim {
	List<String> read(ISingleSource singleSource);

	void append(ISingleSource source, Map<String, Object> newItem);

	void change(ISingleSource source, int index, Map<String, Object> newMap);

	void delete(ISingleSource source, int index);


}
