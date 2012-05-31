package org.softwarefm.views;

import org.easymock.EasyMock;
import org.softwarefm.eclipse.SoftwareFmActivator;
import org.softwarefm.eclipse.SwtTest;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.eclipse.selection.ISelectedBindingStrategy;
import org.softwarefm.eclipse.selection.internal.SelectedArtifactSelectionManager;
import org.softwarefm.eclipse.selection.internal.SwtThreadSelectedBindingAggregator;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.callbacks.MemoryCallback;
import org.softwarefm.utilities.resources.IResourceGetter;

public abstract class AbstractSoftwareFmPanelTest<V extends SoftwareFmView<P>, P extends SoftwareFmPanel> extends SwtTest {

	protected V view;
	protected P panel;

	protected ISelectedBindingStrategy<String, String> strategy;
	protected SelectedArtifactSelectionManager<String, String> selectedArtifactSelectionManager;
	protected SwtThreadSelectedBindingAggregator<String> listenerManager;

	private MemoryCallback<Throwable> rememberedExceptions;

	protected final String digestUrl = "localhost/wiki/digest/0123456789";

	protected final FileNameAndDigest fileNameAndDigest = new FileNameAndDigest("fileName", "0123456789");
	protected final FileNameAndDigest fileNameAndNoDigest = new FileNameAndDigest("fileName", null);
	protected final FileNameAndDigest noFileNameAndNoDigest = new FileNameAndDigest(null, null);
	protected final ProjectData projectData = new ProjectData(fileNameAndDigest, "g", "a", "v");
	protected final String projectUrl = "localhost/wiki/project/g/a";

	abstract protected V makeView();

	public void testDisposeRemovesSelfAsListener() {
		assertEquals(1, listenerManager.getListeners().size());
		panel.dispose();
		assertEquals(0, listenerManager.getListeners().size());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		listenerManager = new SwtThreadSelectedBindingAggregator<String>(display);
		strategy = EasyMock.createMock(ISelectedBindingStrategy.class);
		EasyMock.makeThreadSafe(strategy, true);
		rememberedExceptions = ICallback.Utils.<Throwable> memory();
		selectedArtifactSelectionManager = new SelectedArtifactSelectionManager<String, String>(listenerManager, strategy, getExecutor(), rememberedExceptions);
		view = makeView();
		panel = view.makePanel(shell, new SoftwareFmContainer<String>(IResourceGetter.Utils.resourceGetter(SoftwareFmActivator.class, "text"), selectedArtifactSelectionManager));
	}

	@Override
	protected void tearDown() throws Exception {
		if (rememberedExceptions.getResults().size() > 0)
			for (Throwable e : rememberedExceptions.getResults())
				e.printStackTrace();
		assertEquals(0, rememberedExceptions.getResults().size());
		super.tearDown();

	}

}
