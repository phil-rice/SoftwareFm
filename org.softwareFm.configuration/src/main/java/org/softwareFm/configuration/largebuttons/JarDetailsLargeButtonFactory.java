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

public class JarDetailsLargeButtonFactory extends LargeButtonFactory {

	@Override
	public LargeButtonDefn apply(GuiBuilder guiBuilder) throws Exception {

		return guiBuilder.largeButton("largeButton.jar",//

				guiBuilder.smallButton("smallButton.jar.identifiers", "smallButton.jar.identifiers.title", "smallButton.jar", GeneralAnchor.sfmKey, //
						guiBuilder.displayer("displayer.text").title(groupIdTitle).data(dataJarGroupId).//
								guard(dataRawHexDigest, blankKey, dataJarGroupId, groupIdMissingTitle).//
								actions(editTextButton(guiBuilder, GeneralAnchor.sfmKey)),//
						guiBuilder.displayer("displayer.text").title(artifactIdTitle).data(dataJarArtifactId).//
								guard(dataRawHexDigest, blankKey, dataJarArtifactId, artifactIdMissingTitle).//
								actions(editTextButton(guiBuilder, GeneralAnchor.sfmKey)), //
						guiBuilder.displayer("displayer.text").title(groupVersionTitle).data(dataJarVersion).//
								guard(dataRawHexDigest, blankKey, dataJarVersion, versionMissingTitle).//
								actions(editTextButton(guiBuilder, GeneralAnchor.sfmKey))),

				guiBuilder.smallButton("smallButton.jar.nameAndDescription", "smallButton.jar.identifiers.title", "smallButton.jar", ArtifactsAnchor.projectKey, //
						guiBuilder.displayer("displayer.text").title(artifactNameTitle).data(dataArtifactName).//
								guard(dataRawHexDigest, blankKey, dataJarArtifactId, artifactIdMissingTitle, dataArtifactName, artifactNameMissingTitle).//
								actions(editTextButton(guiBuilder, ArtifactsAnchor.projectKey)//
								), //
						guiBuilder.displayer("displayer.text").title(artifactUrlTitle).data(dataArtifactUrl).//
								guard(dataRawHexDigest, blankKey, dataJarArtifactId, artifactIdMissingTitle).//
								actions(editUrlButton(guiBuilder)), //
						guiBuilder.displayer("displayer.styled.text").title(artifactDescriptionTitle).data(dataArtifactDescription).//
								guard(dataRawHexDigest, descriptionOfNoneJarFile, dataJarArtifactId, descriptionWhenIdsNotDefined).//
								actions(browseButton(guiBuilder), editStyledTextButton(guiBuilder, ArtifactsAnchor.projectKey)))//
				);//

	}

	public static void main(String[] args) {
		final File root = new File("../org.softwareFm.configuration/src/test/resources/org/softwareFm/configuration");
		Swts.xUnit(JarDetailsLargeButtonFactory.class.getSimpleName(), root, "json", new SoftwareFmViewUnit.SoftwareFmViewUnitBuilder(new IFunction1<SoftwareFmFixture, LargeButtonDefn[]>() {
			@Override
			public LargeButtonDefn[] apply(SoftwareFmFixture from) throws Exception {
				return new LargeButtonDefn[] { from.artifactDetailsButton };
			}
		}));
	}
}
