package org.softwarefm.softwarefm;

import java.util.concurrent.ExecutorService;

import org.eclipse.jface.text.ITextSelection;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.SwtTest;
import org.softwarefm.eclipse.constants.MessageKeys;
import org.softwarefm.eclipse.selection.ISelectedBindingManager;
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
		assertEquals("Searching...", IResourceGetter.Utils.getOrException(resourceGetter, MessageKeys.search));
		activator.dispose();
		assertNotSame(resourceGetter, activator.getContainer().resourceGetter);
	}

	public void testSelectionBindingManager() {
		SoftwareFmContainer<ITextSelection> container = activator.getContainer();
		ISelectedBindingManager<ITextSelection> manager = container.selectedBindingManager;
		assertNotNull(manager);
		activator.dispose();
		assertNotSame(manager, activator.getContainer().selectedBindingManager);
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
