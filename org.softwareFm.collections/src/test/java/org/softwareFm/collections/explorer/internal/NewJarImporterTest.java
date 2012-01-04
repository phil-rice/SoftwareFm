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
import org.softwareFm.display.data.IUrlGenerator;
import org.softwareFm.display.data.IUrlGeneratorMap;
import org.softwareFm.display.swt.SwtTest;
import org.softwareFm.display.urlGenerator.UrlGenerator;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.callbacks.MemoryCallback;
import org.softwareFm.utilities.comparators.Comparators;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.maps.Maps;

public class NewJarImporterTest extends SwtTest {

	private NewJarImporter newJarImporter;
	private final Map<String, Object> data1 = Maps.stringObjectMap("data", 1);

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
				"MAKE_REPO:<jarName>/someGroupId/someArtifactId/someVersion:{}\n" + //
				"POST_DATA:<jarName>/someGroupId/someArtifactId/someVersion/"+randomUuid+":{artifact=null, group=null, sling:resourceType=jarname}\n"//
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
		CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(display).//
				withUrlGeneratorMap(IUrlGeneratorMap.Utils.urlGeneratorMap(//
						CardConstants.jarUrlKey, urlKeyWithDigest("jar"), //
						CardConstants.jarNameUrlKey, urlKey("jarName"), //
						CardConstants.groupUrlKey, urlKey("group"), //
						CardConstants.artifactUrlKey, urlKey("artifact"), //
						CardConstants.versionUrlKey, urlKey("version"), //
						CardConstants.digestUrlKey, urlKeyWithDigest("digest")));
		newJarImporter = new NewJarImporter(new ChainImporterMock(), cardConfig, "foundFromTest", "someDigest", "someGroupId", "someArtifactId", "someVersion");
	}

	private IUrlGenerator urlKey(String string) {
		return new UrlGenerator("<" + string + ">/{2}/{4}/{6}", CollectionConstants.groupId, CollectionConstants.artifactId, CollectionConstants.version);// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version
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
