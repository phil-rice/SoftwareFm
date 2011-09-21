package org.softwareFm.configuration.configurators;

import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;

public class OrganisationLargeButtonFactory extends LargeButtonFactory {

	@Override
	public LargeButtonDefn apply(GuiBuilder guiBuilder) throws Exception {
		return guiBuilder.largeButton("largeButton.organisation", //
				guiBuilder.smallButton("smallButton.organisation.details", "smallButton.organisation.details.title", "smallButton.normal", ArtifactsAnchor.organisationKey,//
						guiBuilder.displayer("displayer.url").title("organisation.url.title").data(dataJarOrganisationUrl).actions(//
								guiBuilder.action("action.text.browse", GeneralAnchor.browseKey).tooltip("action.browse.tooltip").params(dataJarOrganisationUrl),//
								guiBuilder.action("action.text.edit", ArtifactsAnchor.organisationKey, "overlay.edit").tooltip("action.edit.tooltip").params(dataJarOrganisationUrl)), //
						guiBuilder.displayer("displayer.text").title("organisation.name.title").data(dataOrganisationName).actions(//
								guiBuilder.action("action.text.edit", ArtifactsAnchor.organisationKey, "overlay.edit").tooltip("action.edit.tooltip").params(dataOrganisationName))),//
				guiBuilder.smallButton("smallButton.organisation.twitter", "smallButton.organisation.twitter.title", "smallButton.normal", ArtifactsAnchor.twitterKey, //
						guiBuilder.listDisplayer("displayer.list", "listEditorId.tweet").title("organisation.twitter.add").data(dataOrganisationTweets).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.twitterKey, dataOrganisationTweets)).//
								listActions(guiBuilder.action("action.list.viewTweets", GeneralAnchor.browseKey).params(dataOrganisationTweets).tooltip("action.browse.tooltip"), //
										listDeleteAction(guiBuilder, ArtifactsAnchor.twitterKey, dataOrganisationTweets))));
	}

}
