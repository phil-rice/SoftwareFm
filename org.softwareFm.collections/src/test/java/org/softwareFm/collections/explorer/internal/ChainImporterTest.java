package org.softwareFm.collections.explorer.internal;

import junit.framework.TestCase;

import org.junit.Test;
import org.softwareFm.card.dataStore.CardDataStoreMock;

public class ChainImporterTest extends TestCase {

	private ChainImporter chainImporter;
	private CardDataStoreMock cardDataStore;

	@Test
	public void test() {
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardDataStore = new CardDataStoreMock();
		chainImporter = new ChainImporter(cardDataStore);
	}
}
