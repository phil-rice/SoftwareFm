package org.softwarefm.display;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.softwareFmImages.BasicImageRegister;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwarefm.display.actions.ActionStore;
import org.softwarefm.display.actions.InternalBrowseFileOrUrlAction;
import org.softwarefm.display.actions.TextEditAction;
import org.softwarefm.display.composites.CompositeConfig;
import org.softwarefm.display.data.GuiDataStore;
import org.softwarefm.display.displayer.DisplayerStore;
import org.softwarefm.display.displayer.TextDisplayer;
import org.softwarefm.display.impl.LargeButtonDefn;
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
				IResourceGetter resourceGetter = IResourceGetter.Utils.noResources().with(Sample.class, "SoftwareFmDisplay");

				SoftwareFmLayout layout = new SoftwareFmLayout();
				CompositeConfig compositeConfig = new CompositeConfig(from.getDisplay(), layout, imageRegistry, resourceGetter);
				GuiDataStore dataStore = new GuiDataStore(IUrlToData.Utils.errorCallback(), ICallback.Utils.rethrow()).//
						urlGenerator("urlGenerator.jar", new JarUrlGenerator()).//
						urlGenerator("urlGenerator.project", new UrlGenerator("project")).//
						urlGenerator("urlGenerator.organisation", new UrlGenerator("organisation")).//
						entity("jar", "urlGenerator.jar").//
						dependant("jar", "project", "jar.projectUrl", "urlGenerator.project").//
						dependant("jar", "organisation", "jar.organisationUrl", "urlGenerator.organisation");

				SmallButtonStore smallButtonStore = new SmallButtonStore().//
						smallButton("smallButton.normal", new SmallButtonFactory());

				ActionStore actionStore = new ActionStore().//
						action("action.text.edit", new TextEditAction()).//
						action("action.text.internalBrowseFileOrUrl", new InternalBrowseFileOrUrlAction());//

				DisplayerStore displayerStore = new DisplayerStore().//
						displayer("displayer.readOnly.text", new TextDisplayer(false)).//
						displayer("displayer.readWrite.text", new TextDisplayer(true)).//
						displayer("displayer.readWrite.url", new TextDisplayer(true)).//
						displayer("displayer.readWrite.list", new TextDisplayer(true));//

				GuiBuilder guiBuilder = new GuiBuilder(resourceGetter, imageRegistry, smallButtonStore, dataStore, actionStore, displayerStore);

				final LargeButtonDefn jarButton = guiBuilder.largeButton("largeButton.jar",//
						guiBuilder.smallButton("smallButton.jar.details","smallButton.jar.details.title","smallButton.normal", "artifact.jar", //
								guiBuilder.displayer("displayer.readOnly.text").title("jar.jarName.title").data("data.jar.jarName").tooltip("jar.jarPath.tooltip"), //
								guiBuilder.displayer("displayer.readOnly.text").title("project.name.title").data("data.project.name").actions(//
										guiBuilder.action("action.text.edit", "artifact.project").tooltip("action.edit.tooltip").params("data.project.url"),//
										guiBuilder.action("action.text.externalBrowseFileOrUrl", "artifact.project").tooltip("action.browse.tooltip").params("data.project.url")//
										).tooltip("data.project.url"),//
								guiBuilder.displayer("displayer.readOnly.text").title("organisation.name.title").data("organisation.name").actions(//
										guiBuilder.action("action.text.externalBrowseFileOrUrl", "artifact.organisation").tooltip("action.browse.tooltip").params("organisation.url")//
										).tooltip("data.organisation.url")).//
								ctrlClickAction("action.text.internalBrowseFileOrUrl", "data.jar.jarPath"));//
				final LargeButtonDefn projectButton = guiBuilder.largeButton("largeButton.project", //
						guiBuilder.smallButton("smallButton.project.details", "smallButton.project.details.title", "smallButton.normal", "artifact.project", //
								guiBuilder.displayer("displayer.readOnly.text").title("project.name.title").data("data.project.name").tooltip("data.project.description"), //
								guiBuilder.displayer("displayer.readWrite.url").title("project.url.title").data("data.jar.projectUrl")).//
								ctrlClickAction("action.text.internalBrowseFileOrUrl", "data.jar.projectUrl").tooltip("smallButton.project.details.tooltip"),//
						guiBuilder.smallButton("smallButton.project.bugs","smallButton.project.bugs.title", "smallButton.normal", "artifact.issues",//
								guiBuilder.displayer("displayer.readWrite.url").title("project.issues.title").data("data.project.issues").tooltip("project.issues.tooltip"),//
								guiBuilder.listDisplayer("displayer.readWrite.list", "lineEditor.nameAndEmail").title("project.mailingList.title").data("data.project.mailingList")).//
								ctrlClickAction("action.text.internalBrowseFileOrUrl", "data.project.issues"),//
						guiBuilder.smallButton("smallButton.project.twitter", "smallButton.project.twitter.title","smallButton.normal", "artifact.twitter",//
								guiBuilder.listDisplayer("displayer.readWrite.list", "lineEditor.nameAndEmail").title("project.twitter.title").data("data.project.twitter")));//
				final LargeButtonDefn organisationButton = guiBuilder.largeButton("largeButton.organisation", //
						guiBuilder.smallButton("smallButton.organisation.details","smallButton.organisation.details.title", "smallButton.normal", "artifact.organisation",//
								guiBuilder.displayer("displayer.readOnly.text").title("organisation.name.title").data("data.organisation.name"), //
								guiBuilder.displayer("displayer.readWrite.url").title("organisation.url.title").data("data.jar.organisationUrl").actions(//
										guiBuilder.action("action.text.externalBrowseFileOrUrl", "artifact.browse").params("organisation.url"))),//
						guiBuilder.smallButton("smallButton.organisation.twitter", "smallButton.organisation.twitter.title","smallButton.normal", "artifact.twitter", //
								guiBuilder.listDisplayer("displayer.readWrite.list", "lineEditor.nameAndEmail").title("organisation.twitter.title").data("data.organisation.twitter")));//
				SoftwareFmDataComposite result = new SoftwareFmDataComposite(from, compositeConfig, ICallback.Utils.rethrow(), jarButton, projectButton, organisationButton);
				return result.getComposite();
			}
		});
	}
}
