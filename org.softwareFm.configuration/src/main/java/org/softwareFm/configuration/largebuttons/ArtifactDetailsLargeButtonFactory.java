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

		return guiBuilder.largeButton("largeButton.jar",//

				guiBuilder.smallButton("smallButton.jar.nameAndDescription", "smallButton.jar.identifiers.title", "smallButton.jar", ArtifactsAnchor.jarKey, //
						guiBuilder.displayer("displayer.text").title(artifactNameTitle).data(dataArtifactName).//
								guard(dataRawHexDigest, blankKey, dataArtifactArtifactId, artifactIdMissingTitle, dataArtifactName, artifactNameMissingTitle).//
								actions(editValueButton(guiBuilder, ArtifactsAnchor.projectKey)//
								), //
						guiBuilder.displayer("displayer.text").title(artifactDescriptionTitle).data(dataArtifactDescription).//
								guard(dataRawHexDigest, blankKey, dataArtifactArtifactId, artifactIdMissingTitle).//
								actions(editValueButton(guiBuilder, ArtifactsAnchor.projectKey)),//
						guiBuilder.displayer("displayer.text").title(artifactUrlTitle).data(dataArtifactUrl).//
								guard(dataRawHexDigest, blankKey, dataArtifactArtifactId, artifactIdMissingTitle).//
								actions(editValueButton(guiBuilder, ArtifactsAnchor.projectKey))),//

				guiBuilder.smallButton("smallButton.jar.details", "smallButton.jar.details.title", "smallButton.jar", ArtifactsAnchor.jarKey, //
						guiBuilder.displayer("displayer.text").title(jarHexDigestTitle).data(dataRawHexDigest).//
								guard(dataRawHexDigest, hexDigestMissingTitle), //
						guiBuilder.displayer("displayer.text").title(jarNameTitle).data(dataRawJarName).tooltip(dataRawJarPath).//
								guard(dataRawHexDigest, hexDigestMissingTitle).actions(//
										guiBuilder.action("action.url.search", GeneralAnchor.searchKey).tooltip("action.search.tooltip")), //
						guiBuilder.displayer("displayer.button.javadoc").title(artifactJavadocTitle).guard(dataRawJavadoc, blankKey).tooltip(dataRawJavadoc).//
								actions(guiBuilder.action("action.javadocSource.nuke", GeneralAnchor.clearKey),//
										guiBuilder.action("action.javadoc.view", ArtifactsAnchor.jarKey).tooltip("action.javadoc.view.tooltip").ignoreGuard(),//
										browseButton(guiBuilder, dataRawJavadoc)), //

						guiBuilder.displayer("displayer.button.source").title(artifactSourceTitle).guard(dataRawSource, blankKey).tooltip(dataRawSource).//
								actions(guiBuilder.action("action.source.view", ArtifactsAnchor.jarKey).tooltip("action.source.view.tooltip").ignoreGuard(),//
										browseButton(guiBuilder, dataRawJavadoc))), //

				guiBuilder.smallButton("smallButton.jar.identifiers", "smallButton.jar.identifiers.title", "smallButton.jar", ArtifactsAnchor.jarKey, //
						guiBuilder.displayer("displayer.text").title(artifactIdTitle).data(dataArtifactArtifactId).//
								guard(dataRawHexDigest, blankKey, dataArtifactArtifactId, artifactIdMissingTitle).//
								actions(editValueButton(guiBuilder, ArtifactsAnchor.projectKey)//
								), //
						guiBuilder.displayer("displayer.text").title(groupIdTitle).data(dataArtifactGroupId).//
								guard(dataRawHexDigest, blankKey, dataArtifactGroupId, groupIdMissingTitle).//
								actions(editValueButton(guiBuilder, ArtifactsAnchor.projectKey)),//
						guiBuilder.displayer("displayer.text").title(groupVersionTitle).data(dataArtifactVersion).//
								guard(dataRawHexDigest, blankKey, dataArtifactVersion, groupIdMissingTitle).//
								actions(editValueButton(guiBuilder, ArtifactsAnchor.projectKey))

				),//

				guiBuilder.smallButton("smallButton.project.issues", "smallButton.project.issues.title", "smallButton.data", ArtifactsAnchor.issuesKey,//
						guiBuilder.displayer("displayer.url").guard(dataArtifactGroupId, blankKey).title(artifactIssuesTitle).data(dataArtifactIssues).tooltip(artifactIssuesTitle).actions(//
								browseButton(guiBuilder), //
								editTextButton(guiBuilder, ArtifactsAnchor.issuesKey))).data(dataArtifactIssues).tooltip(dataArtifactIssues), // ,//

				guiBuilder.smallButton("smallButton.project.mailingLists", "smallButton.project.mailingLists.title", "smallButton.data", ArtifactsAnchor.mailingListKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditorId.nameAndEmail").guard(dataArtifactGroupId, blankKey).title(artifactMailingListTitle).data(dataArtifactMailingList).//
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
