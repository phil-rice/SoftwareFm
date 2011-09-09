package org.softwareFm.repository.api;

public enum RepositoryDataItemStatus {

	PATH_NULL, REQUESTED, NOT_FOUND, FOUND;

	public static class Utils {
		public static boolean isResults(RepositoryDataItemStatus status) {
			return status == NOT_FOUND || status == FOUND;
		}
	}
}
