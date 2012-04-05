/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.git.internal;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitLocal;
import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.git.IGitReader;
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

	private final IContainer comtainer;

	private final String remoteGitPrefix;

	private final HttpGitWriter httpGitWriter;

	public GitLocal(IContainer container, HttpGitWriter gitWriter, String remoteGitPrefix, long period) {
		this.comtainer = container;
		this.httpGitWriter = gitWriter;
		this.remoteGitPrefix = remoteGitPrefix;
		this.period = period;
	}

	@Override
	public String getFileAsString(IFileDescription fileDescription) {
		String url = fileDescription.url();
		String message = "  " + url + "   " + getClass().getSimpleName() + ".getFileAsString(" + fileDescription + ")";
		logger.debug(message);
		pullIfNeeded(fileDescription);
		String result = comtainer.gitOperations().getFileAsString(fileDescription);
		logger.debug(message + " -> " + result);
		return result;
	}

	@Override
	public int countOfFileAsListsOfMap(IFileDescription fileDescription) {
		pullIfNeeded(fileDescription); // possible issue here, in that this and the various "get" methods may return a different result
		return IGitReader.Utils.countOfFileAsListsOfMap(comtainer, fileDescription);
	}

	@Override
	public Iterable<Map<String, Object>> getFileAsListOfMaps(IFileDescription fileDescription) {
		String url = fileDescription.url();
		String message = "  " + url + "   " + getClass().getSimpleName() + ".getFileAsListOfMaps(" + fileDescription + ")";
		logger.debug(message);
		pullIfNeeded(fileDescription);
		Iterable<Map<String, Object>> result = comtainer.gitOperations().getFileAsListOfMaps(fileDescription);
		logger.debug(message + " -> " + result);
		return result;
	}

	@Override
	public Map<String, Object> getFile(IFileDescription fileDescription) {
		String url = fileDescription.url();
		String message = "  " + url + "   " + getClass().getSimpleName() + ".getFile(" + fileDescription + ")";
		logger.debug(message);
		pullIfNeeded(fileDescription);
		Map<String, Object> result = comtainer.gitOperations().getFile(fileDescription);
		if (aboveRepoCache.containsKey(url))
			return aboveRepoCache.get(url);
		logger.debug(message + " -> " + result);
		return result;
	}

	@Override
	public Map<String, Object> getFileAndDescendants(IFileDescription fileDescription, int depth) {
		String url = fileDescription.url();
		logger.debug(getClass().getSimpleName() + ".getFileAndDescendants(" + fileDescription + ")");
		pullIfNeeded(fileDescription);
		if (aboveRepoCache.containsKey(url)) {
			Map<String, Object> result = aboveRepoCache.get(url);
			logger.debug(getClass().getSimpleName() + ".getFileAndDescendants_aboveRepoCache(" + fileDescription + ", " + result + ")");
			return result;
		} else {
			Map<String, Object> result = comtainer.gitOperations().getFileAndDescendants(fileDescription, depth);
			logger.debug(getClass().getSimpleName() + ".getFileAndDescendants_end(" + fileDescription + ")-> " + result);
			return result;
		}
	}

	private void pullIfNeeded(IFileDescription fileDescription) {
		final String url = fileDescription.url();
		if (aboveRepoCache.containsKey(url))
			return;
		synchronized (lock) {
			if (aboveRepoCache.containsKey(url))
				return;// not redundant
			IGitOperations gitOperations = comtainer.gitOperations();
			File root = gitOperations.getRoot();
			File repositoryUrl = fileDescription.findRepositoryUrl(root);
			final long now = System.currentTimeMillis();
			if (repositoryUrl == null) {
				logger.debug("        " + getClass().getSimpleName() + ".pullIfNeeded/clone(" + fileDescription + ")");
				comtainer.access(IRepoFinder.class, new ICallback<IRepoFinder>() {
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
				}).get();
			} else if (needToPull(repositoryUrl, now)) {
				logger.debug("        " + getClass().getSimpleName() + ".pullIfNeeded/pull(" + fileDescription + ")");
				String remoteUrl = Files.offset(root, repositoryUrl);
				gitOperations.pull(remoteUrl);
			} else
				logger.debug("        " + getClass().getSimpleName() + ".pullIfNeeded/notNeeded(" + fileDescription + ")");
		}
	}

	private void clone(final String remoteUrl, final long now) {
		final IGitOperations gitOperations = comtainer.gitOperations();
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
		final IGitOperations gitOperations = comtainer.gitOperations();
		return gitOperations.getRoot();
	}

	@Override
	public void clearCaches() {
		logger.debug(getClass().getSimpleName() + ".clearCaches");
		lastPulledTime.clear();
		comtainer.access(IRepoFinder.class, new ICallback<IRepoFinder>() {
			@Override
			public void process(IRepoFinder repoFinder) throws Exception {
				repoFinder.clearCaches();
			}
		}).get();
		aboveRepoCache.clear();
	}

	@Override
	public void clearCache(String url) {
		clearCaches();
	}

	@SuppressWarnings("ensure git local fully implements git writer")
	@Override
	public void append(IFileDescription fileDescription, Map<String, Object> data, String commitMessage) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int removeLine(IFileDescription fileDescription, IFunction1<Map<String, Object>, Boolean> acceptor, String commitMessage) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void init(String url, String commitMessage) {
		httpGitWriter.init(url, commitMessage);
	}

	@Override
	public void put(IFileDescription fileDescription, Map<String, Object> data, String commitMessage) {
		httpGitWriter.put(fileDescription, data, commitMessage);
		clearCaches();
	}

	@Override
	public void delete(IFileDescription fileDescription, String commitMessage) {
		httpGitWriter.delete(fileDescription, commitMessage);
		clearCaches();
	}

}