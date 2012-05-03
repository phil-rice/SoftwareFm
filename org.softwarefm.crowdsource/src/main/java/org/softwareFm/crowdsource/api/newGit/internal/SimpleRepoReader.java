package org.softwareFm.crowdsource.api.newGit.internal;

import java.nio.channels.FileLock;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.crowdsource.api.newGit.IRepoReaderImplementor;
import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.facard.IGitFacard;
import org.softwareFm.crowdsource.api.newGit.facard.RepoRlAndText;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.exceptions.AggregateException;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.strings.Strings;

public class SimpleRepoReader implements IRepoReaderImplementor {
	private final IGitFacard gitFacard;
	private final Map<String, FileLock> locks = Maps.newMap();

	private final Map<ISingleSource, Map<Integer, Map<String, Object>>> decryptedCache = Maps.newMap();
	private final Map<ISingleSource, List<String>> rawCache = Maps.newMap();

	public SimpleRepoReader(IGitFacard gitFacard) {
		this.gitFacard = gitFacard;
	}

	@Override
	public Map<String, FileLock> locks() {
		return Collections.unmodifiableMap(locks);
	}
	@Override
	public Map<String, Object> readRow(final ISingleSource singleSource, final int row) {
		Map<Integer, Map<String, Object>> decryptedMap = Maps.findOrCreate(decryptedCache, singleSource, new Callable<Map<Integer, Map<String, Object>>>() {
			@Override
			public Map<Integer, Map<String, Object>> call() throws Exception {
				return new HashMap<Integer, Map<String, Object>>();
			}
		});
		Map<String, Object> result = Maps.findOrCreate(decryptedMap, row, new Callable<Map<String, Object>>() {
			@Override
			public Map<String, Object> call() throws Exception {
				List<String> raw = readRaw(singleSource);
				if (row < 0 || row > raw.size())
					throw new IndexOutOfBoundsException();
				return Json.mapFromString(singleSource.decypt(raw.get(row)));
			}
		});
		return result;
	}

	@Override
	public List<String> readRaw(final ISingleSource singleSource) {
		return Maps.findOrCreate(rawCache, singleSource, new Callable<List<String>>() {
			@Override
			public List<String> call() throws Exception {
				String fullRl = singleSource.fullRl();
				final RepoRlAndText repoRlAndText = gitFacard.getFile(fullRl);
				Maps.findOrCreate(locks, repoRlAndText.repoRl, new Callable<FileLock>() {
					@Override
					public FileLock call() throws Exception {
						return gitFacard.lock(repoRlAndText.repoRl);
					}
				});
				List<String> raw = Strings.splitIgnoreBlanks(repoRlAndText.text, "\n");
				return raw;
			}
		});
	}



	@Override
	public Map<ISingleSource, List<String>> rawCache() {
		return Collections.unmodifiableMap(rawCache);
	}

	@Override
	public void commit() {
		releaseLocks();
	}

	@Override
	public void rollback() {
		releaseLocks();
	}

	private void releaseLocks() {
		List<Exception> exceptions = Lists.newList();
		for (FileLock lock : locks.values())
			try {
				Files.releaseAndClose(lock);
			} catch (Exception e) {
				exceptions.add(e);
			}
		locks.clear();
		if (exceptions.size() > 0)
			throw new AggregateException(exceptions);
	}
}
