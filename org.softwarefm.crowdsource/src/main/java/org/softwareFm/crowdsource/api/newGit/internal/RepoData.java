package org.softwareFm.crowdsource.api.newGit.internal;

import java.nio.channels.FileLock;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jgit.storage.file.FileRepository;
import org.softwareFm.crowdsource.api.newGit.IRepoData;
import org.softwareFm.crowdsource.api.newGit.IRepoLocator;
import org.softwareFm.crowdsource.api.newGit.IRepoReaderImplementor;
import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.RepoLocation;
import org.softwareFm.crowdsource.api.newGit.exceptions.CannotChangeTwiceException;
import org.softwareFm.crowdsource.api.newGit.exceptions.CannotUseRepoAfterCommitOrRollbackException;
import org.softwareFm.crowdsource.api.newGit.facard.IGitFacard;
import org.softwareFm.crowdsource.api.newGit.facard.ILinkedGitFacard;
import org.softwareFm.crowdsource.constants.GitMessages;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.comparators.Comparators;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.IHasUrlCache;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.crowdsource.utilities.transaction.ITransactional;

public class RepoData implements IRepoData, ITransactional, IHasUrlCache {

	private final IGitFacard gitFacard;
	Map<ISingleSource, List<Map<String, Object>>> toAppend = Maps.newMap();
	Map<ISingleSource, List<IndexAndMap>> toChange = Maps.newMap();
	Map<ISingleSource, Set<Integer>> toDelete = Maps.newMap();

	final IRepoReaderImplementor repoReader;

	private boolean commitOrRollbackCalled;
	private String commitMessage;
	private final IRepoLocator repoLocator;

	public RepoData(ILinkedGitFacard gitFacard, IFunction1<ILinkedGitFacard, IRepoReaderImplementor> repoReaderBuilder, IRepoLocator repoLocator) {
		this.gitFacard = gitFacard;
		this.repoLocator = repoLocator;
		this.repoReader = Functions.call(repoReaderBuilder, gitFacard);
	}

	@Override
	public Map<String, Object> readRow(ISingleSource singleSource, int row) {
		checkOkToUse("readRow", singleSource, row);
		return repoReader.readRow(singleSource, row);
	}

	@Override
	public List<String> readRaw(ISingleSource singleSource) {
		checkOkToUse("readRaw", singleSource);
		return repoReader.readRaw(singleSource);
	}

	@Override
	public RepoLocation findRepository(ISingleSource singleSource) {
		checkOkToUse("findRepository", singleSource);
		RepoLocation local = gitFacard.findRepoRl(singleSource.fullRl());
		return local == null ? repoLocator.findRepository(singleSource) : local;
	}

	private void checkOkToUse(String methodName, Object... args) {
		if (commitOrRollbackCalled)
			throw new CannotUseRepoAfterCommitOrRollbackException(methodName, args);
	}

	@Override
	public void append(ISingleSource source, Map<String, Object> newItem) {
		checkOkToUse("append", source, newItem);
		readRaw(source);
		Maps.addToList(toAppend, source, newItem);
	}

	@Override
	public void change(ISingleSource source, int index, Map<String, Object> newMap) {
		checkOkToUse("change", index, newMap);
		readRaw(source);
		List<IndexAndMap> existing = Lists.nullSafe(Maps.get(toChange, source));
		for (IndexAndMap indexAndMap : existing)
			if (indexAndMap.index == index)
				throw new CannotChangeTwiceException(GitMessages.cannotMakeChangeToSameItemTwice, source, index, newMap, indexAndMap.map);
		Maps.addToList(toChange, source, new IndexAndMap(index, newMap));
	}

	@Override
	public void delete(ISingleSource source, int index) {
		checkOkToUse("delete", index);
		readRaw(source);
		Maps.addToSet(toDelete, source, index);
	}

	@Override
	public void setCommitMessage(String commitMessage) {
		if (this.commitMessage != null)
			throw new IllegalStateException(MessageFormat.format(GitMessages.commitMessageAlreadySet, this.commitMessage, commitMessage));
		this.commitMessage = commitMessage;
	}

	@Override
	public void commit() {
		commitOrRollbackCalled = true;
		List<String> repoRls = Lists.newList();
		for (Entry<ISingleSource, List<String>> entry : repoReader.rawCache().entrySet()) {
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
		if (repoRls.size() > 0)
			if (commitMessage == null)
				throw new IllegalStateException(GitMessages.commitMessageNotSet);

		for (String repoRl : repoRls) {
			FileRepository fileRepository = gitFacard.addAll(repoRl);
			gitFacard.commit(fileRepository, commitMessage);
		}
		repoReader.commit();

	}

	@Override
	public void rollback() {
		commitOrRollbackCalled = true;
		repoReader.rollback();
	}

	public Map<String, FileLock> locks() {
		return repoReader.locks();
	}

	@Override
	public void clearCaches() {
		if (repoReader instanceof IHasUrlCache)
			((IHasUrlCache) repoReader).clearCaches();
	}

	@Override
	public void clearCache(String url) {
		if (repoReader instanceof IHasUrlCache)
			((IHasUrlCache) repoReader).clearCache(url);
	}

}
