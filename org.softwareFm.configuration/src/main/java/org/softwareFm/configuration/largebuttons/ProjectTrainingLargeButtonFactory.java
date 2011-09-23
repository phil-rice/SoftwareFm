package org.softwareFm.configuration.largebuttons;

import org.softwareFm.configuration.configurators.LargeButtonFactory;
import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;

public class ProjectTrainingLargeButtonFactory extends LargeButtonFactory {

	@Override
	public LargeButtonDefn apply(GuiBuilder guiBuilder) throws Exception {
		return guiBuilder.largeButton("largeButton.project.training", //
				guiBuilder.smallButton("smallButton.project.tutorials", "smallButton.project.tutorials.title", "smallButton.data", ArtifactsAnchor.tutorialsKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditorId.nameAndEmail").guard(dataJarProjectUrl, blankKey).title("project.tutorials.title").data(dataProjectTutorials).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.tutorialsKey, dataProjectTutorials)).//
								listActions(listEditAction(guiBuilder, ArtifactsAnchor.tutorialsKey), listDeleteAction(guiBuilder, ArtifactsAnchor.tutorialsKey))).data(dataProjectTutorials),// ), //

				guiBuilder.smallButton("smallButton.project.blogs", "smallButton.project.blogs.title", "smallButton.data", ArtifactsAnchor.tutorialsKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditorId.nameAndEmail").guard(dataJarProjectUrl, blankKey).title("project.blogs.title").data(dataProjectBlogs).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.tutorialsKey, dataProjectBlogs)).//
								listActions(listEditAction(guiBuilder, ArtifactsAnchor.tutorialsKey), listDeleteAction(guiBuilder, ArtifactsAnchor.tutorialsKey))).data(dataProjectBlogs).//
						ctrlClickAction("action.text.browse", dataProjectIssues));
	}
}
