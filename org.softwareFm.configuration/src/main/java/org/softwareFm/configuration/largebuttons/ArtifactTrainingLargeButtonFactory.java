package org.softwareFm.configuration.largebuttons;

import java.io.File;

import org.softwareFm.configuration.configurators.LargeButtonFactory;
import org.softwareFm.configuration.fixture.SoftwareFmFixture;
import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.eclipse.sample.SoftwareFmViewUnit;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.utilities.functions.IFunction1;

public class ArtifactTrainingLargeButtonFactory extends LargeButtonFactory {

	@Override
	public LargeButtonDefn apply(GuiBuilder guiBuilder) throws Exception {

		return guiBuilder.largeButton("largeButton.artifact.training", //
				makeSfmSmallButton(guiBuilder, "smallButton.data", GeneralAnchor.sfmKey, "smallButton.artifact.training.details", "smallButton.artifact.details.title", false),//

				guiBuilder.smallButton("smallButton.artifact.tutorials", "smallButton.artifact.tutorials.title", "smallButton.data", ArtifactsAnchor.tutorialsKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditorId.tutorials").guard(dataJarGroupId, blankKey).title(artifactTutorialsTitle).data(dataArtifactTutorials).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.tutorialsKey, dataArtifactTutorials)).//
								listActions(listEditAction(guiBuilder, ArtifactsAnchor.tutorialsKey), listDeleteAction(guiBuilder, ArtifactsAnchor.tutorialsKey))).data(dataArtifactTutorials),// ), //

				guiBuilder.smallButton("smallButton.artifact.jobs", "smallButton.artifact.jobs.title", "smallButton.data", ArtifactsAnchor.jobKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditorId.job").title(artifactJobTitle).data(dataArtifactJob).//
								actions(//
								makeMainListActions(guiBuilder, ArtifactsAnchor.jobKey, dataArtifactJob)).//
								listActions(listEditAction(guiBuilder, ArtifactsAnchor.jobKey), listDeleteAction(guiBuilder, ArtifactsAnchor.jobKey))).data(dataArtifactJob),// ), //

				guiBuilder.smallButton("smallButton.artifact.blogs", "smallButton.artifact.blogs.title", "smallButton.data", ArtifactsAnchor.tutorialsKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditorId.blogs").guard(dataJarGroupId, blankKey).title(artifactBlogsTitle).data(dataArtifactBlogs).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.tutorialsKey, dataArtifactBlogs)).//
								listActions(listEditAction(guiBuilder, ArtifactsAnchor.tutorialsKey), listDeleteAction(guiBuilder, ArtifactsAnchor.tutorialsKey))).data(dataArtifactBlogs).//
						ctrlClickAction("action.text.browse", dataArtifactBlogs));
	}

	public static void main(String[] args) {
		final File root = new File("../org.softwareFm.configuration/src/test/resources/org/softwareFm/configuration");
		Swts.xUnit(ArtifactTrainingLargeButtonFactory.class.getSimpleName(), root, "json", new SoftwareFmViewUnit.SoftwareFmViewUnitBuilder(new IFunction1<SoftwareFmFixture, LargeButtonDefn[]>() {
			@Override
			public LargeButtonDefn[] apply(SoftwareFmFixture from) throws Exception {
				return new LargeButtonDefn[] { from.artifactTrainingButton };
			}
		}));
	}
}
