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

public class ArtifactDetailsLargeButtonFactory extends LargeButtonFactory {

	@Override
	public LargeButtonDefn apply(GuiBuilder guiBuilder) throws Exception {

		return guiBuilder.largeButton("largeButton.artifact.details",//

				makeSfmSmallButton(guiBuilder, "smallButton.artifact.nameAndDescription", "smallButton.jar.identifiers.title"),//

				guiBuilder.smallButton("smallButton.artifact.details", "smallButton.jar.details.title", "smallButton.jar", ArtifactsAnchor.jarKey, //
						guiBuilder.displayer("displayer.text").title(jarHexDigestTitle).data(dataRawHexDigest), guiBuilder.displayer("displayer.text").title(jarNameTitle).data(dataRawJarName).tooltip(dataRawJarPath).actions(//
								guiBuilder.action("action.url.search", GeneralAnchor.searchKey).tooltip("action.search.tooltip")), //

						guiBuilder.displayer("displayer.button.javadoc").title(artifactJavadocTitle).guard(dataRawJavadoc, blankKey).tooltip(dataRawJavadoc).//
								actions(guiBuilder.action("action.javadocSource.nuke", GeneralAnchor.clearKey),//
										guiBuilder.action("action.javadoc.view", ArtifactsAnchor.jarKey).tooltip("action.javadoc.view.tooltip").ignoreGuard(),//
										browseButton(guiBuilder, dataRawJavadoc)), //

						guiBuilder.displayer("displayer.button.source").title(artifactSourceTitle).guard(dataRawSource, blankKey).tooltip(dataRawSource).//
								actions(guiBuilder.action("action.source.view", ArtifactsAnchor.jarKey).tooltip("action.source.view.tooltip").ignoreGuard(),//
										browseButton(guiBuilder, dataRawJavadoc))),//

				guiBuilder.smallButton("smallButton.project.issues", "smallButton.project.issues.title", "smallButton.data", ArtifactsAnchor.issuesKey,//
						guiBuilder.displayer("displayer.url").guard(dataJarGroupId, blankKey).title(artifactIssuesTitle).data(dataArtifactIssues).tooltip(artifactIssuesTitle).actions(//
								browseButton(guiBuilder), //
								editTextButton(guiBuilder, ArtifactsAnchor.issuesKey))).data(dataArtifactIssues).tooltip(dataArtifactIssues), // ,//

				guiBuilder.smallButton("smallButton.project.mailingLists", "smallButton.project.mailingLists.title", "smallButton.data", ArtifactsAnchor.mailingListKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditorId.nameAndEmail").guard(dataJarGroupId, blankKey).title(artifactMailingListTitle).data(dataArtifactMailingList).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.mailingListKey, dataArtifactMailingList)).//
								listActions(listEditAction(guiBuilder, ArtifactsAnchor.mailingListKey), listDeleteAction(guiBuilder, ArtifactsAnchor.mailingListKey))).//
						ctrlClickAction("action.text.browse", dataArtifactMailingList).data(dataArtifactMailingList));
	}

	public static void main(String[] args) {
		final File root = new File("../org.softwareFm.configuration/src/test/resources/org/softwareFm/configuration");
		Swts.xUnit(ArtifactDetailsLargeButtonFactory.class.getSimpleName(), root, "json", new SoftwareFmViewUnit.SoftwareFmViewUnitBuilder(new IFunction1<SoftwareFmFixture, LargeButtonDefn[]>() {
			@Override
			public LargeButtonDefn[] apply(SoftwareFmFixture from) throws Exception {
				return new LargeButtonDefn[] { from.artifactDetailsButton };
			}
		}));
	}
}
