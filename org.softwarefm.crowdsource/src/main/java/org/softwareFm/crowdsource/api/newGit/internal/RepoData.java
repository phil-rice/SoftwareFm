package org.softwareFm.crowdsource.api.newGit.internal;

import java.nio.channels.FileLock;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.eclipse.jgit.storage.file.FileRepository;
import org.softwareFm.crowdsource.api.newGit.IRepoData;
import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.ISources;
import org.softwareFm.crowdsource.api.newGit.RepoLocation;
import org.softwareFm.crowdsource.api.newGit.facard.CannotUseRepoAfterCommitOrRollbackException;
import org.softwareFm.crowdsource.api.newGit.facard.IGitFacard;
import org.softwareFm.crowdsource.api.newGit.facard.RepoRlAndText;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.collections.Sets;
import org.softwareFm.crowdsource.utilities.comparators.Comparators;
import org.softwareFm.crowdsource.utilities.exceptions.AggregateException;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.crowdsource.utilities.transaction.ITransactional;

public class RepoData implements IRepoData, ITransactional {

	private final IGitFacard gitFacard;
	final Map<String, FileLock> locks = Maps.newMap();
	Map<ISingleSource, List<Map<String, Object>>> toAppend = Maps.newMap();
	Map<ISingleSource, List<IndexAndMap>> toChange = Maps.newMap();
	Map<ISingleSource, Set<Integer>> toDelete = Maps.newMap();

	private final Map<ISingleSource, Map<Integer, Map<String, Object>>> decryptedCache = Maps.newMap();
	private final Map<ISingleSource, List<String>> rawCache = Maps.newMap();

	private boolean commitOrRollbackCalled;
	private final String commitMessage;

	public RepoData(IGitFacard gitFacard, String commitMessage) {
		this.gitFacard = gitFacard;
		this.commitMessage = commitMessage;
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
	public int countLines(ISingleSource source) {
		return readRaw(source).size();
	}

	@Override
	public Map<String, Object> readFirstRow(ISingleSource singleSource) {
		if (countLines(singleSource) > 0)
			return readRow(singleSource, 0);
		else
			return Maps.emptyStringObjectMap();
	}

	@Override
	public String readPropertyFromFirstLine(IRepoData reader, ISingleSource singleSource, String cryptoKey) {
		return (String) readFirstRow(singleSource).get(cryptoKey);
	}

	@Override
	public List<Map<String, Object>> readAllRows(ISingleSource singleSource) {
		List<String> readRaw = readRaw(singleSource);
		List<Map<String, Object>> result = Lists.newList();
		for (int i = 0; i < readRaw.size(); i++)
			result.add(readRow(singleSource, i));
		return result;
	}

	@Override
	public List<String> readRaw(final ISingleSource singleSource) {
		checkOkToUse("read", singleSource);
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

	private void checkOkToUse(String methodName, Object... args) {
		if (commitOrRollbackCalled)
			throw new CannotUseRepoAfterCommitOrRollbackException(methodName, args);
	}

	@Override
	public void append(ISingleSource source, Map<String, Object> newItem) {
		readRaw(source);
		Maps.addToList(toAppend, source, newItem);
	}

	@Override
	public void change(ISingleSource source, int index, Map<String, Object> newMap) {
		checkOkToUse("change", index, newMap);
		readRaw(source);
		Maps.addToList(toChange, source, new IndexAndMap(index, newMap));
	}

	@Override
	public void delete(ISingleSource source, int index) {
		checkOkToUse("delete", index);
		readRaw(source);
		Maps.addToSet(toDelete, source, index);
	}

	@Override
	public void commit() {
		List<String> repoRls = Lists.newList();
		for (Entry<ISingleSource, List<String>> entry : rawCache.entrySet()) {
			List<String> rawCacheItem = entry.getValue();
			boolean changed = false;
			ISingleSource source = entry.getKey();

			List<IndexAndMap> changeItems = toChange.get(source);
			for (IndexAndMap indexAndMap : Lists.nullSafe(changeItems)) {
				rawCacheItem.set(indexAndMap.index, source.encrypt(Json.toString(indexAndMap.map)));
				changed = true;
			}

			Set<Integer> itemsToDelete = toDelete.get(source);
			List<Integer> deleteItems = itemsToDelete == null ? Collections.<Integer> emptyList() : Lists.sort(itemsToDelete, Comparators.invert(Comparators.<Integer> naturalOrder()));
			for (Integer index : deleteItems) {
				rawCacheItem.remove(index.intValue());
				changed = true;
			}

			List<Map<String, Object>> appendItems = toAppend.get(source);
			for (Map<String, Object> item : Lists.nullSafe(appendItems)) {
				rawCacheItem.add(source.encrypt(Json.toString(item)));
				changed = true;
			}
			if (changed) {
				String text = Strings.join(rawCacheItem, "\n");
				String rl = source.fullRl();
				String repoRl = gitFacard.putFileReturningRepoRl(rl, text);
				repoRls.add(repoRl);
			}

		}
		for (String repoRl : repoRls) {
			FileRepository fileRepository = gitFacard.addAll(repoRl);
			gitFacard.commit(fileRepository, commitMessage);
		}
		releaseLocks();
	}

	@Override
	public void rollback() {
		releaseLocks();
	}

	private void releaseLocks() {
		commitOrRollbackCalled = true;
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

	@Override
	public Map<ISingleSource, Integer> countLines(ISources sources) {
		Map<ISingleSource, Integer> result = Maps.newMap();
		for (ISingleSource source: sources.singleSources(this))
			result.put(source, countLines(source));
		return result;
	}


	@Override
	public Collection<RepoLocation> findRepositories(ISources sources) {
		Set<RepoLocation> result = Sets.newSet();
		for (ISingleSource source: sources.singleSources(this)){
			RepoLocation repoLocation = findRepository(source);
			result.add(repoLocation);
		}
		return result;
	}

	@Override
	public RepoLocation findRepository(ISingleSource source) {
		String fullRl = source.fullRl();
		RepoLocation repoLocation = gitFacard.findRepoRl(fullRl);
		return repoLocation;
	}

}
