package org.softwareFm.eclipse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.data.GuiDataStore.DependantData;
import org.softwareFm.display.displayer.DisplayerStore;
import org.softwareFm.display.lists.ListEditorStore;
import org.softwareFm.display.smallButtons.SmallButtonStore;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.collections.Sets;
import org.softwareFm.utilities.functions.IFunction1;

public class ActivatorTest extends TestCase {

	private SoftwareFmActivator softwareFmActivator;
	private Shell shell;
	private Display display;

	public void testSetup() {
		assertNotNull(softwareFmActivator);
	}

	public void testGuiBuilder() {
		GuiBuilder guiBuilder = softwareFmActivator.getGuiBuilder();
		assertSame(guiBuilder, softwareFmActivator.getGuiBuilder());
	}

	public void testGetCompositeConfig() {
		CompositeConfig config = softwareFmActivator.getCompositeConfig(display);
		assertSame(config, softwareFmActivator.getCompositeConfig(display));

		assertNotNull(config.layout);
		assertNotNull(config.imageRegistry.get(ArtifactsAnchor.jarKey));
		assertNotNull(config.imageButtonConfig);
	}

	public void testSmallButtonStore() {
		SmallButtonStore store = softwareFmActivator.getGuiBuilder().getSmallButtonStore();
		assertSame(store, softwareFmActivator.getGuiBuilder().getSmallButtonStore());
		assertNotNull(store.get("smallButton.normal"));
	}

	public void testListEditorStore() {
		ListEditorStore store = softwareFmActivator.getGuiBuilder().getListEditorStore();
		assertSame(store, softwareFmActivator.getGuiBuilder().getListEditorStore());
		assertNotNull(store.get("listEditor.nameAndValue"));
	}

	public void testDisplayerStore() {
		DisplayerStore store = softwareFmActivator.getGuiBuilder().getDisplayerStore();
		assertSame(store, softwareFmActivator.getGuiBuilder().getDisplayerStore());
		assertNotNull(store.get("displayer.text"));
	}
	

	public void testGetGuiDataStore() {
		GuiDataStore dataStore = softwareFmActivator.getGuiDataStore();
		assertSame(dataStore, softwareFmActivator.getGuiDataStore());

		assertEquals("jar", dataStore.getMainEntity());
		Map<String, List<DependantData>> entityToDependantMap = dataStore.getEntityToDependantMap();
		assertEquals(Sets.makeSet("jar"), entityToDependantMap.keySet());
		assertEquals(Arrays.asList("project", "organisation"), Lists.map(entityToDependantMap.get("jar"), new IFunction1<DependantData, String>() {
			@Override
			public String apply(DependantData from) throws Exception {
				return from.entity;
			}
		}));
		assertEquals(Sets.makeSet("urlGenerator.jar", "urlGenerator.project", "urlGenerator.organisation"), dataStore.getUrlGeneratorMap().keySet());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		softwareFmActivator = SoftwareFmActivator.getDefault();
		shell = new Shell();
		display = shell.getDisplay();
	}

	@Override
	protected void tearDown() throws Exception {
		shell.dispose();

		super.tearDown();
	}
}
