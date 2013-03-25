package org.softwarefm.core.cache;

import java.io.File;

import org.softwarefm.core.cache.internal.ArtifactDataCache;
import org.softwarefm.core.cache.internal.CachedArtifactData;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.selection.FileAndDigest;
import org.softwarefm.utilities.cache.IHasCache;

public interface IArtifactDataCache extends IHasCache {

	void addNotFound(FileAndDigest fileAndDigest);

	void addProjectData(ArtifactData artifactData);

	CachedArtifactData projectData(File file);

	String codeHtml(String sfmId);

	String myCodeHtml(String myName, String sfmId);

	void putCodeHtml(String sfmId, String codeHtml);

	void putMyCodeHtml(String myName, String sfmId, String codeHtml);

	public static class Utils {
		public static IArtifactDataCache artifactDataCache() {
			return new ArtifactDataCache();
		}

		public static IArtifactDataCache noCache() {
			return new IArtifactDataCache() {

				public void clearCaches() {
				}

				public CachedArtifactData projectData(File file) {
					return null;
				}

				public void addProjectData(ArtifactData artifactData) {
				}

				public void addNotFound(FileAndDigest fileAndDigest) {
				}

				@Override
				public String codeHtml(String sfmId) {
					return null;
				}

				@Override
				public void putCodeHtml(String sfmId, String codeHtml) {
				}

				@Override
				public String myCodeHtml(String myName, String sfmId) {
					return null;
				}

				@Override
				public void putMyCodeHtml(String myName, String sfmId, String codeHtml) {
				}
			};
		}
	}

}
