package org.softwareFm.configuration.fixture;

import java.util.Map;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.configuration.SoftwareFmPropertyAnchor;
import org.softwareFm.configuration.configurators.ActionStoreConfigurator;
import org.softwareFm.configuration.configurators.DataStoreConfigurator;
import org.softwareFm.configuration.configurators.DisplayerStoreConfigurator;
import org.softwareFm.configuration.configurators.EditorFactoryConfigurator;
import org.softwareFm.configuration.configurators.JarLargeButtonFactory;
import org.softwareFm.configuration.configurators.ListEditorConfigurator;
import org.softwareFm.configuration.configurators.OrganisationLargeButtonFactory;
import org.softwareFm.configuration.configurators.ProjectLargeButtonFactory;
import org.softwareFm.configuration.configurators.SmallButtonConfigurator;
import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.IUrlToData;
import org.softwareFm.display.SoftwareFmDataComposite;
import org.softwareFm.display.SoftwareFmLayout;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.displayer.DisplayerStore;
import org.softwareFm.display.editor.EditorContext;
import org.softwareFm.display.editor.EditorFactory;
import org.softwareFm.display.editor.IEditorFactory;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.display.lists.ListEditorStore;
import org.softwareFm.display.smallButtons.SmallButtonStore;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.utilities.callbacks.ICallback;
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
	public final LargeButtonDefn jarButton;
	public final LargeButtonDefn projectButton;
	public final LargeButtonDefn organisationButton;
	private IEditorFactory editorFactory;
	private ListEditorStore listEditorStore;
	private EditorContext editorContext;

	public SoftwareFmFixture(Display display) throws Exception {
		imageRegistry = new ImageRegistry();
		new BasicImageRegisterConfigurator().registerWith(display, imageRegistry);

		resourceGetter = IResourceGetter.Utils.noResources().with(SoftwareFmPropertyAnchor.class, "SoftwareFmDisplay");
		layout = new SoftwareFmLayout();
		compositeConfig = new CompositeConfig(display, layout, imageRegistry, resourceGetter);
		editorContext = new EditorContext(compositeConfig);

		new SmallButtonConfigurator().process(smallButtonStore = new SmallButtonStore());
		new ActionStoreConfigurator().process(actionStore = new ActionStore());
		new DisplayerStoreConfigurator().process(displayerStore = new DisplayerStore());
		new ListEditorConfigurator().process(listEditorStore = new ListEditorStore());
		new EditorFactoryConfigurator().process(editorFactory = new EditorFactory(editorContext));

		guiBuilder = new GuiBuilder(smallButtonStore, actionStore, displayerStore, listEditorStore);

		jarButton = new JarLargeButtonFactory().apply(guiBuilder);
		projectButton = new ProjectLargeButtonFactory().apply(guiBuilder);
		organisationButton = new OrganisationLargeButtonFactory().apply(guiBuilder);

		new DataStoreConfigurator().process(dataStore = new GuiDataStore(IUrlToData.Utils.errorCallback(), ICallback.Utils.rethrow()));
	}

	public SoftwareFmDataComposite makeComposite(Composite parent) {
		SoftwareFmDataComposite result = new SoftwareFmDataComposite(parent, //
				dataStore, compositeConfig, actionStore, editorFactory, ICallback.Utils.rethrow(), //
				jarButton, projectButton, organisationButton);
		return result;
	}

	@SuppressWarnings("unchecked")
	public void forceData(String url, String entity, Map<String, Object> map) {
		Map<String, Object> data = (Map<String, Object>) map.get(entity);
		Map<String, Object> context = Maps.newMap();
		dataStore.forceData(url, entity, data, context);
	}
}
