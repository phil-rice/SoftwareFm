package org.softwarefm.eclipse.composite;

import java.io.File;

import org.easymock.EasyMock;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.actions.SfmActionState;
import org.softwarefm.eclipse.cache.IArtifactDataCache;
import org.softwarefm.eclipse.jdtBinding.CodeData;
import org.softwarefm.eclipse.jdtBinding.ArtifactData;
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

	protected final static String classAndMethodNameUrl = Strings.url(CommonConstants.softwareFmHostAndPrefix, "code:packageName/className");
	protected final static String digestUrl = Strings.url(CommonConstants.softwareFmHostAndPrefix, "Digest:0123456789");
	protected final static String projectUrl = Strings.url(CommonConstants.softwareFmHostAndPrefix, "project/g/a");
	protected final static File file = new File(new File("some"), "file");
	protected final static FileAndDigest fileAndDigest = new FileAndDigest(file, "0123456789");
	protected final static FileAndDigest fileNameAndNoDigest = new FileAndDigest(file, null);
	protected final static FileAndDigest noFileNameAndNoDigest = new FileAndDigest(null, null);
	protected final static ArtifactData artifactData = new ArtifactData(fileAndDigest, "g", "a", "v");
	protected final static CodeData classExpressionData = new CodeData("somePackage", "someClass");
	protected final static CodeData classAndMethodExpressionData = new CodeData("somePackage", "someClass", "someMethod");

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
		selectedArtifactSelectionManager = new SelectedArtifactSelectionManager<String, String>(listenerManager, strategy, getExecutor(), IArtifactDataCache.Utils.artifactDataCache(), rememberedExceptions);
		IUrlStrategy urlStrategy = IUrlStrategy.Utils.urlStrategy();
		initialListeners = listenerManager.getListeners().size();
		assertEquals(0, initialListeners);
		panel = makePanel(shell, SoftwareFmContainer.make(urlStrategy, //
				selectedArtifactSelectionManager,//
				ICallback2.Utils.<String, Integer> exception("ImportPom shouldnt be used"),//
				ICallback2.Utils.<ArtifactData, Integer> exception("ImportPom shouldnt be used"),//
				ITemplateStore.Utils.templateStore(urlStrategy), //
				IArtifactDataCache.Utils.artifactDataCache(),//
				new SfmActionState()));
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
