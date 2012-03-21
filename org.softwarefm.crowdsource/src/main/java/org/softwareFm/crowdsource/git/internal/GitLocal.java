/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.git.internal;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;
import org.softwareFm.crowdsource.api.ICrowdSourceReadWriteApi;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitLocal;
import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.api.git.IGitWriter;
import org.softwareFm.crowdsource.api.git.IRepoFinder;
import org.softwareFm.crowdsource.api.git.RepoDetails;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.maps.UrlCache;

public class GitLocal implements IGitLocal {

	private static Logger logger = Logger.getLogger(IGitLocal.class);

	private final Map<File, Long> lastPulledTime = Collections.synchronizedMap(Maps.<File, Long> newMap());
	private final long period;
	private final Object lock = new Object();

	private final UrlCache<Map<String, Object>> aboveRepoCache = new UrlCache<Map<String, Object>>();

	private final ICrowdSourceReadWriteApi readWriteApi;

	private final String remoteGitPrefix;

	public GitLocal(ICrowdSourceReadWriteApi readWriteApi, String remoteGitPrefix, long period) {
		this.readWriteApi = readWriteApi;
		this.remoteGitPrefix = remoteGitPrefix;
		this.period = period;
	}

	@Override
	public String getFileAsString(IFileDescription fileDescription) {
		String url = fileDescription.url();
		String message = "  " + url + "   " + getClass().getSimpleName() + ".getFileAsString(" + fileDescription + ")";
		logger.debug(message);
		pullIfNeeded(fileDescription);
		String result = readWriteApi.gitOperations().getFileAsString( fileDescription);
		logger.debug(message + " -> " + result);
		return result;
	}

	@Override
	public int countOfFileAsListsOfMap(IFileDescription fileDescription) {
		pullIfNeeded(fileDescription); // possible issue here, in that this and the various "get" methods may return a different result
		return IGitReader.Utils.countOfFileAsListsOfMap(readWriteApi, fileDescription);
	}

	@Override
	public Iterable<Map<String, Object>> getFileAsListOfMaps(IFileDescription fileDescription) {
		String url = fileDescription.url();
		String message = "  " + url + "   " + getClass().getSimpleName() + ".getFileAsListOfMaps(" + fileDescription + ")";
		logger.debug(message);
		pullIfNeeded(fileDescription);
		Iterable<Map<String, Object>> result = readWriteApi.gitOperations().getFileAsListOfMaps( fileDescription);
		logger.debug(message + " -> " + result);
		return result;
	}

	@Override
	public Map<String, Object> getFile(IFileDescription fileDescription) {
		String url = fileDescription.url();
		String message = "  " + url + "   " + getClass().getSimpleName() + ".getFile(" + fileDescription + ")";
		logger.debug(message);
		pullIfNeeded(fileDescription);
		Map<String, Object> result = readWriteApi.gitOperations().getFile(fileDescription);
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
		Map<String, Object> result = readWriteApi.gitOperations().getFileAndDescendants(fileDescription);
		if (aboveRepoCache.containsKey(url))
			return aboveRepoCache.get(url);
		logger.debug(getClass().getSimpleName() + ".getFileAndDescendants_end(" + fileDescription + ")-> " + result);
		return result;
	}

	private void pullIfNeeded(IFileDescription fileDescription) {
		final String url = fileDescription.url();
		if (aboveRepoCache.containsKey(url))
			return;
		synchronized (lock) {
			if (aboveRepoCache.containsKey(url))
				return;// not redundant
			IGitOperations gitOperations = readWriteApi.gitOperations();
			File repositoryUrl = fileDescription.findRepositoryUrl(gitOperations.getRoot());
			final long now = System.currentTimeMillis();
			if (repositoryUrl == null) {
				logger.debug("        " + getClass().getSimpleName() + ".pullIfNeeded/clone(" + fileDescription + ")");
				readWriteApi.modify(IRepoFinder.class, new ICallback<IRepoFinder>() {
					@Override
					public void process(IRepoFinder repoFinder) throws Exception {
						RepoDetails repoDetails = repoFinder.findRepoUrl(url);
						if (repoDetails.aboveRepository()) {
							aboveRepoCache.put(url, repoDetails.getAboveRepositoryData());
							return;// nothing to pull
						}
						String remoteUrl = repoDetails.getRepositoryUrl();
						GitLocal.this.clone(remoteUrl, now);
					}
				});
			} else if (needToPull(repositoryUrl, now)) {
				logger.debug("        " + getClass().getSimpleName() + ".pullIfNeeded/pull(" + fileDescription + ")");
				String remoteUrl = Files.offset(gitOperations.getRoot(), repositoryUrl);
				gitOperations.pull(remoteUrl);
			} else
				logger.debug("        " + getClass().getSimpleName() + ".pullIfNeeded/notNeeded(" + fileDescription + ")");
		}
	}

	private void clone(final String remoteUrl, final long now) {
		final IGitOperations gitOperations = readWriteApi.gitOperations();
		final File root = gitOperations.getRoot();
		Files.doOperationInLock(root, CommonConstants.lockFileName, new IFunction1<File, Void>() {
			@Override
			public Void apply(File from) throws Exception {
				gitOperations.init(remoteUrl);
				gitOperations.setConfigForRemotePull(remoteUrl, remoteGitPrefix);
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
		final IGitOperations gitOperations = readWriteApi.gitOperations();
		return gitOperations.getRoot();
	}

	@Override
	public void init(final String url) {
		logger.debug(getClass().getSimpleName() + ".init(" + url + ")");
		readWriteApi.modify(IGitWriter.class, new ICallback<IGitWriter>() {
			@Override
			public void process(IGitWriter gitWriter) throws Exception {
				gitWriter.init(url);

			}
		});
	}

	@Override
	public void put(final IFileDescription fileDescription, final Map<String, Object> data) {
		logger.debug(getClass().getSimpleName() + ".put(" + fileDescription + ", " + data + ")");
		readWriteApi.modify(IGitWriter.class, new ICallback<IGitWriter>() {
			@Override
			public void process(IGitWriter gitWriter) throws Exception {
				gitWriter.put(fileDescription, data);
				clearCaches();
			}
		});
	}

	@Override
	public void clearCaches() {
		logger.debug(getClass().getSimpleName() + ".clearCaches");
		lastPulledTime.clear();
		readWriteApi.modify(IRepoFinder.class, new ICallback<IRepoFinder>() {
			@Override
			public void process(IRepoFinder repoFinder) throws Exception {
				repoFinder.clearCaches();
			}
		});
		aboveRepoCache.clear();
	}

	@Override
	public void clearCache(String url) {
		clearCaches();
	}

	@Override
	public void delete(final IFileDescription fileDescription) {
		logger.debug(getClass().getSimpleName() + ".delete(" + fileDescription + ")");
		readWriteApi.modify(IGitWriter.class, new ICallback<IGitWriter>() {
			@Override
			public void process(IGitWriter gitWriter) throws Exception {
				gitWriter.delete(fileDescription);
			}
		});
	}
}