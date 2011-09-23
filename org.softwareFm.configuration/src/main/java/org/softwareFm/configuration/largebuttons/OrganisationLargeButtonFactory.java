package org.softwareFm.configuration.largebuttons;

import org.softwareFm.configuration.configurators.LargeButtonFactory;
import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;

public class OrganisationLargeButtonFactory extends LargeButtonFactory {

	@Override
	public LargeButtonDefn apply(GuiBuilder guiBuilder) throws Exception {
		return guiBuilder.largeButton("largeButton.organisation", //
				guiBuilder.smallButton("smallButton.organisation.details", "smallButton.organisation.details.title", "smallButton.data", ArtifactsAnchor.organisationKey,//
						guiBuilder.displayer("displayer.url").title("organisation.url.title").data(dataJarOrganisationUrl).//
								guard(dataRawHexDigest, hexDigestMissingTitle, dataJarOrganisationUrl, organisationUrlMissingTitle).actions(//
										browseButton(guiBuilder),//
										editTextButton(guiBuilder, ArtifactsAnchor.organisationKey)), //
						guiBuilder.displayer("displayer.text").guard(dataJarOrganisationUrl, blankKey, dataOrganisationName, organisationNameMissingTitle).title("organisation.name.title").data(dataOrganisationName).actions(//
								editTextButton(guiBuilder, ArtifactsAnchor.organisationKey))).data(dataJarOrganisationUrl),//
				guiBuilder.smallButton("smallButton.organisation.twitter", "smallButton.organisation.twitter.title", "smallButton.data", ArtifactsAnchor.twitterKey, //
						guiBuilder.listDisplayer("displayer.list", "listEditorId.tweet").guard(dataJarOrganisationUrl, blankKey).title("organisation.twitter.add").data(dataOrganisationTweets).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.twitterKey, dataOrganisationTweets)).//
								listActions(guiBuilder.action("action.list.viewTweets", GeneralAnchor.browseKey).params(dataOrganisationTweets).tooltip("action.browse.tooltip"), //
										listEditAction(guiBuilder, ArtifactsAnchor.twitterKey),//
										listDeleteAction(guiBuilder, ArtifactsAnchor.twitterKey))).data(dataOrganisationTweets));
	}

}
