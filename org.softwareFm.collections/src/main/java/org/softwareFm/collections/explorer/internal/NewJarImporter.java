/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.explorer.internal;

import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.IAfterEditCallback;
import org.softwareFm.card.dataStore.IMutableCardDataStore;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.display.data.IUrlGenerator;
import org.softwareFm.display.data.IUrlGeneratorMap;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.maps.Maps;

public class NewJarImporter {

	private final ChainImporter importer;
	private final Map<String, Object> groupIdArtifactIdVersionMap;
	private final String groupId;
	private final IUrlGeneratorMap map;
	private final String digest;
	private final String found;
	private final Map<String, Object> groupIdArtifactIdVersionDigestMap;

	static class ChainImporter {

		private final CardConfig cardConfig;

		public ChainImporter(CardConfig cardConfig) {
			this.cardConfig = cardConfig;
		}

		public Future<?> process(Runnable afterOk, ImportStage... stages) {
			return process(afterOk, 0, stages);
		}

		private Future<?> process(final Runnable afterOk, final int index, final ImportStage[] stages) {
			if (index >= stages.length) {
				afterOk.run();
				return Futures.doneFuture(null);
			}
			ImportStage stage = stages[index];
			IMutableCardDataStore cardDataStore = (IMutableCardDataStore) cardConfig.cardDataStore;
			System.out.println("Putting: " + stage.url + " " + stage.data);
			if (stage.url == null)
				return process(afterOk, index + 1, stages);
			else
				return cardDataStore.put(stage.url, stage.data, new IAfterEditCallback() {
					@Override
					public void afterEdit(String url) {
						process(afterOk, index + 1, stages);
					}
				});
		}

	}

	static class ImportStage {
		String url;
		Map<String, Object> data;

		public ImportStage(String url, Map<String, Object> data) {
			this.url = url;
			this.data = data;
		}

	}

	public NewJarImporter(CardConfig cardConfig, String found, String digest, String groupId, String artifactId, String version) {
		this.found = found;
		this.digest = digest;
		this.groupId = groupId;
		map = cardConfig.urlGeneratorMap;
		importer = new ChainImporter(cardConfig);
		groupIdArtifactIdVersionMap = Maps.stringObjectMap(CollectionConstants.groupId, groupId, CollectionConstants.artifactId, artifactId, CollectionConstants.version, version);
		groupIdArtifactIdVersionDigestMap = Maps.with(groupIdArtifactIdVersionMap, CardConstants.digest, digest);
	}

	private ImportStage stage(String urlKey, Object... namesAndValues) {
		String url = map.get(urlKey).findUrlFor(groupIdArtifactIdVersionDigestMap);
		return new ImportStage(url, Maps.stringObjectMap(namesAndValues));
	}

	private ImportStage map(String urlKey, Map<String, Object> value) {
		IUrlGenerator iUrlGenerator = map.get(urlKey);
		String url = iUrlGenerator.findUrlFor(groupIdArtifactIdVersionDigestMap);
		return new ImportStage(url, value);
	}

	private ImportStage collection(String urlKey, String collectionName) {
		IUrlGenerator generator = map.get(urlKey);
		String baseUrl = generator.findUrlFor(groupIdArtifactIdVersionDigestMap);
		return new ImportStage(baseUrl + "/" + collectionName, Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.collection));
	}

	public Future<?> process(final ICallback<String> afterOk) {
		return importer.process(new Runnable() {
			@Override
			public void run() {
				String artifactUrl = map.get(CardConstants.artifactUrlKey).findUrlFor(groupIdArtifactIdVersionMap);
				ICallback.Utils.call(afterOk, artifactUrl);
			}
		},//
				map(CardConstants.jarUrlKey, Maps.with(groupIdArtifactIdVersionMap, CardConstants.slingResourceType, CardConstants.collection)), //
				stage(CardConstants.urlGroupKey, CollectionConstants.groupId, groupId, CardConstants.slingResourceType, CardConstants.group),//
				collection(CardConstants.urlGroupKey, CardConstants.artifact), //
				stage(CardConstants.artifactUrlKey, CardConstants.slingResourceType, CardConstants.artifact),//
				collection(CardConstants.artifactUrlKey, CardConstants.version), //
				map(CardConstants.versionUrlKey, Maps.with(groupIdArtifactIdVersionMap, CardConstants.slingResourceType, CardConstants.version)), //
				collection(CardConstants.versionUrlKey, CardConstants.digest), //
				stage(CardConstants.digestUrlKey, CardConstants.slingResourceType, CardConstants.versionJar, CardConstants.digest, digest, CardConstants.found, found));
	}
}