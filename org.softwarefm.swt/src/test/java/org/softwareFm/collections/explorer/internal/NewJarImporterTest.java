package org.softwareFm.collections.explorer.internal;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.collections.explorer.internal.NewJarImporter.ImportStage;
import org.softwareFm.collections.explorer.internal.NewJarImporter.ImportStageCommand;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.callbacks.MemoryCallback;
import org.softwareFm.common.comparators.Comparators;
import org.softwareFm.common.future.Futures;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.IUrlGeneratorMap;
import org.softwareFm.common.url.UrlGenerator;
import org.softwareFm.display.swt.SwtTest;

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
		return new UrlGenerator("<" + string + ">/{2}/{4}/{6}", CollectionConstants.groupId, CollectionConstants.artifactId, CollectionConstants.version);// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version
	}

	private IUrlGenerator urlKeyForJarName(String string) {
		return new UrlGenerator("<" + string + ">/{2}", CollectionConstants.jarStem);
	}

	private IUrlGenerator urlKeyWithDigest(String string) {
		return new UrlGenerator("<" + string + ">/{2}/{4}/{6}/{8}", CollectionConstants.groupId, CollectionConstants.artifactId, CollectionConstants.version, CardConstants.digest);// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version, 8,9: digest
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
