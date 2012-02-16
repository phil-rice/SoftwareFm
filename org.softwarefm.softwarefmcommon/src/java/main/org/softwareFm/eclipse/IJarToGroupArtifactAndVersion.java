package org.softwareFm.eclipse;

import java.util.concurrent.Future;

public interface IJarToGroupArtifactAndVersion {

	Future<?> convertJarToGroupArtifactAndVersion(String jarDigest, IGroupArtifactVersionCallback callback);

	public static class Utils {

		public static IJarToGroupArtifactAndVersion expection() {
			return new IJarToGroupArtifactAndVersion() {

				@Override
				public Future<?> convertJarToGroupArtifactAndVersion(String jarDigest, IGroupArtifactVersionCallback callback) {
					throw new UnsupportedOperationException();
				}
			};
		}

	}

}
