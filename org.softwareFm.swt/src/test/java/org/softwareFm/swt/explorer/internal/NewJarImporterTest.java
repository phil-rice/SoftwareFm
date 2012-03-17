/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.explorer.internal;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.MemoryCallback;
import org.softwareFm.crowdsource.utilities.comparators.Comparators;
import org.softwareFm.crowdsource.utilities.future.Futures;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.crowdsource.utilities.url.IUrlGeneratorMap;
import org.softwareFm.crowdsource.utilities.url.UrlGenerator;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.constants.CollectionConstants;
import org.softwareFm.swt.explorer.internal.NewJarImporter.ImportStage;
import org.softwareFm.swt.explorer.internal.NewJarImporter.ImportStageCommand;
import org.softwareFm.swt.swt.SwtTest;

public class NewJarImporterTest extends SwtTest {

	private NewJarImporter newJarImporter;
	private final Map<String, Object> data1 = Maps.stringObjectMap("data", 1);
	private CardConfig cardConfig;

	public void testMap() {
		ImportStage stage = newJarImporter.map(CardConstants.jarUrlKey, data1);
		checkStage(stage, ImportStageCommand.POST_DATA, "<jar>/someGroupId/someArtifactId/someVersion/someDigest", data1);
	}

	public void testStage() {
		ImportStage stage = newJarImporter.stage(CardConstants.jarUrlKey, "data", 1);
		checkStage(stage, ImportStageCommand.POST_DATA, "<jar>/someGroupId/someArtifactId/someVersion/someDigest", data1);
	}

	public void testCollection() {
		ImportStage stage = newJarImporter.collection(CardConstants.jarUrlKey, "collectionName");
		checkStage(stage, ImportStageCommand.POST_DATA, "<jar>/someGroupId/someArtifactId/someVersion/someDigest/collectionName", Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.collection));
	}

	public void testMakeRepo() {
		ImportStage stage = newJarImporter.makeRepo(CardConstants.jarUrlKey);
		checkStage(stage, ImportStageCommand.MAKE_REPO, "<jar>/someGroupId/someArtifactId/someVersion/someDigest", Maps.emptyStringObjectMap());
	}

	public void testProcess() throws InterruptedException, ExecutionException, TimeoutException {
		MemoryCallback<String> afterOk = ICallback.Utils.memory();
		newJarImporter.process(afterOk).get(2, TimeUnit.SECONDS);
		assertEquals("<artifact>/someGroupId/someArtifactId/someVersion", afterOk.getOnlyResult());
	}

	public void testProcessImport() throws InterruptedException, ExecutionException, TimeoutException {
		MemoryCallback<String> afterOk = ICallback.Utils.memory();
		String randomUuid = UUID.randomUUID().toString();
		Object actual = newJarImporter.processImport(randomUuid, afterOk).get(2, TimeUnit.SECONDS);
		assertEquals("<artifact>/someGroupId/someArtifactId/someVersion", afterOk.getOnlyResult());
		assertEquals("MAKE_REPO:<jar>/someGroupId/someArtifactId/someVersion/someDigest:{}\n" + //
				"POST_DATA:<jar>/someGroupId/someArtifactId/someVersion/someDigest:{artifactId=someArtifactId, groupId=someGroupId, version=someVersion}\n" + //
				"MAKE_REPO:<artifact>/someGroupId/someArtifactId/someVersion:{}\n" + //
				"POST_DATA:<artifact>/someGroupId/someArtifactId/someVersion:{sling:resourceType=artifact}\n" + //
				"POST_DATA:<artifact>/someGroupId/someArtifactId/someVersion/version:{sling:resourceType=collection}\n" + //
				"POST_DATA:<version>/someGroupId/someArtifactId/someVersion:{artifactId=someArtifactId, groupId=someGroupId, sling:resourceType=version, version=someVersion}\n" + //
				"POST_DATA:<version>/someGroupId/someArtifactId/someVersion/digest:{sling:resourceType=collection}\n" + //
				"POST_DATA:<digest>/someGroupId/someArtifactId/someVersion/someDigest:{digest=someDigest, found=foundFromTest, sling:resourceType=jar}\n" + //
				"MAKE_REPO:<jarName>/someJarStem:{}\n" + //
				"POST_DATA:<jarName>/someJarStem:{sling:resourceType=collection}\n" +//
				"POST_DATA:<jarName>/someJarStem/" + randomUuid + ":{artifactId=someArtifactId, group=someGroupId, sling:resourceType=jarname}\n"//
		, actual);

	}

	private void checkStage(ImportStage stage, NewJarImporter.ImportStageCommand command, String url, Map<String, Object> data) {
		assertEquals(url, stage.url);
		assertEquals(data, stage.data);
		assertEquals(command, stage.command);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardConfig = CardDataStoreFixture.syncCardConfig(display).//
				withUrlGeneratorMap(IUrlGeneratorMap.Utils.urlGeneratorMap(//
						CardConstants.jarUrlKey, urlKeyWithDigest("jar"), //
						CardConstants.jarNameUrlKey, urlKeyForJarName("jarName"), //
						CardConstants.groupUrlKey, urlKey("group"), //
						CardConstants.artifactUrlKey, urlKey("artifact"), //
						CardConstants.versionUrlKey, urlKey("version"), //
						CardConstants.digestUrlKey, urlKeyWithDigest("digest")));
		newJarImporter = new NewJarImporter(new ChainImporterMock(), cardConfig, "foundFromTest", "someDigest", "someGroupId", "someArtifactId", "someVersion", "someJarStem");
	}
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		cardConfig.dispose();
	}

	private IUrlGenerator urlKey(String string) {
		return new UrlGenerator("<" + string + ">/{2}/{4}/{6}", SoftwareFmConstants.groupId, SoftwareFmConstants.artifactId, SoftwareFmConstants.version);// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version
	}

	private IUrlGenerator urlKeyForJarName(String string) {
		return new UrlGenerator("<" + string + ">/{2}", CollectionConstants.jarStem);
	}

	private IUrlGenerator urlKeyWithDigest(String string) {
		return new UrlGenerator("<" + string + ">/{2}/{4}/{6}/{8}", SoftwareFmConstants.groupId, SoftwareFmConstants.artifactId, SoftwareFmConstants.version, SoftwareFmConstants.digest);// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version, 8,9: digest
	}

	static class ChainImporterMock implements IChainImporter {

		@Override
		public Future<?> process(Runnable afterOk, ImportStage... stages) {
			StringBuilder builder = new StringBuilder();
			for (ImportStage stage : stages)
				builder.append(stage.command + ":" + stage.url + ":" + Maps.sortByKey(stage.data, Comparators.<String> naturalOrder()) + "\n");
			Future<?> result = Futures.doneFuture(builder.toString());
			afterOk.run();
			return result;
		}

	}

}