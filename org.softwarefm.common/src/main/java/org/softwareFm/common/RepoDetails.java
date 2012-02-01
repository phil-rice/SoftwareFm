package org.softwareFm.common;

import java.util.Map;

public class RepoDetails {

	public static RepoDetails repositoryUrl(String repositoryUrl) {
		if (repositoryUrl == null)
			throw new IllegalArgumentException();
		return new RepoDetails(null, repositoryUrl);
	}

	public static RepoDetails aboveRepo(Map<String, Object> map) {
		if (map == null)
			throw new IllegalArgumentException();
		return new RepoDetails(map, null);
	}

	private final Map<String, Object> data;
	private final String repositoryUrl;

	private RepoDetails(Map<String, Object> data, String repositoryUrl) {
		this.data = data;
		this.repositoryUrl = repositoryUrl;
		assert data == null || repositoryUrl == null;
		assert !(data == null && repositoryUrl == null);
	}

	public boolean aboveRepository() {
		return data != null;
	}

	public Map<String, Object> getAboveRepositoryData() {
		if (data == null)
			throw new IllegalStateException();
		return data;
	}

	public String getRepositoryUrl() {
		if (repositoryUrl == null)
			throw new IllegalStateException();
		return repositoryUrl;
	}

}
