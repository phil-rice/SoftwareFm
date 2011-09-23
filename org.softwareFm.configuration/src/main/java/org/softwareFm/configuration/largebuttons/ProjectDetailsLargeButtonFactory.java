package org.softwareFm.configuration.largebuttons;

import org.softwareFm.configuration.configurators.LargeButtonFactory;
import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;

public class ProjectDetailsLargeButtonFactory extends LargeButtonFactory {

	@Override
	public LargeButtonDefn apply(GuiBuilder guiBuilder) throws Exception {
		return guiBuilder.largeButton("largeButton.project.details", //
				guiBuilder.smallButton("smallButton.project.details", "smallButton.project.details.title", "smallButton.data", ArtifactsAnchor.projectKey, //
						guiBuilder.displayer("displayer.url").title("project.name.title").data(dataProjectName).tooltip(dataJarProjectUrl).//
								guard(dataRawHexDigest, hexDigestMissingTitle, dataJarProjectUrl, projectUrlMissingTitle).actions(//
										browseButton(guiBuilder, dataJarProjectUrl),//
										editTextButton(guiBuilder, ArtifactsAnchor.projectKey))).data(dataJarProjectUrl).//
						ctrlClickAction("action.text.browse", "data.jar.projectUrl").tooltip("smallButton.project.details.tooltip"),//
				guiBuilder.smallButton("smallButton.project.issues", "smallButton.project.issues.title", "smallButton.data", ArtifactsAnchor.issuesKey,//
						guiBuilder.displayer("displayer.url").guard(dataJarProjectUrl, blankKey).title("project.issues.title").data(dataProjectIssues).tooltip("project.issues.tooltip").actions(//
								browseButton(guiBuilder), //
								editTextButton(guiBuilder, ArtifactsAnchor.issuesKey))).data(dataProjectIssues), // ,//
				guiBuilder.smallButton("smallButton.project.mailingLists", "smallButton.project.mailingLists.title", "smallButton.data", ArtifactsAnchor.mailingListKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditorId.nameAndEmail").guard(dataJarProjectUrl, blankKey).title("project.mailingList.title").data(dataProjectMailingList).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.mailingListKey, dataProjectMailingList)).//
								listActions(listEditAction(guiBuilder, ArtifactsAnchor.mailingListKey), listDeleteAction(guiBuilder, ArtifactsAnchor.mailingListKey))).//
						ctrlClickAction("action.text.browse", dataProjectIssues).data(dataProjectMailingList));
	}
}
