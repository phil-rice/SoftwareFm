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
						guiBuilder.displayer("displayer.url").title("organisation.url.title").data(dataOrganisationName).//
								guard(dataRawHexDigest, hexDigestMissingTitle, dataJarOrganisationUrl, organisationUrlMissingTitle).actions(//
										editUrlButton(guiBuilder, dataJarOrganisationUrl),//
										editNameButton(guiBuilder, ArtifactsAnchor.organisationKey),//
										browseButton(guiBuilder, dataJarOrganisationUrl)), //
						guiBuilder.displayer("displayer.text").guard(dataJarOrganisationUrl, blankKey, dataOrganisationName, organisationNameMissingTitle).title("organisation.name.title").data(dataOrganisationName).actions(//
								editTextButton(guiBuilder, ArtifactsAnchor.organisationKey))).data(dataJarOrganisationUrl),//
				guiBuilder.smallButton("smallButton.organisation.rss", "smallButton.project.rss.title", "smallButton.data", ArtifactsAnchor.rssKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditorId.rss").guard(dataJarProjectUrl, blankKey).title("organisation.rss.add").data(dataOrganisationRss).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.rssKey, dataOrganisationRss)).//
								listActions(guiBuilder.action("action.rss.browse", GeneralAnchor.browseKey).tooltip("action.browse.tooltip"), //
										listEditAction(guiBuilder, ArtifactsAnchor.rssKey),//
										listDeleteAction(guiBuilder, ArtifactsAnchor.rssKey))).data(dataOrganisationRss), //
				guiBuilder.smallButton("smallButton.organisation.twitter", "smallButton.organisation.twitter.title", "smallButton.data", ArtifactsAnchor.twitterKey, //
						guiBuilder.listDisplayer("displayer.list", "listEditorId.tweet").guard(dataJarOrganisationUrl, blankKey).title("organisation.twitter.add").data(dataOrganisationTweets).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.twitterKey, dataOrganisationTweets)).//
								listActions(guiBuilder.action("action.list.viewTweets", GeneralAnchor.browseKey).params(dataOrganisationTweets).tooltip("action.browse.tooltip"), //
										listEditAction(guiBuilder, ArtifactsAnchor.twitterKey),//
										listDeleteAction(guiBuilder, ArtifactsAnchor.twitterKey))).data(dataOrganisationTweets));
	}

}
