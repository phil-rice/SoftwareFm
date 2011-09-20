package org.softwareFm.eclipse.configurators;

import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.largeButton.ILargeButtonFactory;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;

public class OrganisationLargeButtonFactory implements ILargeButtonFactory {

	@Override
	public LargeButtonDefn apply(GuiBuilder guiBuilder) throws Exception {
		return guiBuilder.largeButton("largeButton.organisation", //
				guiBuilder.smallButton("smallButton.organisation.details", "smallButton.organisation.details.title", "smallButton.normal", ArtifactsAnchor.organisationKey,//
						guiBuilder.displayer("displayer.text").title("organisation.name.title").data("data.organisation.name").actions(//
								guiBuilder.action("action.text.edit", ArtifactsAnchor.organisationKey, "overlay.edit").tooltip("action.edit.tooltip").params("data.organisation.name")), // , //
						guiBuilder.displayer("displayer.url").title("organisation.url.title").data("data.jar.organisation.url").actions(//
								guiBuilder.action("action.text.browse", GeneralAnchor.browseKey).tooltip("data.jar.organisation.url").params("data.jar.organisation.url"),//
								guiBuilder.action("action.text.edit", ArtifactsAnchor.organisationKey, "overlay.edit").tooltip("action.edit.tooltip").params("data.organisation.url"))),//
				guiBuilder.smallButton("smallButton.organisation.twitter", "smallButton.organisation.twitter.title", "smallButton.normal", ArtifactsAnchor.twitterKey, //
						guiBuilder.listDisplayer("displayer.list", "listEditor.nameAndEmail").title("organisation.twitter.title").data("data.organisation.twitter").//
								actions(LargeButtonMakers.makeMainListActions(guiBuilder, ArtifactsAnchor.twitterKey)).listActions(LargeButtonMakers.listDeleteAction(guiBuilder, ArtifactsAnchor.twitterKey))));
	}

}
