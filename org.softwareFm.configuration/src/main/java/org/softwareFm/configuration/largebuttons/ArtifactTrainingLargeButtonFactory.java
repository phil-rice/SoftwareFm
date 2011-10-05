package org.softwareFm.configuration.largebuttons;

import java.io.File;

import org.softwareFm.configuration.configurators.LargeButtonFactory;
import org.softwareFm.configuration.fixture.SoftwareFmFixture;
import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.eclipse.sample.SoftwareFmViewUnit;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.utilities.functions.IFunction1;

public class ArtifactTrainingLargeButtonFactory extends LargeButtonFactory {

	@Override
	public LargeButtonDefn apply(GuiBuilder guiBuilder) throws Exception {

		return guiBuilder.largeButton("largeButton.group.training", //
				guiBuilder.smallButton("smallButton.project.social.details", "smallButton.group.details.title", "smallButton.data", ArtifactsAnchor.projectKey, //
						guiBuilder.displayer("displayer.url").title(artifactNameTitle).data(dataArtifactName).tooltip(dataArtifactGroupId).//
								guard(dataRawHexDigest, hexDigestMissingTitle, dataArtifactGroupId, artifactIdMissingTitle).actions(editUrlButton(guiBuilder, dataArtifactGroupId),//
										editValueButton(guiBuilder, ArtifactsAnchor.projectKey),//
										browseButton(guiBuilder, dataArtifactGroupId)//
								)).data(dataArtifactName),//
				guiBuilder.smallButton("smallButton.group.tutorials", "smallButton.group.tutorials.title", "smallButton.data", ArtifactsAnchor.tutorialsKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditorId.tutorials").guard(dataArtifactGroupId, blankKey).title(artifactTutorialsTitle).data(dataArtifactTutorials).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.tutorialsKey, dataArtifactTutorials)).//
								listActions(listEditAction(guiBuilder, ArtifactsAnchor.tutorialsKey), listDeleteAction(guiBuilder, ArtifactsAnchor.tutorialsKey))).data(dataArtifactTutorials),// ), //

				guiBuilder.smallButton("smallButton.group.blogs", "smallButton.group.blogs.title", "smallButton.data", ArtifactsAnchor.tutorialsKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditorId.blogs").guard(dataArtifactGroupId, blankKey).title(artifactBlogsTitle).data(dataArtifactBlogs).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.tutorialsKey, dataArtifactBlogs)).//
								listActions(listEditAction(guiBuilder, ArtifactsAnchor.tutorialsKey), listDeleteAction(guiBuilder, ArtifactsAnchor.tutorialsKey))).data(dataArtifactBlogs).//
						ctrlClickAction("action.text.browse", dataArtifactBlogs));
	}	public static void main(String[] args) {
		final File root = new File("../org.softwareFm.configuration/src/test/resources/org/softwareFm/configuration");
		Swts.xUnit(ArtifactTrainingLargeButtonFactory.class.getSimpleName(), root, "json", new SoftwareFmViewUnit.SoftwareFmViewUnitBuilder(new IFunction1<SoftwareFmFixture, LargeButtonDefn[]>() {
			@Override
			public LargeButtonDefn[] apply(SoftwareFmFixture from) throws Exception {
				return new LargeButtonDefn[] { from.artifactTrainingButton };
			}
		}));
	}
}
