package org.softwareFm.server.internal;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitOperations;
import org.softwareFm.server.IGitReader;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;

public class LocalReader implements IGitReader {

	private final String remotePrefix;
	private final Map<File, Long> lastPulledTime = Collections.synchronizedMap(Maps.<File, Long> newMap());

	private final IGitOperations gitOperations;
	private final IFunction1<String, String> findRepositoryRoot;

	private final long period;
	private final Object lock = new Object();

	public LocalReader(IFunction1<String, String> findRepositoryRoot, IGitOperations gitOperations, String remotePrefix, long period) {
		this.findRepositoryRoot = findRepositoryRoot;
		this.gitOperations = gitOperations;
		this.remotePrefix = remotePrefix;
		this.period = period;
	}

	@Override
	public void clearCaches() {
		lastPulledTime.clear();
	}

	@Override
	public Map<String, Object> getFile(IFileDescription fileDescription) {
		pullIfNeeded(fileDescription);
		return gitOperations.getFile(fileDescription);
	}

	@Override
	public Map<String, Object> getFileAndDescendants(IFileDescription fileDescription) {
		pullIfNeeded(fileDescription);
		return gitOperations.getFileAndDescendants(fileDescription);
	}

	private void pullIfNeeded(IFileDescription fileDescription) {
		synchronized (lock) {
			File repositoryUrl = fileDescription.findRepositoryUrl(gitOperations.getRoot());
			long now = System.currentTimeMillis();
			if (repositoryUrl == null) {
				String remoteUrl = Functions.call(findRepositoryRoot, fileDescription.url());
				clone(remoteUrl, now);
			} else if (needToPull(repositoryUrl, now)) {
				String remoteUrl = Files.offset(gitOperations.getRoot(), repositoryUrl);
				gitOperations.pull(remoteUrl);
			}
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
}
