package org.softwarefm.core.cache.internal;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.softwarefm.core.cache.IArtifactDataCache;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.selection.FileAndDigest;
import org.softwarefm.utilities.maps.Maps;

public class ArtifactDataCache implements IArtifactDataCache {
	private final Map<File, CachedArtifactData> cachedArtifactData = Maps.newMap(LinkedHashMap.class); // Linked to make writing tests easier
	private final Map<String, String> cachedCodeData = Maps.newMap(LinkedHashMap.class);
	private final Map<String, Map<String, String>> cachedMyCodeData = Maps.newMap(LinkedHashMap.class);
	private final Object lock = new Object();

	public void clearCaches() {
		synchronized (lock) {
			cachedArtifactData.clear();
			cachedCodeData.clear();
			cachedMyCodeData.clear();
		}
	}

	public CachedArtifactData projectData(File file) {
		synchronized (lock) {
			return cachedArtifactData.get(file);
		}
	}

	public void addProjectData(ArtifactData artifactData) {
		synchronized (lock) {
			File file = artifactData.fileAndDigest.file;
			Maps.putNoDuplicates(cachedArtifactData, file, CachedArtifactData.found(artifactData));
		}
	}

	public void addNotFound(FileAndDigest fileAndDigest) {
		synchronized (lock) {
			File file = fileAndDigest.file;
			Maps.putNoDuplicates(cachedArtifactData, file, CachedArtifactData.notFound(fileAndDigest));
		}

	}

	@Override
	public String codeHtml(String sfmId) {
		synchronized (lock) {
			return cachedCodeData.get(sfmId);
		}
	}

	@Override
	public void putCodeHtml(String sfmId, String codeHtml) {
		synchronized (lock) {
			Maps.putNoDuplicates(cachedCodeData, sfmId, codeHtml);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String myCodeHtml(String myName, String sfmId) {
		synchronized (lock) {
			return Maps.getOrNull((Map) cachedMyCodeData, myName, sfmId);
		}
	}

	@Override
	public void putMyCodeHtml(String myName, String sfmId, String codeHtml) {
		synchronized (lock) {
			Maps.put(cachedMyCodeData, myName, sfmId, codeHtml);
		}
	}
}