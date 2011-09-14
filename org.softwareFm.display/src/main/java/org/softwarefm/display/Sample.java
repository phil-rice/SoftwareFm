package org.softwarefm.display;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.softwareFmImages.BasicImageRegister;
import org.softwareFm.softwareFmImages.backdrop.BackdropAnchor;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwarefm.display.actions.ActionStore;
import org.softwarefm.display.actions.ExternalBrowseFileOrUrlAction;
import org.softwarefm.display.actions.InternalBrowseFileOrUrlAction;
import org.softwarefm.display.data.GuiDataStore;
import org.softwarefm.display.displayer.DisplayerStore;
import org.softwarefm.display.displayer.TextDisplayer;
import org.softwarefm.display.impl.LargeButtonDefn;
import org.softwarefm.display.smallButtons.ImageButtonConfig;
import org.softwarefm.display.smallButtons.SmallButtonStore;
import org.softwarefm.display.urlGenerator.JarUrlGenerator;
import org.softwarefm.display.urlGenerator.UrlGenerator;

public class Sample {

	public static void main(String[] args) {

		Swts.display("Sample", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				ImageRegistry imageRegistry = new ImageRegistry();
				new BasicImageRegister().registerWith(from.getDisplay(), imageRegistry);
				IResourceGetter resourceGetter = IResourceGetter.Utils.noResources();

				SoftwareFmLayout layout = new SoftwareFmLayout();
				ImageButtonConfig imageButtonConfig = new ImageButtonConfig(layout, imageRegistry, BackdropAnchor.depressed, BackdropAnchor.main, null, null);
				
				GuiDataStore dataStore = new GuiDataStore(IUrlToData.Utils.errorCallback()).//
						urlGenerator("urlGenerator.jar", new JarUrlGenerator()).//
						urlGenerator("urlGenerator.project", new UrlGenerator("project")).//
						urlGenerator("urlGenerator.organisation", new UrlGenerator("organisation")).//
						entity("jar", "urlGenerator.jar").//
						dependant("jar", "project", "jar.projectUrl", "urlGenerator.project").//
						dependant("jar", "organisation", "jar.organisationUrl", "urlGenerator.organisation");

				SmallButtonStore smallButtonStore = new SmallButtonStore().//
						smallButton("smallButton.jarSummary", new JarSmallButtonFactory()).//
						smallButton("smallButton.normal", new SmallButtonFactory());

				ActionStore actionStore = new ActionStore().//
						action("action.url.internalBrowseFirstUrl", new InternalBrowseFileOrUrlAction()).//
						action("action.text.internalBrowseFileOrUrl", new InternalBrowseFileOrUrlAction()).//
						action("action.list.internalBrowseFirstUrl", new InternalBrowseFileOrUrlAction()).//
						action("action.text.externalBrowseFileOrUrl", new ExternalBrowseFileOrUrlAction());

				DisplayerStore displayerStore = new DisplayerStore().//
						displayer("displayer.readOnly.text", new TextDisplayer()).//
						displayer("displayer.readWrite.text", new TextDisplayer()).//
						displayer("displayer.readWrite.url", new TextDisplayer()).//
						displayer("displayer.readWrite.list", new TextDisplayer());//

				GuiBuilder guiBuilder = new GuiBuilder(resourceGetter, imageRegistry, smallButtonStore, dataStore, actionStore, displayerStore);

				final LargeButtonDefn jarButton = guiBuilder.largeButton("largeButton.jar",//
						guiBuilder.smallButton("smallButton.jar.details", "smallButton.jarSummary", "artifact.jar", //
								guiBuilder.displayer("displayer.readOnly.text").data("jar.jarName").tooltip("jar.jarPath"), //
								guiBuilder.displayer("displayer.readOnly.text").data("project.name").action("action.text.externalBrowseFileOrUrl", "project.url", "artifact.project").tooltip("data.project.url"),//
								guiBuilder.displayer("displayer.readOnly.text").data("organisation.name").action("action.text.externalBrowseFileOrUrl", "organisation.url", "artifact.organisation").tooltip("data.organisation.url")).//
								ctrlClickAction("action.text.internalBrowseFileOrUrl", "data.jar.jarPath"));//
				final LargeButtonDefn projectButton = guiBuilder.largeButton("largeButton.project", //
						guiBuilder.smallButton("smallButton.project.details", "smallButton.normal", "artifact.project", //
								guiBuilder.displayer("displayer.readOnly.text").data("data.project.name").tooltip("data.project.description"), //
								guiBuilder.displayer("displayer.readWrite.url").data("data.jar.projectUrl")).//
								ctrlClickAction("action.text.internalBrowseFileOrUrl", "data.jar.projectUrl").tooltip("smallButton.project.details.tooltip"),//
						guiBuilder.smallButton("smallButton.project.bugs", "smallButton.normal", "artifact.issues",//
								guiBuilder.displayer("displayer.readWrite.url").data("data.project.issues").tooltip("project.issues.tooltip"),//
								guiBuilder.listDisplayer("displayer.readWrite.list", "lineEditor.nameAndEmail").data("data.project.mailingList")).//
								ctrlClickAction("action.text.internalBrowseFileOrUrl", "data.project.issues"),//
						guiBuilder.smallButton("smallButton.project.twitter", "smallButton.normal", "artifact.twitter",//
								guiBuilder.listDisplayer("displayer.readWrite.list", "lineEditor.nameAndEmail").data("data.project.twitter")).//
								ctrlClickAction("action.list.internalBrowseFirstUrl", "data.project.twitter"));//
				final LargeButtonDefn organisationButton = guiBuilder.largeButton("largeButton.organisation", //
						guiBuilder.smallButton("smallButton.organisation.details", "smallButton.normal", "artifact.organisation",//
								guiBuilder.displayer("displayer.readOnly.text").data("data.organisation.name"), //
								guiBuilder.displayer("displayer.readWrite.url").data("data.jar.organisationUrl").action("action.text.externalBrowseFileOrUrl", "organisation.url", "artifact.browse")),//
						guiBuilder.smallButton("smallButton.organisation.bugs", "smallButton.normal", "artifact.issues",//
								guiBuilder.displayer("displayer.readWrite.url").data("data.project.issues"), //
								guiBuilder.listDisplayer("displayer.readWrite.list", "lineEditor.nameAndEmail").data("data.organisation.mailingList")),//
						guiBuilder.smallButton("smallButton.organisation.twitter", "smallButton.normal", "artifact.twitter", //
								guiBuilder.listDisplayer("displayer.readWrite.list", "lineEditor.nameAndEmail").data("data.organisation.twitter")));//
				SoftwareFmDataComposite result = new SoftwareFmDataComposite(from, imageButtonConfig, ICallback.Utils.rethrow(),jarButton, projectButton, organisationButton);
				return result.getComposite();
			}
		});
	}
}
