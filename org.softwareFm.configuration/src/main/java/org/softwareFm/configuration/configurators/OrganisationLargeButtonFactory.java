package org.softwareFm.configuration.configurators;

import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;

public class OrganisationLargeButtonFactory extends LargeButtonFactory {

	@Override
	public LargeButtonDefn apply(GuiBuilder guiBuilder) throws Exception {
		return guiBuilder.largeButton("largeButton.organisation", //
				guiBuilder.smallButton("smallButton.organisation.details", "smallButton.organisation.details.title", "smallButton.organisation", ArtifactsAnchor.organisationKey,//
						guiBuilder.displayer("displayer.url").title("organisation.url.title").data(dataJarOrganisationUrl).guard(dataRawJarPath, jarMissingTitle, dataJarOrganisationUrl, organisationUrlMissingTitle).actions(//
								guiBuilder.action("action.text.browse", GeneralAnchor.browseKey).tooltip("action.browse.tooltip"),//
								guiBuilder.action("action.text.edit", ArtifactsAnchor.organisationKey, "overlay.edit").tooltip("action.edit.tooltip").params(dataJarOrganisationUrl)), //
						guiBuilder.displayer("displayer.text").guard(dataJarOrganisationUrl, blankKey, dataOrganisationName, organisationNameMissingTitle).title("organisation.name.title").data(dataOrganisationName).actions(//
								guiBuilder.action("action.text.edit", ArtifactsAnchor.organisationKey, "overlay.edit").tooltip("action.edit.tooltip").params(dataOrganisationName))),//
				guiBuilder.smallButton("smallButton.organisation.twitter", "smallButton.organisation.twitter.title", "smallButton.normal", ArtifactsAnchor.twitterKey, //
						guiBuilder.listDisplayer("displayer.list", "listEditorId.tweet").guard(dataJarOrganisationUrl, blankKey).title("organisation.twitter.add").data(dataOrganisationTweets).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.twitterKey, dataOrganisationTweets)).//
								listActions(guiBuilder.action("action.list.viewTweets", GeneralAnchor.browseKey).params(dataOrganisationTweets).tooltip("action.browse.tooltip"), //
										listEditAction(guiBuilder, ArtifactsAnchor.twitterKey),//
										listDeleteAction(guiBuilder, ArtifactsAnchor.twitterKey))));
	}

}
