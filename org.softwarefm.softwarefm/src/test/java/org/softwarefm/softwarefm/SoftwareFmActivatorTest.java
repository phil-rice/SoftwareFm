package org.softwarefm.softwarefm;

import java.util.concurrent.ExecutorService;

import org.eclipse.jface.text.ITextSelection;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.actions.SfmActionState;
import org.softwarefm.eclipse.cache.IArtifactDataCache;
import org.softwarefm.eclipse.constants.TextKeys;
import org.softwarefm.eclipse.jdtBinding.ArtifactData;
import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.eclipse.tests.SwtTest;
import org.softwarefm.eclipse.url.HostOffsetAndUrl;
import org.softwarefm.eclipse.url.IUrlStrategy;
import org.softwarefm.utilities.constants.CommonConstants;
import org.softwarefm.utilities.resources.IResourceGetter;

public class SoftwareFmActivatorTest extends SwtTest {

	private SoftwareFmActivator activator;

	public void testContainer() {
		SoftwareFmContainer<ITextSelection> container = activator.getContainer();
		SoftwareFmContainer<ITextSelection> container1 = activator.getContainer();
		assertSame(container, container1);
		activator.dispose();
		assertNotSame(container, activator.getContainer());
	}

	public void testResourceGetter() {
		SoftwareFmContainer<ITextSelection> container = activator.getContainer();
		IResourceGetter resourceGetter = container.resourceGetter;
		assertEquals("zeroandone", IResourceGetter.Utils.getMessageOrException(resourceGetter, TextKeys.msgTestForTest, "zero", "one"));
		activator.dispose();
		assertNotSame(resourceGetter, activator.getContainer().resourceGetter);
	}
	
	public void testActionState(){
		SfmActionState state1 = activator.getActionState();
		SfmActionState state2 = activator.getActionState();
		assertSame(state1, state2);
		IUrlStrategy urlStrategy = activator.getContainer().urlStrategy;
		state1.setUrlSuffix("someSuffix");
		assertEquals(new HostOffsetAndUrl(CommonConstants.softwareFmHost, CommonConstants.softwareFmPageOffset, "project", "g", "a#someSuffix"), urlStrategy.projectUrl(new ArtifactData(null, "g", "a", "v")));

		activator.dispose();
		
		SfmActionState state1a = activator.getActionState();
		SfmActionState state2a = activator.getActionState();
		assertSame(state1a, state2a);
		assertNotSame(state1, state1a);
		
	}
	
	public void testProjectDataCache(){
		IArtifactDataCache cache1 = activator.getArtifactDataCache();
		IArtifactDataCache cache2 = activator.getArtifactDataCache();
		SoftwareFmContainer<ITextSelection> container = activator.getContainer();
		assertSame(cache1, cache2);
		assertSame(cache1, container.artifactDataCache);
		activator.dispose();
		
		IArtifactDataCache cache1b = activator.getArtifactDataCache();
		IArtifactDataCache cache2b = activator.getArtifactDataCache();
		SoftwareFmContainer<ITextSelection> containerb = activator.getContainer();
		assertSame(cache1b, cache2b);
		assertSame(cache1b, containerb.artifactDataCache);
		assertNotSame(cache1, cache1b);
		
	}

	public void testSelectionBindingManager() {
		SoftwareFmContainer<ITextSelection> container = activator.getContainer();
		ISelectedBindingManager<ITextSelection> manager = container.selectedBindingManager;
		assertNotNull(manager);
		activator.dispose();
		assertNotSame(manager, activator.getContainer().selectedBindingManager);
	}

	public void testGetUrlStrategy(){
		SoftwareFmContainer<ITextSelection> container1 = activator.getContainer();
		SoftwareFmContainer<ITextSelection> container2 = activator.getContainer();
		assertSame(container1.urlStrategy, container2.urlStrategy);
		assertNotNull(container1.urlStrategy);
		activator.dispose();
		assertNotSame(activator.getContainer().urlStrategy, container1.urlStrategy);
	}
	
	public void testExecutorService() {
		ExecutorService service1 = activator.getExecutorService();
		ExecutorService service2 = activator.getExecutorService();
		assertSame(service1, service2);
		assertFalse(service1.isShutdown());
		activator.dispose();
		ExecutorService service3 = activator.getExecutorService();
		assertNotSame(service1, service3);
		assertTrue(service1.isShutdown());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activator = SoftwareFmActivator.makeActivatorForTests(display);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		activator.dispose();
	}
}
