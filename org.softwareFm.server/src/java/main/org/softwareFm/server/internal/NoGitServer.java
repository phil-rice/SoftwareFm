package org.softwareFm.server.internal;

import java.io.File;
import java.util.Map;

import org.softwareFm.server.GetResult;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitServer;

public  class NoGitServer implements IGitServer {
	@Override
	public GetResult localGet(IFileDescription fileDescription) {
		throw new UnsupportedOperationException();
	}

	@Override
	public File getRoot() {
		throw new UnsupportedOperationException();
	}

	@Override
	public GetResult getFile(IFileDescription fileDescription) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(IFileDescription fileDescription) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void pull(String url) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void post(IFileDescription fileDescription, Map<String, Object> map) {
		throw new UnsupportedOperationException();
	}

	@Override
	public File findRepositoryUrl(String url) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void createRepository(String url) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clone(String url) {
		throw new UnsupportedOperationException();
	}
}