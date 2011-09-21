package org.softwareFm.eclipse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.data.GuiDataStore.DependantData;
import org.softwareFm.display.displayer.DisplayerStore;
import org.softwareFm.display.editor.EditorFactory;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.display.lists.ListEditorStore;
import org.softwareFm.display.smallButtons.SmallButtonStore;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.collections.Sets;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.tests.IDontRunAutomaticallyTest;

public class ActivatorTest extends TestCase implements IDontRunAutomaticallyTest{

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

	public void testResourceGetter() {
		IResourceGetter resourceGetter = softwareFmActivator.getResourceGetter();
		assertSame(resourceGetter, softwareFmActivator.getResourceGetter());
		assertEquals("Edit", resourceGetter.getStringOrNull("action.edit.tooltip"));

	}

	public void testGetCompositeConfig() {
		CompositeConfig config = softwareFmActivator.getCompositeConfig(display);
		assertSame(config, softwareFmActivator.getCompositeConfig(display));
		assertSame(config.resourceGetter, softwareFmActivator.getResourceGetter());
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

	public void testEditorStore() {
		EditorFactory store = (EditorFactory) softwareFmActivator.getEditorFactory(display);
		assertSame(store, softwareFmActivator.getEditorFactory(display));
		assertNotNull(store.get("editor.text"));
	}

	public void testActionStore() {
		ActionStore store = softwareFmActivator.getGuiBuilder().getActionStore();
		assertSame(store, softwareFmActivator.getGuiBuilder().getActionStore());
		assertNotNull(store.get("action.text.browse"));
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

	public void testGetLargeButtons() {
		List<LargeButtonDefn> largeButtons = softwareFmActivator.getLargeButtonDefns();
		assertSame(largeButtons, softwareFmActivator.getLargeButtonDefns());

		assertEquals(Arrays.asList("largeButton.jar", "largeButton.project", "largeButton.organisation"), Lists.map(largeButtons, new IFunction1<LargeButtonDefn, String>() {
			@Override
			public String apply(LargeButtonDefn from) throws Exception {
				return from.id;
			}
		}));
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
