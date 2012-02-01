package org.softwareFm.server.internal;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitLocal;
import org.softwareFm.server.IGitOperations;
import org.softwareFm.server.IGitWriter;
import org.softwareFm.server.IRepoFinder;
import org.softwareFm.server.RepoDetails;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.maps.UrlCache;

public class GitLocal implements IGitLocal {

	private static Logger logger = Logger.getLogger(IGitLocal.class);

	private final String remotePrefix;
	private final Map<File, Long> lastPulledTime = Collections.synchronizedMap(Maps.<File, Long> newMap());
	private final IGitOperations gitOperations;
	private final IRepoFinder repoFinder;

	private final long period;
	private final Object lock = new Object();
	private final IGitWriter gitWriter;

	private final UrlCache<Map<String, Object>> aboveRepoCache = new UrlCache<Map<String, Object>>();

	public GitLocal(IRepoFinder repoFinder, IGitOperations gitOperations, IGitWriter gitWriter, String remotePrefix, long period) {
		this.repoFinder = repoFinder;
		this.gitOperations = gitOperations;
		this.gitWriter = gitWriter;
		this.remotePrefix = remotePrefix;
		this.period = period;
	}

	@Override
	public Map<String, Object> getFile(IFileDescription fileDescription) {
		String url = fileDescription.url();
		String message = "  " + url + "   " + getClass().getSimpleName() + ".getFile(" + fileDescription + ")";
		logger.debug(message);
		pullIfNeeded(fileDescription);
		Map<String, Object> result = gitOperations.getFile(fileDescription);
		if (aboveRepoCache.containsKey(url))
			return aboveRepoCache.get(url);
		logger.debug(message + " -> " + result);
		return result;
	}

	@Override
	public Map<String, Object> getFileAndDescendants(IFileDescription fileDescription) {
		String url = fileDescription.url();
		logger.debug(getClass().getSimpleName() + ".getFileAndDescendants(" + fileDescription + ")");
		pullIfNeeded(fileDescription);
		Map<String, Object> result = gitOperations.getFileAndDescendants(fileDescription);
		if (aboveRepoCache.containsKey(url))
			return aboveRepoCache.get(url);
		logger.debug(getClass().getSimpleName() + ".getFileAndDescendants_end(" + fileDescription + ")-> " + result);
		return result;
	}

	private void pullIfNeeded(IFileDescription fileDescription) {
		String url = fileDescription.url();
		if (aboveRepoCache.containsKey(url))
			return;
		synchronized (lock) {
			if (aboveRepoCache.containsKey(url))
				return;// not redundant
			File repositoryUrl = fileDescription.findRepositoryUrl(gitOperations.getRoot());
			long now = System.currentTimeMillis();
			if (repositoryUrl == null) {
				logger.debug("        " + getClass().getSimpleName() + ".pullIfNeeded/clone(" + fileDescription + ")");
				RepoDetails repoDetails = repoFinder.findRepoUrl(url);
				if (repoDetails.aboveRepository()) {
					aboveRepoCache.put(url, repoDetails.getAboveRepositoryData());
					return;// nothing to pull
				}
				String remoteUrl = repoDetails.getRepositoryUrl();
				clone(remoteUrl, now);
			} else if (needToPull(repositoryUrl, now)) {
				logger.debug("        " + getClass().getSimpleName() + ".pullIfNeeded/pull(" + fileDescription + ")");
				String remoteUrl = Files.offset(gitOperations.getRoot(), repositoryUrl);
				gitOperations.pull(remoteUrl);
			} else
				logger.debug("        " + getClass().getSimpleName() + ".pullIfNeeded/notNeeded(" + fileDescription + ")");
		}
	}

	private void clone(final String remoteUrl, final long now) {
		final File root = gitOperations.getRoot();
		Files.doOperationInLock(root, CommonConstants.lockFileName, new IFunction1<File, Void>() {
			@Override
			public Void apply(File from) throws Exception {
				gitOperations.init(remoteUrl);
				gitOperations.setConfigForRemotePull(remoteUrl, remotePrefix);
				gitOperations.pull(remoteUrl);
				File remote = new File(root, remoteUrl);
				lastPulledTime.put(remote, now);
				return null;
			}
		});
	}

	private boolean needToPull(File repositoryUrl, long now) {
		Long time = lastPulledTime.get(repositoryUrl);
		boolean result = time == null || now > time + period;
		if (result)
			lastPulledTime.put(repositoryUrl, now);
		return result;
	}

	@Override
	public File getRoot() {
		return gitOperations.getRoot();
	}

	@Override
	public void init(String url) {
		logger.debug(getClass().getSimpleName() + ".init(" + url + ")");
		gitWriter.init(url);
	}

	@Override
	public void put(IFileDescription fileDescription, Map<String, Object> data) {
		logger.debug(getClass().getSimpleName() + ".put(" + fileDescription + ", " + data + ")");
		gitWriter.put(fileDescription, data);
		clearCaches();
	}

	@Override
	public void clearCaches() {
		logger.debug(getClass().getSimpleName() + ".clearCaches");
		lastPulledTime.clear();
		repoFinder.clearCaches();
		aboveRepoCache.clear();
	}

	@Override
	public void clearCache(String url) {
		logger.debug(getClass().getSimpleName() + ".clearCache(" + url + ")");
		lastPulledTime.clear();
		repoFinder.clearCache(url);
		aboveRepoCache.clear();
	}

	@Override
	public void delete(IFileDescription fileDescription) {
		logger.debug(getClass().getSimpleName() + ".delete(" + fileDescription + ")");
		gitWriter.delete(fileDescription);
	}
}
