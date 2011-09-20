package org.softwareFm.eclipse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.data.GuiDataStore.DependantData;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.collections.Sets;
import org.softwareFm.utilities.functions.IFunction1;

public class ActivatorTest extends TestCase {

	private SoftwareFmActivator softwareFmActivator;

	public void testSetup() {
		assertNotNull(softwareFmActivator);
	}

	public void testGuiDataStore() {
		GuiDataStore store = softwareFmActivator.getGuiDataStore();
		assertSame(store, softwareFmActivator.getGuiDataStore());
		assertEquals("jar", store.getMainEntity());
		Map<String, List<DependantData>> entityToDependantMap = store.getEntityToDependantMap();
		assertEquals(Sets.makeSet("jar"), entityToDependantMap.keySet());
		assertEquals(Arrays.asList("project", "organisation"), Lists.map(entityToDependantMap.get("jar"), new IFunction1<DependantData	, String>() {
			@Override
			public String apply(DependantData from) throws Exception {
				return from.entity;
			}
		}));
		assertEquals(Sets.makeSet("urlGenerator.jar", "urlGenerator.project", "urlGenerator.organisation"), store.getUrlGeneratorMap().keySet());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		softwareFmActivator = SoftwareFmActivator.getDefault();
	}
}
