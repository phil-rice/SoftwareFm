package org.softwareFm.eclipse.fixture;

import java.util.Map;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.display.ActionDefn;
import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.IUrlToData;
import org.softwareFm.display.SmallButtonFactory;
import org.softwareFm.display.SoftwareFmDataComposite;
import org.softwareFm.display.SoftwareFmLayout;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.actions.BrowseAction;
import org.softwareFm.display.actions.ListAddAction;
import org.softwareFm.display.actions.ListDeleteAction;
import org.softwareFm.display.actions.TextEditAction;
import org.softwareFm.display.actions.ViewTweetsAction;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.displayer.DisplayerStore;
import org.softwareFm.display.displayer.ListDisplayerFactory;
import org.softwareFm.display.displayer.TextDisplayerFactory;
import org.softwareFm.display.editor.EditorContext;
import org.softwareFm.display.editor.EditorFactory;
import org.softwareFm.display.editor.IEditorFactory;
import org.softwareFm.display.editor.TextEditor;
import org.softwareFm.display.impl.LargeButtonDefn;
import org.softwareFm.display.lists.ListEditorStore;
import org.softwareFm.display.lists.NameAndUrlListEditor;
import org.softwareFm.display.lists.NameAndValueListEditor;
import org.softwareFm.display.lists.ValueListEditor;
import org.softwareFm.display.smallButtons.SmallButtonStore;
import org.softwareFm.display.urlGenerator.JarUrlGenerator;
import org.softwareFm.display.urlGenerator.UrlGenerator;
import org.softwareFm.eclipse.SoftwareFmDataStoreConfigurator;
import org.softwareFm.eclipse.SoftwareFmPropertyAnchor;
import org.softwareFm.eclipse.jar.JarSimpleButtonFactory;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

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
	private ListEditorStore listEditorStore;

	public SoftwareFmFixture(Display display) {
		imageRegistry = new ImageRegistry();
		new BasicImageRegisterConfigurator().registerWith(display, imageRegistry);
		resourceGetter = IResourceGetter.Utils.noResources().with(SoftwareFmPropertyAnchor.class, "SoftwareFmDisplay");

		layout = new SoftwareFmLayout();
		compositeConfig = new CompositeConfig(display, layout, imageRegistry, resourceGetter);
		dataStore = new GuiDataStore(IUrlToData.Utils.errorCallback(), ICallback.Utils.rethrow());
		new SoftwareFmDataStoreConfigurator().process(dataStore);

		smallButtonStore = new SmallButtonStore().//
				smallButton("smallButton.normal", new SmallButtonFactory()).//
				smallButton("smallButton.jar", new JarSimpleButtonFactory());

		actionStore = new ActionStore().//
				action("action.text.edit", new TextEditAction()).//
				action("action.list.add", new ListAddAction()).//
				action("action.list.delete", new ListDeleteAction()).//
				action("action.list.viewTweets", new ViewTweetsAction()).//
				action("action.text.browse", new BrowseAction());

		displayerStore = new DisplayerStore().//
				displayer("displayer.text", new TextDisplayerFactory()).//
				displayer("displayer.url", new TextDisplayerFactory()).//
				displayer("displayer.list", new ListDisplayerFactory());

		listEditorStore = new ListEditorStore().//
				register("listEditor.nameAndValue", new NameAndValueListEditor("")).//
				register("listEditor.nameAndUrl", new NameAndUrlListEditor("")).//
				register("listEditor.nameAndEmail", new NameAndValueListEditor("")).//
				register("listEditor.tweet", new ValueListEditor("tweet.line.title"));

		EditorContext editorContext = new EditorContext(compositeConfig);
		editorFactory = new EditorFactory(editorContext).register("editor.text", new TextEditor());
		guiBuilder = new GuiBuilder(resourceGetter, imageRegistry, smallButtonStore, dataStore, actionStore, displayerStore, listEditorStore);

		jarButton = guiBuilder.largeButton("largeButton.jar",//
				guiBuilder.smallButton("smallButton.jar.details", "smallButton.jar.details.title", "smallButton.jar", ArtifactsAnchor.jarKey, //
						guiBuilder.displayer("displayer.text").title("jar.jarName.title").data("data.raw.jarPath").tooltip("jar.jarPath.tooltip"), //
						guiBuilder.displayer("displayer.text").title("project.name.title").data("data.project.name").actions(//
								guiBuilder.action("action.text.edit", ArtifactsAnchor.projectKey, "overlay.edit").tooltip("action.edit.tooltip").params("data.jar.project.url"),//
								guiBuilder.action("action.text.browse", GeneralAnchor.browseKey).tooltip("data.jar.project.url").params("data.jar.project.url")//
								).tooltip("data.jar.project.url"),//
						guiBuilder.displayer("displayer.text").title("organisation.name.title").data("data.organisation.name").actions(//
								guiBuilder.action("action.text.browse", GeneralAnchor.browseKey).tooltip("data.jar.organisation.url").params("data.jar.organisation.url")//
								).tooltip("data.organisation.url"),//
						guiBuilder.displayer("displayer.text").title("jar.javadoc.title").data("data.raw.javadoc").tooltip("data.raw.javadoc"), //
						guiBuilder.displayer("displayer.text").title("jar.source.title").data("data.raw.source").tooltip("data.raw.source")));

		projectButton = guiBuilder.largeButton("largeButton.project", //
				guiBuilder.smallButton("smallButton.project.details", "smallButton.project.details.title", "smallButton.normal", ArtifactsAnchor.projectKey, //
						guiBuilder.displayer("displayer.text").title("project.name.title").data("data.project.name").tooltip("data.project.description").actions(//
								guiBuilder.action("action.text.edit", ArtifactsAnchor.projectKey, "overlay.edit").tooltip("action.edit.tooltip").params("data.project.name")), //
						guiBuilder.displayer("displayer.url").title("project.url.title").data("data.jar.project.url")).//
						ctrlClickAction("action.text.browse", "data.jar.projectUrl").tooltip("smallButton.project.details.tooltip"),//
				guiBuilder.smallButton("smallButton.project.bugs", "smallButton.project.bugs.title", "smallButton.normal", ArtifactsAnchor.issuesKey,//
						guiBuilder.displayer("displayer.url").title("project.issues.title").data("data.project.issues").tooltip("project.issues.tooltip").actions(//
								guiBuilder.action("action.text.edit", "artifact.issues", "overlay.edit").tooltip("action.edit.tooltip").params("data.project.issues")), // ,//
						guiBuilder.listDisplayer("displayer.list", "listEditor.nameAndEmail").title("project.mailingList.title").data("data.project.mailingList").//
								actions(makeMainListActions(ArtifactsAnchor.mailingListKey)).listActions(listDeleteAction(ArtifactsAnchor.mailingListKey))).//
						ctrlClickAction("action.text.browse", "data.project.issues"),//
				guiBuilder.smallButton("smallButton.project.twitter", "smallButton.project.twitter.title", "smallButton.normal", ArtifactsAnchor.twitterKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditor.tweet").title("project.twitter.title").data("data.project.twitter").//
								actions(makeMainListActions(ArtifactsAnchor.twitterKey)).//
								listActions(guiBuilder.action("action.list.viewTweets", GeneralAnchor.browseKey).params("project.twitter.lineTitle"), listDeleteAction(ArtifactsAnchor.twitterKey))));

		organisationButton = guiBuilder.largeButton("largeButton.organisation", //
				guiBuilder.smallButton("smallButton.organisation.details", "smallButton.organisation.details.title", "smallButton.normal", ArtifactsAnchor.organisationKey,//
						guiBuilder.displayer("displayer.text").title("organisation.name.title").data("data.organisation.name").actions(//
								guiBuilder.action("action.text.edit", ArtifactsAnchor.organisationKey, "overlay.edit").tooltip("action.edit.tooltip").params("data.organisation.name")), // , //
						guiBuilder.displayer("displayer.url").title("organisation.url.title").data("data.jar.organisation.url").actions(//
								guiBuilder.action("action.text.browse", GeneralAnchor.browseKey).tooltip("data.jar.organisation.url").params("data.jar.organisation.url"),//
								guiBuilder.action("action.text.edit", ArtifactsAnchor.organisationKey, "overlay.edit").tooltip("action.edit.tooltip").params("data.organisation.url"))),//
				guiBuilder.smallButton("smallButton.organisation.twitter", "smallButton.organisation.twitter.title", "smallButton.normal", ArtifactsAnchor.twitterKey, //
						guiBuilder.listDisplayer("displayer.list", "listEditor.nameAndEmail").title("organisation.twitter.title").data("data.organisation.twitter").//
								actions(makeMainListActions(ArtifactsAnchor.twitterKey)).listActions(listDeleteAction(ArtifactsAnchor.twitterKey))));
	}

	public ActionDefn[] makeMainListActions(String artifactId) {
		return new ActionDefn[] {//
		guiBuilder.action("action.list.add", artifactId, OverlaysAnchor.addKey).tooltip("action.add.tooltip") };
	}

	public ActionDefn listDeleteAction(String artifactId) {
		return guiBuilder.action("action.list.delete", artifactId, OverlaysAnchor.deleteKey).tooltip("action.delete.tooltip");
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
