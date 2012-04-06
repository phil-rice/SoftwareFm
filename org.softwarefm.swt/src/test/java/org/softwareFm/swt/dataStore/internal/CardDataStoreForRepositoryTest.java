package org.softwareFm.swt.dataStore.internal;

import java.util.Map;

import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.crowdsource.utilities.url.Urls;
import org.softwareFm.eclipse.usage.internal.AbstractExplorerIntegrationTest;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.dataStore.ICardDataStoreCallback;
import org.softwareFm.swt.dataStore.IMutableCardDataStore;

public class CardDataStoreForRepositoryTest extends AbstractExplorerIntegrationTest {

	private IMutableCardDataStore cardDataStore;


	public void testProcessData() {
		postArtifactData();
		ICardDataStoreCallback<Map<String, Object>> callback = new ICardDataStoreCallback<Map<String, Object>>() {
			@Override
			public Map<String, Object> process(String url, Map<String, Object> result) throws Exception {
				return result;
			}

			@Override
			public Map<String, Object> noData(String url) throws Exception {
				fail();
				return null;
			}
		};
		String url = Urls.compose(rootArtifactUrl, artifactUrl);
		ITransaction<Map<String, Object>> transaction = cardDataStore.processDataFor(url, callback);
		Map<String, Object> result = transaction.get(CommonConstants.testTimeOutMs);
		dispatchUntilJobsFinished();
		assertEquals(Maps.stringObjectMap(//
				CommonConstants.typeTag, CardConstants.artifact, //
				"tutorial", Maps.stringObjectMap(CommonConstants.typeTag, CardConstants.collection, //
						"one", Maps.stringObjectMap(CommonConstants.typeTag, "tutorial"),//
						"two", Maps.stringObjectMap(CommonConstants.typeTag, "tutorial"))),//
				result);
	}

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		cardDataStore = cardConfig.cardDataStore;
	}

}
