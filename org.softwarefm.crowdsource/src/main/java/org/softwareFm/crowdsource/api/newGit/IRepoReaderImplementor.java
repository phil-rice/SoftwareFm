package org.softwareFm.crowdsource.api.newGit;

import java.nio.channels.FileLock;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.utilities.transaction.ITransactional;

public interface IRepoReaderImplementor extends IRepoReader, ITransactional {

	Map<ISingleSource, List<String>> rawCache();

	Map<String, FileLock> locks();

}
