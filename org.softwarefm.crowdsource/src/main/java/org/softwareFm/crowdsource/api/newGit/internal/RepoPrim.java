package org.softwareFm.crowdsource.api.newGit.internal;

import java.io.File;
import java.util.Map;

import org.eclipse.jgit.storage.file.FileRepository;
import org.softwareFm.crowdsource.api.newGit.IRepoPrim;
import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.RepoLocation;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.transaction.ITransactional;

public class RepoPrim implements IRepoPrim, ITransactional {

	 final File root;
	 final Map<RepoLocation, FileRepository> repositories = Maps.newMap();

	public RepoPrim(File root) {
		this.root = root;
	}

	@Override
	public String[] read(ISingleSource singleSource) {
		return null;
	}

	@Override
	public void append(ISingleSource source, Map<String, Object> newItem) {
	}

	@Override
	public void change(ISingleSource source, int index, Map<String, Object> newLine) {
	}

	@Override
	public void delete(ISingleSource source, int index) {
	}

	@Override
	public void prepare(RepoLocation repoLocation) {
	}

	@Override
	public void commit(RepoLocation repoLocation) {
	}

	@Override
	public void commit() {
		releaseRepositories();
	}

	@Override
	public void rollback() {
	}

}
