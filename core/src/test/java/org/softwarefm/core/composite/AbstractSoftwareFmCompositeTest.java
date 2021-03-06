package org.softwarefm.core.composite;

import java.io.File;

import org.easymock.EasyMock;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.actions.SfmActionState;
import org.softwarefm.core.cache.IArtifactDataCache;
import org.softwarefm.core.constants.ImageConstants;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.selection.FileAndDigest;
import org.softwarefm.core.selection.ISelectedBindingListener;
import org.softwarefm.core.selection.ISelectedBindingStrategy;
import org.softwarefm.core.selection.internal.SelectedArtifactSelectionManager;
import org.softwarefm.core.selection.internal.SwtThreadExecutor;
import org.softwarefm.core.templates.ITemplateStore;
import org.softwarefm.core.tests.SwtTest;
import org.softwarefm.core.url.IUrlStrategy;
import org.softwarefm.shared.constants.CommonConstants;
import org.softwarefm.shared.social.ISocialManager;
import org.softwarefm.shared.usage.IUsageFromServer;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.callbacks.ICallback2;
import org.softwarefm.utilities.callbacks.MemoryCallback;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.strings.Strings;

public abstract class AbstractSoftwareFmCompositeTest<P extends SoftwareFmComposite> extends SwtTest {
	protected final CodeData codeData = new CodeData("packageName/className");
	protected final ArtifactData artifactData = new ArtifactData(fileAndDigest, "groupId0", "artifactId0", "version0");

	protected P panel;

	protected ISelectedBindingStrategy<String, String> strategy;
	protected SelectedArtifactSelectionManager<String, String> selectedArtifactSelectionManager;

	private MemoryCallback<Throwable> rememberedExceptions;

	protected final static String classAndMethodNameUrl = Strings.url(CommonConstants.softwareFmHostAndPrefix, "code:packageName/className");
	protected final static String digestUrl = Strings.url(CommonConstants.softwareFmHostAndPrefix, "Digest:0123456789");
	protected final static String projectUrl = Strings.url(CommonConstants.softwareFmHostAndPrefix, "project/groupId0/artifactId0");
	protected final static File file = new File(new File("some"), "file");
	protected final static FileAndDigest fileAndDigest = new FileAndDigest(file, "0123456789");
	protected final static FileAndDigest fileAndNoDigest = new FileAndDigest(file, null);
	protected final static FileAndDigest noFileNameAndNoDigest = new FileAndDigest(null, null);
	protected final static CodeData classData = new CodeData("packageName/className");

	private int initialListeners;

	private ISocialManager socialManager;

	abstract protected P makePanel(Composite parent, SoftwareFmContainer<?> container);

	public void testDisposeRemovesSelfAsListener() {
		panel.dispose();
		assertEquals(selectedArtifactSelectionManager.getListeners().toString(), initialListeners, selectedArtifactSelectionManager.getListeners().size());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		IMultipleListenerList listenerList = IMultipleListenerList.Utils.defaultList();
		listenerList.registerExecutor(ISelectedBindingListener.class, new SwtThreadExecutor(display));
		
		strategy = EasyMock.createMock(ISelectedBindingStrategy.class);
		EasyMock.makeThreadSafe(strategy, true);
		rememberedExceptions = ICallback.Utils.<Throwable> memory();
		socialManager = ISocialManager.Utils.socialManager(listenerList, IUsagePersistance.Utils.persistance());
		selectedArtifactSelectionManager = new SelectedArtifactSelectionManager<String, String>(listenerList, strategy, getExecutor(), IArtifactDataCache.Utils.artifactDataCache(), rememberedExceptions);
		IUrlStrategy urlStrategy = IUrlStrategy.Utils.urlStrategy();

		ImageRegistry imageRegistry = new ImageRegistry(display);
		ImageConstants.initializeImageRegistry(display, imageRegistry);
		IUsageFromServer usageFromServer = null;	
		SoftwareFmContainer<String> container = SoftwareFmContainer.make(urlStrategy, //
				selectedArtifactSelectionManager,//
				socialManager,//
				ICallback2.Utils.<String, Integer> exception("ImportPom shouldnt be used"),//
				ICallback2.Utils.<ArtifactData, Integer> exception("ImportPom shouldnt be used"),//
				ITemplateStore.Utils.templateStore(urlStrategy), //
				IArtifactDataCache.Utils.artifactDataCache(),//
				new SfmActionState(),//
				imageRegistry, usageFromServer);
		initialListeners = selectedArtifactSelectionManager.getListeners().size();
		assertEquals(1, initialListeners);
		panel = makePanel(shell, container);
	}

	@Override
	protected void tearDown() throws Exception {
		if (rememberedExceptions.getResults().size() > 0)
			for (Throwable e : rememberedExceptions.getResults())
				e.printStackTrace();
		assertEquals(0, rememberedExceptions.getResults().size());
		super.tearDown();
	}
	protected void setUpMockForUnknownDigest() {
		EasyMock.expect(strategy.findNode("selection", 1)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", 1)).andReturn(codeData);
		EasyMock.expect(strategy.findFile("selection", "node", 1)).andReturn(file);
		EasyMock.expect(strategy.findDigest("selection", "node", file, 1)).andReturn(fileAndDigest);
		EasyMock.expect(strategy.findArtifact("selection", fileAndDigest, 1)).andReturn(null);
		EasyMock.replay(strategy);
	}
	protected void setupMockForFoundArtifact() {
		EasyMock.expect(strategy.findNode("selection", 1)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", 1)).andReturn(codeData);
		EasyMock.expect(strategy.findFile("selection", "node", 1)).andReturn(file);
		EasyMock.expect(strategy.findDigest("selection", "node", file, 1)).andReturn(fileAndDigest);
		EasyMock.expect(strategy.findArtifact("selection", fileAndDigest, 1)).andReturn(artifactData);
		EasyMock.replay(strategy);
	}
	protected void setUpMockForNoDigest() {
		EasyMock.expect(strategy.findNode("selection", 1)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", 1)).andReturn(codeData);
		EasyMock.expect(strategy.findFile("selection", "node", 1)).andReturn(file);
		EasyMock.expect(strategy.findDigest("selection", "node", file, 1)).andReturn(null);
		EasyMock.replay(strategy);
	}

}
