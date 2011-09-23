package org.softwareFm.configuration.largebuttons;

import org.softwareFm.configuration.configurators.LargeButtonFactory;
import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;

public class ProjectSocialLargeButtonFactory extends LargeButtonFactory {

	@Override
	public LargeButtonDefn apply(GuiBuilder guiBuilder) throws Exception {
		return guiBuilder.largeButton("largeButton.project.social", //
				guiBuilder.smallButton("smallButton.project.rss", "smallButton.project.rss.title", "smallButton.data", ArtifactsAnchor.rssKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditorId.tweet").guard(dataJarProjectUrl, blankKey).title("project.rss.add").data(dataProjectRss).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.rssKey, dataProjectRss)).//
								listActions(guiBuilder.action("action.list.viewTweets", GeneralAnchor.browseKey).tooltip("action.browse.tooltip"), //
										listEditAction(guiBuilder, ArtifactsAnchor.rssKey),//
										listDeleteAction(guiBuilder, ArtifactsAnchor.rssKey))).data(dataProjectRss),//
				guiBuilder.smallButton("smallButton.project.twitter", "smallButton.project.twitter.title", "smallButton.data", ArtifactsAnchor.twitterKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditorId.tweet").guard(dataJarProjectUrl, blankKey).title("project.twitter.add").data(dataProjectTweets).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.twitterKey, dataProjectTweets)).//
								listActions(guiBuilder.action("action.list.viewTweets", GeneralAnchor.browseKey).tooltip("action.browse.tooltip"), //
										listEditAction(guiBuilder, ArtifactsAnchor.twitterKey),//
										listDeleteAction(guiBuilder, ArtifactsAnchor.twitterKey))).data(dataProjectTweets),//
				guiBuilder.smallButton("smallButton.project.facebook", "smallButton.project.facebook.title", "smallButton.data", ArtifactsAnchor.facebookKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditorId.tweet").guard(dataJarProjectUrl, blankKey).title("project.facebook.add").data(dataProjectFacebook).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.facebookKey, dataProjectFacebook)).//
								listActions(guiBuilder.action("action.list.viewTweets", GeneralAnchor.browseKey).tooltip("action.browse.tooltip"), //
										listEditAction(guiBuilder, ArtifactsAnchor.facebookKey),//
										listDeleteAction(guiBuilder, ArtifactsAnchor.facebookKey))).data(dataProjectFacebook));
	}

}
