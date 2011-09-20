package org.softwareFm.configuration.configurators;

import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.largeButton.ILargeButtonFactory;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;

public class ProjectLargeButtonFactory implements ILargeButtonFactory {

	@Override
	public LargeButtonDefn apply(GuiBuilder guiBuilder) throws Exception {
		return guiBuilder.largeButton("largeButton.project", //
				guiBuilder.smallButton("smallButton.project.details", "smallButton.project.details.title", "smallButton.normal", ArtifactsAnchor.projectKey, //
						guiBuilder.displayer("displayer.text").title("project.name.title").data("data.project.name").tooltip("data.project.description").actions(//
								guiBuilder.action("action.text.edit", ArtifactsAnchor.projectKey, "overlay.edit").tooltip("action.edit.tooltip").params("data.project.name")), //
						guiBuilder.displayer("displayer.url").title("project.url.title").data("data.jar.project.url")).//
						ctrlClickAction("action.text.browse", "data.jar.projectUrl").tooltip("smallButton.project.details.tooltip"),//
				guiBuilder.smallButton("smallButton.project.bugs", "smallButton.project.bugs.title", "smallButton.normal", ArtifactsAnchor.issuesKey,//
						guiBuilder.displayer("displayer.url").title("project.issues.title").data("data.project.issues").tooltip("project.issues.tooltip").actions(//
								guiBuilder.action("action.text.edit", "artifact.issues", "overlay.edit").tooltip("action.edit.tooltip").params("data.project.issues")), // ,//
						guiBuilder.listDisplayer("displayer.list", "listEditor.nameAndEmail").title("project.mailingList.title").data("data.project.mailingList").//
								actions(LargeButtonMakers.makeMainListActions(guiBuilder, ArtifactsAnchor.mailingListKey)).listActions(LargeButtonMakers.listDeleteAction(guiBuilder, ArtifactsAnchor.mailingListKey))).//
						ctrlClickAction("action.text.browse", "data.project.issues"),//
				guiBuilder.smallButton("smallButton.project.twitter", "smallButton.project.twitter.title", "smallButton.normal", ArtifactsAnchor.twitterKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditor.tweet").title("project.twitter.title").data("data.project.twitter").//
								actions(LargeButtonMakers.makeMainListActions(guiBuilder, ArtifactsAnchor.twitterKey)).//
								listActions(guiBuilder.action("action.list.viewTweets", GeneralAnchor.browseKey).params("project.twitter.lineTitle"), LargeButtonMakers.listDeleteAction(guiBuilder, ArtifactsAnchor.twitterKey))));
	}

}
