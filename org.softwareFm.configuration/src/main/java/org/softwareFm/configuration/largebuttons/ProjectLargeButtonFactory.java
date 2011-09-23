package org.softwareFm.configuration.largebuttons;

import org.softwareFm.configuration.configurators.LargeButtonFactory;
import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;

public class ProjectLargeButtonFactory extends LargeButtonFactory {

	@Override
	public LargeButtonDefn apply(GuiBuilder guiBuilder) throws Exception {

		return guiBuilder.largeButton("largeButton.project", //
				guiBuilder.smallButton("smallButton.project.details", "smallButton.project.details.title", "smallButton.project", ArtifactsAnchor.projectKey, //
						guiBuilder.displayer("displayer.url").title("project.url.title").data(dataJarProjectUrl).//
								guard(dataRawHexDigest, hexDigestMissingTitle, dataJarProjectUrl, projectUrlMissingTitle).actions(//
										browseButton(guiBuilder),//
										editTextButton(guiBuilder, ArtifactsAnchor.projectKey)),//
						guiBuilder.displayer("displayer.text").//
								guard(dataJarProjectUrl, blankKey, dataProjectName, projectNameMissingTitle).title("project.name.title").data(dataProjectName).tooltip("data.project.description").actions(//
										editTextButton(guiBuilder, ArtifactsAnchor.projectKey))).//
						ctrlClickAction("action.text.browse", "data.jar.projectUrl").tooltip("smallButton.project.details.tooltip"),//
				guiBuilder.smallButton("smallButton.project.bugs", "smallButton.project.bugs.title", "smallButton.normal", ArtifactsAnchor.issuesKey,//
						guiBuilder.displayer("displayer.url").guard(dataJarProjectUrl, blankKey).title("project.issues.title").data(dataProjectIssues).tooltip("project.issues.tooltip").actions(//
								browseButton(guiBuilder), //
								editTextButton(guiBuilder, ArtifactsAnchor.issuesKey))), // ,//
				guiBuilder.smallButton("smallButton.project.mailingLists", "smallButton.project.mailingLists.title", "smallButton.normal", ArtifactsAnchor.mailingListKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditorId.nameAndEmail").guard(dataJarProjectUrl, blankKey).title("project.mailingList.title").data(dataProjectMailingList).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.mailingListKey, dataProjectMailingList)).//
								listActions(listEditAction(guiBuilder, ArtifactsAnchor.mailingListKey), listDeleteAction(guiBuilder, ArtifactsAnchor.mailingListKey))).//
						ctrlClickAction("action.text.browse", dataProjectIssues),//
				guiBuilder.smallButton("smallButton.project.twitter", "smallButton.project.twitter.title", "smallButton.normal", ArtifactsAnchor.twitterKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditorId.tweet").guard(dataJarProjectUrl, blankKey).title("project.twitter.add").data(dataProjectTweets).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.twitterKey, dataProjectTweets)).//
								listActions(guiBuilder.action("action.list.viewTweets", GeneralAnchor.browseKey).tooltip("action.browse.tooltip"), //
										listEditAction(guiBuilder, ArtifactsAnchor.twitterKey),//
										listDeleteAction(guiBuilder, ArtifactsAnchor.twitterKey))));
	}

}
