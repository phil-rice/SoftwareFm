package org.softwareFm.configuration.fixture;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.configuration.SoftwareFmPropertyAnchor;
import org.softwareFm.configuration.configurators.ActionStoreConfigurator;
import org.softwareFm.configuration.configurators.DataStoreConfigurator;
import org.softwareFm.configuration.configurators.DisplayerStoreConfigurator;
import org.softwareFm.configuration.configurators.EditorFactoryConfigurator;
import org.softwareFm.configuration.configurators.ListEditorConfigurator;
import org.softwareFm.configuration.configurators.SmallButtonConfigurator;
import org.softwareFm.configuration.largebuttons.ArtifactDetailsLargeButtonFactory;
import org.softwareFm.configuration.largebuttons.ArtifactSocialLargeButtonFactory;
import org.softwareFm.configuration.largebuttons.ArtifactTrainingLargeButtonFactory;
import org.softwareFm.configuration.largebuttons.GroupLargeButtonFactory;
import org.softwareFm.configuration.largebuttons.JarDetailsLargeButtonFactory;
import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.SoftwareFmDataComposite;
import org.softwareFm.display.SoftwareFmLayout;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.browser.BrowserFeedConfigurator;
import org.softwareFm.display.browser.IBrowserConfigurator;
import org.softwareFm.display.browser.RssFeedConfigurator;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.data.IUrlToData;
import org.softwareFm.display.displayer.DisplayerStore;
import org.softwareFm.display.editor.EditorContext;
import org.softwareFm.display.editor.EditorFactory;
import org.softwareFm.display.editor.IEditorFactory;
import org.softwareFm.display.editor.IUpdateStore;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.display.lists.ListEditorStore;
import org.softwareFm.display.smallButtons.SmallButtonStore;
import org.softwareFm.display.timeline.IPlayList;
import org.softwareFm.display.timeline.IPlayListGetter;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public class SoftwareFmFixture {

	public static SoftwareFmDataComposite makeSoftwareFmComposite(Composite parent) throws Exception {
		return new SoftwareFmFixture(parent.getDisplay()).makeComposite(parent);
	}

	public final ImageRegistry imageRegistry;
	public final IResourceGetter resourceGetter;
	public final SoftwareFmLayout layout;
	public final CompositeConfig compositeConfig;
	public final GuiDataStore dataStore;
	public final SmallButtonStore smallButtonStore;
	public final ActionStore actionStore;
	public final DisplayerStore displayerStore;
	public final GuiBuilder guiBuilder;
	private IEditorFactory editorFactory;
	private ListEditorStore listEditorStore;
	private final EditorContext editorContext;
	private final IUpdateStore updateStore;

	public final LargeButtonDefn artifactDetailsButton;
	public final LargeButtonDefn artifactTrainingButton;
	public final LargeButtonDefn artifactSocialButton;
	public final LargeButtonDefn groupLargeButtonFactory;

	private final List<IBrowserConfigurator> browserConfigurators;
	private final IPlayListGetter playListGetter;
	public LargeButtonDefn[] allLargeButtons;
	private LargeButtonDefn jarDetailsButton;

	public SoftwareFmFixture(Display display) throws Exception {
		imageRegistry = new ImageRegistry();
		new BasicImageRegisterConfigurator().registerWith(display, imageRegistry);

		resourceGetter = IResourceGetter.Utils.noResources().with(SoftwareFmPropertyAnchor.class, "SoftwareFmDisplay");
		layout = new SoftwareFmLayout();
		compositeConfig = new CompositeConfig(display, layout, imageRegistry, resourceGetter);
		updateStore = IUpdateStore.Utils.sysoutUpdateStore();
		editorContext = new EditorContext(compositeConfig);

		new ActionStoreConfigurator().process(actionStore = new ActionStore());

		new SmallButtonConfigurator().process(smallButtonStore = new SmallButtonStore());
		new DisplayerStoreConfigurator().process(displayerStore = new DisplayerStore());
		new ListEditorConfigurator().process(listEditorStore = new ListEditorStore());
		new EditorFactoryConfigurator().process(editorFactory = new EditorFactory(editorContext));

		guiBuilder = new GuiBuilder(smallButtonStore, actionStore, displayerStore, listEditorStore);

		jarDetailsButton = new JarDetailsLargeButtonFactory().apply(guiBuilder);
		artifactDetailsButton = new ArtifactDetailsLargeButtonFactory().apply(guiBuilder);
		artifactSocialButton = new ArtifactSocialLargeButtonFactory().apply(guiBuilder);
		artifactTrainingButton = new ArtifactTrainingLargeButtonFactory().apply(guiBuilder);
		groupLargeButtonFactory = new GroupLargeButtonFactory().apply(guiBuilder);

		allLargeButtons = new LargeButtonDefn[] { jarDetailsButton, artifactDetailsButton, artifactSocialButton, artifactTrainingButton, groupLargeButtonFactory };
		browserConfigurators = Arrays.asList(new BrowserFeedConfigurator(), new RssFeedConfigurator());
		playListGetter = new IPlayListGetter() {

			@Override
			public Future<IPlayList> getPlayListFor(String playListName, ICallback<IPlayList> iCallback) throws Exception {
				IPlayList playlist = IPlayList.Utils.make("PlayList", DisplayConstants.rssFeedType, "http://feeds.bbci.co.uk/news/rss.xml");
				iCallback.process(playlist);
				return Futures.doneFuture(playlist);
			}
		};

		new DataStoreConfigurator().process(dataStore = new GuiDataStore(IUrlToData.Utils.errorCallback(), resourceGetter, ICallback.Utils.rethrow()));
	}

	public SoftwareFmDataComposite makeComposite(Composite parent) {
		return makeComposite(parent, allLargeButtons);
	}

	public SoftwareFmDataComposite makeComposite(Composite parent, LargeButtonDefn... largeButtons) {
		SoftwareFmDataComposite result = new SoftwareFmDataComposite(parent, dataStore, compositeConfig, actionStore, //
				editorFactory, updateStore, listEditorStore, ICallback.Utils.rethrow(), //
				Arrays.asList(largeButtons), //
				browserConfigurators, playListGetter);
		return result;
	}

	@SuppressWarnings("unchecked")
	public void forceData(String url, String entity, Map<String, Object> map) {
		Map<String, Object> data = (Map<String, Object>) map.get(entity);
		Map<String, Object> context = Maps.newMap();
		dataStore.forceData(url, entity, data, context);
	}
}
