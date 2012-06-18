package org.softwarefm.eclipse.composite;

import java.io.File;

import org.easymock.EasyMock;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.cache.IProjectDataCache;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileAndDigest;
import org.softwarefm.eclipse.selection.ISelectedBindingStrategy;
import org.softwarefm.eclipse.selection.internal.SelectedArtifactSelectionManager;
import org.softwarefm.eclipse.selection.internal.SwtThreadSelectedBindingAggregator;
import org.softwarefm.eclipse.templates.ITemplateStore;
import org.softwarefm.eclipse.tests.SwtTest;
import org.softwarefm.eclipse.url.IUrlStrategy;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.callbacks.ICallback2;
import org.softwarefm.utilities.callbacks.MemoryCallback;
import org.softwarefm.utilities.constants.CommonConstants;
import org.softwarefm.utilities.strings.Strings;

public abstract class AbstractSoftwareFmCompositeTest<P extends SoftwareFmComposite> extends SwtTest {

	protected P panel;

	protected ISelectedBindingStrategy<String, String> strategy;
	protected SelectedArtifactSelectionManager<String, String> selectedArtifactSelectionManager;
	protected SwtThreadSelectedBindingAggregator<String> listenerManager;

	private MemoryCallback<Throwable> rememberedExceptions;

	protected final static String classAndMethodNameUrl = Strings.url(CommonConstants.softwareFmHostAndPrefix, "java/packageName/className");
	protected final static String digestUrl = Strings.url(CommonConstants.softwareFmHostAndPrefix, "Digest:0123456789");
	protected final static String projectUrl = Strings.url(CommonConstants.softwareFmHostAndPrefix, "project/g/a");
	protected final static File file = new File(new File("some"), "file");
	protected final static FileAndDigest fileAndDigest = new FileAndDigest(file, "0123456789");
	protected final static FileAndDigest fileNameAndNoDigest = new FileAndDigest(file, null);
	protected final static FileAndDigest noFileNameAndNoDigest = new FileAndDigest(null, null);
	protected final static ProjectData projectData = new ProjectData(fileAndDigest, "g", "a", "v");
	protected final static ExpressionData classExpressionData = new ExpressionData("somePackage", "someClass");
	protected final static ExpressionData classAndMethodExpressionData = new ExpressionData("somePackage", "someClass", "someMethod");

	private int initialListeners;

	abstract protected P makePanel(Composite parent, SoftwareFmContainer<?> container);

	public void testDisposeRemovesSelfAsListener() {
		panel.dispose();
		assertEquals(listenerManager.getListeners().toString(), initialListeners, listenerManager.getListeners().size());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		listenerManager = new SwtThreadSelectedBindingAggregator<String>(display);
		strategy = EasyMock.createMock(ISelectedBindingStrategy.class);
		EasyMock.makeThreadSafe(strategy, true);
		rememberedExceptions = ICallback.Utils.<Throwable> memory();
		selectedArtifactSelectionManager = new SelectedArtifactSelectionManager<String, String>(listenerManager, strategy, getExecutor(), IProjectDataCache.Utils.projectDataCache(), rememberedExceptions);
		IUrlStrategy urlStrategy = IUrlStrategy.Utils.urlStrategy();
		initialListeners = listenerManager.getListeners().size();
		assertEquals(0, initialListeners);
		panel = makePanel(shell, SoftwareFmContainer.make(urlStrategy, //
				selectedArtifactSelectionManager,//
				ICallback2.Utils.<String, Integer> exception("ImportPom shouldnt be used"),//
				ICallback2.Utils.<ProjectData, Integer> exception("ImportPom shouldnt be used"),//
				ITemplateStore.Utils.templateStore(urlStrategy), //
				IProjectDataCache.Utils.projectDataCache()));
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
