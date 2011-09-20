package org.softwareFm.eclipse;

import junit.framework.TestCase;

import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.utilities.collections.Sets;

public class ActivatorTest extends TestCase {

	private Activator activator;

	public void testSetup() {
		assertNotNull(activator);
	}

	public void testGuiDataStore() {
		GuiDataStore store = activator.getGuiDataStore();
		assertSame(store, activator.getGuiDataStore());
		assertEquals("jar", store.getMainEntity());
		assertEquals(Sets.makeSet("jar", "project", "organisation"), store.getEntityToDependantMap().keySet());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activator = Activator.getDefault();
	}
}
