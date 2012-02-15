package org.softwareFm.eclipse;

import java.util.concurrent.Future;

public interface IJarToGroupArtifactAndVersion {
	
	Future<?> convertJarToGroupArtifactAndVersion(String jarDigest, IGroupArtifactVersionCallback callback);

}
