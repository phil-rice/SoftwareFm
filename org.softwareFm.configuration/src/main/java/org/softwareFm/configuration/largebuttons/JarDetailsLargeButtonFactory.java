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

public class JarDetailsLargeButtonFactory extends LargeButtonFactory {

	@Override
	public LargeButtonDefn apply(GuiBuilder guiBuilder) throws Exception {

		return guiBuilder.largeButton("largeButton.jar",//
				makeSfmSmallButton(guiBuilder, "smallButton.jar.identifiers", "smallButton.jar.identifiers.title"),//

				guiBuilder.smallButton("smallButton.jar.nameAndDescription", "smallButton.jar.identifiers.title", "smallButton.jar", ArtifactsAnchor.projectKey, //
						guiBuilder.displayer("displayer.compressed.text").title(artifactUrlTitle).data(dataArtifactUrl).tooltip(artifactUrlTitle).//
								guard(dataRawHexDigest, blankKey, dataJarArtifactId, artifactIdMissingTitle).//
								actions(editUrlButton(guiBuilder)), //
						guiBuilder.displayer("displayer.compressed.styled.text").title(artifactDescriptionTitle).data(dataArtifactDescription).//
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
