package org.softwarefm.eclipse;

import java.util.concurrent.ExecutorService;

import org.eclipse.jface.text.ITextSelection;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.actions.SfmActionState;
import org.softwarefm.core.cache.IArtifactDataCache;
import org.softwarefm.core.constants.TextKeys;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.selection.ISelectedBindingManager;
import org.softwarefm.core.tests.SwtTest;
import org.softwarefm.core.url.HostOffsetAndUrl;
import org.softwarefm.core.url.IUrlStrategy;
import org.softwarefm.eclipse.SoftwareFmPlugin;
import org.softwarefm.shared.usage.IUsage;
import org.softwarefm.utilities.constants.CommonConstants;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.resources.IResourceGetter;

public class SoftwareFmPlugInTest extends SwtTest {

	private SoftwareFmPlugin plugin;

	public void testContainer() {
		SoftwareFmContainer<ITextSelection> container = plugin.getContainer();
		SoftwareFmContainer<ITextSelection> container1 = plugin.getContainer();
		assertSame(container, container1);
		plugin.dispose();
		assertNotSame(container, plugin.getContainer());
	}

	public void testResourceGetter() {
		SoftwareFmContainer<ITextSelection> container = plugin.getContainer();
		IResourceGetter resourceGetter = container.resourceGetter;
		assertEquals("zeroandone", IResourceGetter.Utils.getMessageOrException(resourceGetter, TextKeys.msgTestForTest, "zero", "one"));
		plugin.dispose();
		assertNotSame(resourceGetter, plugin.getContainer().resourceGetter);
	}

	public void testActionState() {
		SfmActionState state1 = plugin.getActionState();
		SfmActionState state2 = plugin.getActionState();
		assertSame(state1, state2);
		IUrlStrategy urlStrategy = plugin.getContainer().urlStrategy;
		state1.setUrlSuffix("someSuffix");
		assertEquals(new HostOffsetAndUrl(CommonConstants.softwareFmHost, CommonConstants.softwareFmPageOffset, "project", "g", "a#someSuffix"), urlStrategy.projectUrl(new ArtifactData(null, "g", "a", "v")));

		plugin.dispose();

		SfmActionState state1a = plugin.getActionState();
		SfmActionState state2a = plugin.getActionState();
		assertSame(state1a, state2a);
		assertNotSame(state1, state1a);

	}

	public void testProjectDataCache() {
		IArtifactDataCache cache1 = plugin.getArtifactDataCache();
		IArtifactDataCache cache2 = plugin.getArtifactDataCache();
		SoftwareFmContainer<ITextSelection> container = plugin.getContainer();
		assertSame(cache1, cache2);
		assertSame(cache1, container.artifactDataCache);
		plugin.dispose();

		IArtifactDataCache cache1b = plugin.getArtifactDataCache();
		IArtifactDataCache cache2b = plugin.getArtifactDataCache();
		SoftwareFmContainer<ITextSelection> containerb = plugin.getContainer();
		assertSame(cache1b, cache2b);
		assertSame(cache1b, containerb.artifactDataCache);
		assertNotSame(cache1, cache1b);

	}

	public void testUsage() {
		IUsage usage1 = plugin.getUsage();
		IUsage usage2 = plugin.getUsage();
		assertSame(usage1, usage2);
		plugin.dispose();
		IUsage usage3 = plugin.getUsage();
		assertNotSame(usage1, usage3);
	}

	public void testGetMultipleListenerList() {
		IMultipleListenerList list1 = plugin.getMultipleListenerList();
		IMultipleListenerList list2 = plugin.getMultipleListenerList();
		assertSame(list1, list2);
		plugin.dispose();
		IMultipleListenerList list3 = plugin.getMultipleListenerList();
		assertNotSame(list1, list3);
	}

	public void testSelectionBindingManager() {
		SoftwareFmContainer<ITextSelection> container = plugin.getContainer();
		ISelectedBindingManager<ITextSelection> manager = container.selectedBindingManager;
		assertNotNull(manager);
		plugin.dispose();
		assertNotSame(manager, plugin.getContainer().selectedBindingManager);
	}

	public void testGetUrlStrategy() {
		SoftwareFmContainer<ITextSelection> container1 = plugin.getContainer();
		SoftwareFmContainer<ITextSelection> container2 = plugin.getContainer();
		assertSame(container1.urlStrategy, container2.urlStrategy);
		assertNotNull(container1.urlStrategy);
		plugin.dispose();
		assertNotSame(plugin.getContainer().urlStrategy, container1.urlStrategy);
	}

	public void testExecutorService() {
		ExecutorService service1 = plugin.getExecutorService();
		ExecutorService service2 = plugin.getExecutorService();
		assertSame(service1, service2);
		assertFalse(service1.isShutdown());
		plugin.dispose();
		ExecutorService service3 = plugin.getExecutorService();
		assertNotSame(service1, service3);
		assertTrue(service1.isShutdown());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		plugin = SoftwareFmPlugin.makeActivatorForTests(display);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		plugin.dispose();
	}
}
