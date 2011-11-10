package org.softwareFm.configuration.largebuttons;

import java.io.File;

import org.softwareFm.configuration.configurators.LargeButtonFactory;
import org.softwareFm.configuration.fixture.SoftwareFmFixture;
import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.eclipse.sample.SoftwareFmViewUnit;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.utilities.functions.IFunction1;

public class ArtifactDetailsLargeButtonFactory extends LargeButtonFactory {

	@Override
	public LargeButtonDefn apply(GuiBuilder guiBuilder) throws Exception {

		return guiBuilder.largeButton("largeButton.artifact.details",//
				makeSfmSmallButton(guiBuilder, "smallButton.jar", ArtifactsAnchor.jarKey, "smallButton.artifact.nameAndDescription", "smallButton.jar.identifiers.title", false,//
						guiBuilder.displayer("displayer.summary.javadoc").icon(ArtifactsAnchor.javadocKey).editor(editorJavadocKey).help("javadoc.help").title(artifactJavadocTitle).guard(dataRawJavadoc, blankKey).tooltip(dataRawJavadoc), //
						guiBuilder.displayer("displayer.summary.javadoc").icon(ArtifactsAnchor.sourceKey).editor(editorSourceKey).help("source.help").title(artifactSourceTitle).guard(dataRawSource, blankKey).tooltip(dataRawSource)),//

				guiBuilder.smallButton("smallButton.project.issues", "smallButton.project.issues.title", "smallButton.data", ArtifactsAnchor.issuesKey,//
						guiBuilder.displayer("displayer.compressed.text").guard(dataJarGroupId, blankKey).editor(editorTextKey).title(artifactIssuesTitle).help("issues.help").//
								data(dataArtifactIssues).tooltip(artifactIssuesTitle).actions(//
										browseButton(guiBuilder))).data(dataArtifactIssues).tooltip(dataArtifactIssues), // ,//

				guiBuilder.smallButton("smallButton.project.mailingLists", "smallButton.project.mailingLists.title", "smallButton.data", ArtifactsAnchor.mailingListKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditorId.nameAndEmail").guard(dataJarGroupId, blankKey).title(artifactMailingListTitle).data(dataArtifactMailingList).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.mailingListKey, dataArtifactMailingList)).//
								listActions(listEditAction(guiBuilder, ArtifactsAnchor.mailingListKey), listDeleteAction(guiBuilder, ArtifactsAnchor.mailingListKey))).//
						ctrlClickAction("action.text.browse", dataArtifactMailingList).data(dataArtifactMailingList));
	}

	public static void main(String[] args) {
		final File root = new File("../org.softwareFm.configuration/src/test/resources/org/softwareFm/configuration");
		SoftwareFmViewUnit.SoftwareFmViewUnitBuilder builder = new SoftwareFmViewUnit.SoftwareFmViewUnitBuilder(new IFunction1<SoftwareFmFixture, LargeButtonDefn[]>() {
			@Override
			public LargeButtonDefn[] apply(SoftwareFmFixture from) throws Exception {
				return new LargeButtonDefn[] { from.artifactDetailsButton };
			}
		});
		try {
			Show.xUnit(ArtifactDetailsLargeButtonFactory.class.getSimpleName(), root, "json", builder);
		} finally {
			builder.shutdown();
		}
	}
}
