package org.softwarefm.display;

import java.util.Map;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwarefm.display.actions.ActionStore;
import org.softwarefm.display.actions.BrowseAction;
import org.softwarefm.display.actions.TextEditAction;
import org.softwarefm.display.composites.CompositeConfig;
import org.softwarefm.display.data.GuiDataStore;
import org.softwarefm.display.displayer.DisplayerStore;
import org.softwarefm.display.displayer.TextDisplayer;
import org.softwarefm.display.editor.EditorContext;
import org.softwarefm.display.editor.EditorFactory;
import org.softwarefm.display.editor.IEditorFactory;
import org.softwarefm.display.editor.TextEditor;
import org.softwarefm.display.impl.LargeButtonDefn;
import org.softwarefm.display.smallButtons.SmallButtonStore;
import org.softwarefm.display.urlGenerator.JarUrlGenerator;
import org.softwarefm.display.urlGenerator.UrlGenerator;

public class SoftwareFmFixture {
	public static SoftwareFmDataComposite makeSoftwareFmComposite(Composite parent) {
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

	public SoftwareFmFixture(Display display) {
		imageRegistry = new ImageRegistry();
		new BasicImageRegisterConfigurator().registerWith(display, imageRegistry);
		resourceGetter = IResourceGetter.Utils.noResources().with(Sample.class, "SoftwareFmDisplay");

		layout = new SoftwareFmLayout();
		compositeConfig = new CompositeConfig(display, layout, imageRegistry, resourceGetter);
		dataStore = new GuiDataStore(IUrlToData.Utils.errorCallback(), ICallback.Utils.rethrow()).//
				urlGenerator("urlGenerator.jar", new JarUrlGenerator()).//
				urlGenerator("urlGenerator.project", new UrlGenerator("project")).//
				urlGenerator("urlGenerator.organisation", new UrlGenerator("organisation")).//
				entity("jar", "urlGenerator.jar").//
				dependant("jar", "project", "jar.projectUrl", "urlGenerator.project").//
				dependant("jar", "organisation", "jar.organisationUrl", "urlGenerator.organisation");

		smallButtonStore = new SmallButtonStore().//
				smallButton("smallButton.normal", new SmallButtonFactory());

		actionStore = new ActionStore().//
				action("action.text.edit", new TextEditAction()).//
				action("action.text.externalBrowseFileOrUrl", new BrowseAction());

		displayerStore = new DisplayerStore().//
				displayer("displayer.text", new TextDisplayer(false)).//
				displayer("displayer.url", new TextDisplayer(true)).//
				displayer("displayer.list", new TextDisplayer(true));

		EditorContext editorContext = new EditorContext(compositeConfig);
		editorFactory = new EditorFactory(editorContext).register("editor.text", new TextEditor());
		guiBuilder = new GuiBuilder(resourceGetter, imageRegistry, smallButtonStore, dataStore, actionStore, displayerStore);

		jarButton = guiBuilder.largeButton("largeButton.jar",//
				guiBuilder.smallButton("smallButton.jar.details", "smallButton.jar.details.title", "smallButton.normal", "artifact.jar", //
						guiBuilder.displayer("displayer.text").title("jar.jarName.title").data("data.jar.jarName").tooltip("jar.jarPath.tooltip"), //
						guiBuilder.displayer("displayer.text").title("project.name.title").data("data.project.name").actions(//
								guiBuilder.action("action.text.edit", "artifact.project", "overlay.edit").tooltip("action.edit.tooltip").params("data.jar.project.url"),//
								guiBuilder.action("action.text.externalBrowseFileOrUrl", "general.browse").tooltip("data.jar.project.url").params("data.jar.project.url")//
								).tooltip("data.jar.project.url"),//
						guiBuilder.displayer("displayer.text").title("organisation.name.title").data("data.organisation.name").actions(//
								guiBuilder.action("action.text.externalBrowseFileOrUrl", "general.browse").tooltip("data.jar.organisation.url").params("data.jar.organisation.url")//
								).tooltip("data.organisation.url")).//
						ctrlClickAction("action.text.externalBrowseFileOrUrl", "data.jar.jarPath"));

		projectButton = guiBuilder.largeButton("largeButton.project", //
				guiBuilder.smallButton("smallButton.project.details", "smallButton.project.details.title", "smallButton.normal", "artifact.project", //
						guiBuilder.displayer("displayer.text").title("project.name.title").data("data.project.name").tooltip("data.project.description").actions(//
								guiBuilder.action("action.text.edit", "artifact.project", "overlay.edit").tooltip("action.edit.tooltip")), //
						guiBuilder.displayer("displayer.url").title("project.url.title").data("data.jar.project.url")).//
						ctrlClickAction("action.text.externalBrowseFileOrUrl", "data.jar.projectUrl").tooltip("smallButton.project.details.tooltip"),//
				guiBuilder.smallButton("smallButton.project.bugs", "smallButton.project.bugs.title", "smallButton.normal", "artifact.issues",//
						guiBuilder.displayer("displayer.url").title("project.issues.title").data("data.project.issues").tooltip("project.issues.tooltip").actions(//
								guiBuilder.action("action.text.edit", "artifact.issues", "overlay.edit").tooltip("action.edit.tooltip")), // ,//
						guiBuilder.listDisplayer("displayer.list", "lineEditor.nameAndEmail").title("project.mailingList.title").data("data.project.mailingList")).//
						ctrlClickAction("action.text.externalBrowseFileOrUrl", "data.project.issues"),//
				guiBuilder.smallButton("smallButton.project.twitter", "smallButton.project.twitter.title", "smallButton.normal", "artifact.twitter",//
						guiBuilder.listDisplayer("displayer.list", "lineEditor.nameAndEmail").title("project.twitter.title").data("data.project.twitter")));

		organisationButton = guiBuilder.largeButton("largeButton.organisation", //
				guiBuilder.smallButton("smallButton.organisation.details", "smallButton.organisation.details.title", "smallButton.normal", "artifact.organisation",//
						guiBuilder.displayer("displayer.text").title("organisation.name.title").data("data.organisation.name").actions(//
								guiBuilder.action("action.text.edit", "artifact.project", "overlay.edit").tooltip("action.edit.tooltip")), // , //
						guiBuilder.displayer("displayer.url").title("organisation.url.title").data("data.jar.organisation.url").actions(//
								guiBuilder.action("action.text.externalBrowseFileOrUrl", "general.browse").tooltip("data.jar.organisation.url").params("data.jar.organisation.url"))),//
				guiBuilder.smallButton("smallButton.organisation.twitter", "smallButton.organisation.twitter.title", "smallButton.normal", "artifact.twitter", //
						guiBuilder.listDisplayer("displayer.list", "lineEditor.nameAndEmail").title("organisation.twitter.title").data("data.organisation.twitter")));
	}

	public SoftwareFmDataComposite makeComposite(Composite parent) {
		SoftwareFmDataComposite result = new SoftwareFmDataComposite(parent, //
				dataStore, compositeConfig, actionStore, editorFactory, ICallback.Utils.rethrow(), //
				jarButton, projectButton, organisationButton);
		return result;
	}

	public void forceData(String url, Map<String, Object> map) {
		forceData(url, "jar", map);
		forceData(url, "project", map);
		forceData(url, "organisation", map);
	}

	@SuppressWarnings("unchecked")
	private void forceData(String url, String entity, Map<String, Object> map) {
		Map<String, Object> data = (Map<String, Object>) map.get(entity);
		Map<String, Object> context = Maps.newMap();
		dataStore.forceData(url, entity, data, context);

	}
}