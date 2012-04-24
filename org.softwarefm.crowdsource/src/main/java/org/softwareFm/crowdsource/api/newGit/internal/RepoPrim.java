package org.softwareFm.crowdsource.api.newGit.internal;

import java.nio.channels.FileLock;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.eclipse.jgit.storage.file.FileRepository;
import org.softwareFm.crowdsource.api.newGit.IRepoPrim;
import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.facard.CannotUseRepoAfterCommitOrRollbackException;
import org.softwareFm.crowdsource.api.newGit.facard.IGitFacard;
import org.softwareFm.crowdsource.api.newGit.facard.RepoRlAndText;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.comparators.Comparators;
import org.softwareFm.crowdsource.utilities.exceptions.AggregateException;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.crowdsource.utilities.transaction.ITransactional;

public class RepoPrim implements IRepoPrim, ITransactional {

	private final IGitFacard gitFacard;
	final Map<String, FileLock> locks = Maps.newMap();
	Map<ISingleSource, List<Map<String, Object>>> toAppend = Maps.newMap();
	Map<ISingleSource, List<IndexAndMap>> toChange = Maps.newMap();
	Map<ISingleSource, Set<Integer>> toDelete = Maps.newMap();

	private final Map<ISingleSource, List<String>> decryptedCache = Maps.newMap();
	private final Map<ISingleSource, List<String>> rawCache = Maps.newMap();

	private boolean commitOrRollbackCalled;
	private final String commitMessage;

	public RepoPrim(IGitFacard gitFacard, String commitMessage) {
		this.gitFacard = gitFacard;
		this.commitMessage = commitMessage;
	}

	@Override
	public List<String> read(final ISingleSource singleSource) {
		checkOkToUse("read", singleSource);
		if (!decryptedCache.containsKey(singleSource)) {
			String fullRl = singleSource.fullRl();
			final RepoRlAndText repoRlAndText = gitFacard.getFile(fullRl);
			Maps.findOrCreate(locks, repoRlAndText.repoRl, new Callable<FileLock>() {
				@Override
				public FileLock call() throws Exception {
					return gitFacard.lock(repoRlAndText.repoRl);
				}
			});
			List<String> raw = Strings.splitIgnoreBlanks(repoRlAndText.text, "\n");
			List<String> decrypted = Lists.map(raw, singleSource.decyptLine());
			rawCache.put(singleSource, raw);
			decryptedCache.put(singleSource, decrypted);
		}
		return decryptedCache.get(singleSource);

	}

	private void checkOkToUse(String methodName, Object... args) {
		if (commitOrRollbackCalled)
			throw new CannotUseRepoAfterCommitOrRollbackException(methodName, args);
	}

	@Override
	public void append(ISingleSource source, Map<String, Object> newItem) {
		read(source);
		Maps.addToList(toAppend, source, newItem);
	}

	@Override
	public void change(ISingleSource source, int index, Map<String, Object> newMap) {
		checkOkToUse("change", index, newMap);
		read(source);
		Maps.addToList(toChange, source, new IndexAndMap(index, newMap));
	}

	@Override
	public void delete(ISingleSource source, int index) {
		checkOkToUse("delete", index);
		read(source);
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

}
