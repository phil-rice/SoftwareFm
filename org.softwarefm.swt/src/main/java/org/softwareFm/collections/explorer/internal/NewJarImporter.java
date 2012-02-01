/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.explorer.internal;

import java.text.MessageFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.UtilityConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.IUrlGeneratorMap;
import org.softwareFm.common.url.Urls;
import org.softwareFm.swt.configuration.CardConfig;

public class NewJarImporter {

	private final IChainImporter importer;
	private final Map<String, Object> groupIdArtifactIdVersionMap;
	@SuppressWarnings("unused")
	private final String groupId;
	private final IUrlGeneratorMap map;
	private final String digest;
	private final String found;
	private final Map<String, Object> groupIdArtifactIdVersionDigestJarStemMap;

	static enum ImportStageCommand {
		POST_DATA, MAKE_REPO;
	}

	static class ImportStage {
		String url;
		Map<String, Object> data;
		ImportStageCommand command;

		public ImportStage(String url, Map<String, Object> data, ImportStageCommand command) {
			this.url = url;
			this.data = data;
			this.command = command;
		}

	}

	public NewJarImporter(CardConfig cardConfig, String found, String digest, String groupId, String artifactId, String version, String jarStem) {
		this(new ChainImporter(cardConfig.cardDataStore), cardConfig, found, digest, groupId, artifactId, version, jarStem);
	}

	public NewJarImporter(IChainImporter chainImporter, CardConfig cardConfig, String found, String digest, String groupId, String artifactId, String version, String jarStem) {
		this.found = found;
		this.digest = digest;
		this.groupId = groupId;
		this.map = cardConfig.urlGeneratorMap;
		this.importer = chainImporter;
		this.groupIdArtifactIdVersionMap = Maps.stringObjectMap(CollectionConstants.groupId, groupId, CollectionConstants.artifactId, artifactId, CollectionConstants.version, version);
		this.groupIdArtifactIdVersionDigestJarStemMap = Maps.with(groupIdArtifactIdVersionMap, CardConstants.digest, digest, CollectionConstants.jarStem, jarStem);
	}

	public ImportStage stage(String urlKey, Object... namesAndValues) {
		return map(urlKey, Maps.stringObjectMap(namesAndValues));
	}

	public ImportStage map(String urlKey, Map<String, Object> value) {
		String url = getUrl(urlKey);
		return new ImportStage(url, value, ImportStageCommand.POST_DATA);
	}

	public ImportStage mapWithOffset(String urlKey, String offset, Map<String, Object> value) {
		String url = Urls.compose(getUrl(urlKey), offset);
		return new ImportStage(url, value, ImportStageCommand.POST_DATA);
	}

	private String getUrl(String urlKey) {
		IUrlGenerator urlGenerator = map.get(urlKey);
		if (urlGenerator == null)
			throw new IllegalArgumentException(MessageFormat.format(UtilityConstants.mapDoesntHaveKey, urlKey, Lists.sort(map.keys()), map));
		String url = urlGenerator.findUrlFor(groupIdArtifactIdVersionDigestJarStemMap);
		return url;
	}

	public ImportStage collection(String urlKey, String collectionName) {
		String baseUrl = getUrl(urlKey);
		return new ImportStage(baseUrl + "/" + collectionName, Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.collection), ImportStageCommand.POST_DATA);
	}

	public ImportStage makeRepo(String urlKey) {
		String baseUrl = getUrl(urlKey);
		return new ImportStage(baseUrl, Maps.emptyStringObjectMap(), ImportStageCommand.MAKE_REPO);
	}

	public Future<?> process(final ICallback<String> afterOk, ImportStage... stages) {
		return importer.process(new Runnable() {
			@Override
			public void run() {
				String artifactUrl = map.get(CardConstants.artifactUrlKey).findUrlFor(groupIdArtifactIdVersionMap);
				ICallback.Utils.call(afterOk, artifactUrl);
			}
		}, stages);
	}

	public Future<?> processImport( final ICallback<String> afterOk) {
		return processImport(UUID.randomUUID().toString(), afterOk);
		
	}
	public Future<?> processImport(String jarNameUuid, final ICallback<String> afterOk) {
		return process(afterOk,//
				
				makeRepo(CardConstants.jarUrlKey),//
				map(CardConstants.jarUrlKey, groupIdArtifactIdVersionMap), //

				makeRepo(CardConstants.artifactUrlKey),//
				stage(CardConstants.artifactUrlKey, CardConstants.slingResourceType, CardConstants.artifact),//
				collection(CardConstants.artifactUrlKey, CardConstants.version), //
				map(CardConstants.versionUrlKey, Maps.with(groupIdArtifactIdVersionMap, CardConstants.slingResourceType, CardConstants.version)), //
				collection(CardConstants.versionUrlKey, CardConstants.digest), //
				stage(CardConstants.digestUrlKey, CardConstants.slingResourceType, CardConstants.versionJar, CardConstants.digest, digest, CardConstants.found, found),//

				makeRepo(CardConstants.jarNameUrlKey),//
				map(CardConstants.jarNameUrlKey, Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.collection)), //
				mapWithOffset(CardConstants.jarNameUrlKey, jarNameUuid, Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.jarName, CollectionConstants.artifactId, groupIdArtifactIdVersionMap.get(CollectionConstants.artifactId), CardConstants.group, groupIdArtifactIdVersionMap.get(CollectionConstants.groupId))) //
		);
	}

}