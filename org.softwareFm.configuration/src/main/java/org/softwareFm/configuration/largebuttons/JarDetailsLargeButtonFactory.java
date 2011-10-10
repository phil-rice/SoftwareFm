package org.softwareFm.configuration.largebuttons;

import java.io.File;

import org.softwareFm.configuration.configurators.LargeButtonFactory;
import org.softwareFm.configuration.fixture.SoftwareFmFixture;
import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.eclipse.sample.SoftwareFmViewUnit;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.utilities.functions.IFunction1;

public class JarDetailsLargeButtonFactory extends LargeButtonFactory {

	@Override
	public LargeButtonDefn apply(GuiBuilder guiBuilder) throws Exception {
		return guiBuilder.largeButton("largeButton.jar",//
				makeSfmSmallButton(guiBuilder, "smallButton.data", GeneralAnchor.sfmKey, "smallButton.jar.identifiers", "smallButton.jar.identifiers.title", true,//

						guiBuilder.displayer("displayer.compressed.text").editor(editorTextKey).title(artifactUrlTitle).data(dataArtifactUrl).tooltip(dataArtifactUrl).//
								guard(dataRawHexDigest, blankKey, //
										dataJarArtifactId, softwareFmIdMissingTitle,//
										dataArtifactUrl, artifactUrlMissingTitle),//

						guiBuilder.displayer("displayer.compressed.styled.text").editor(editorStyledTextKey).title(artifactDescriptionTitle).data(dataArtifactDescription).//
								guard(dataRawHexDigest, descriptionOfNoneJarFile, dataJarArtifactId, descriptionWhenIdsNotDefined)));

	}

	public static void main(String[] args) {
		final File root = new File("../org.softwareFm.configuration/src/test/resources/org/softwareFm/configuration");
		SoftwareFmViewUnit.SoftwareFmViewUnitBuilder builder = new SoftwareFmViewUnit.SoftwareFmViewUnitBuilder(new IFunction1<SoftwareFmFixture, LargeButtonDefn[]>() {
			@Override
			public LargeButtonDefn[] apply(SoftwareFmFixture from) throws Exception {
				return new LargeButtonDefn[] { from.jarDetailsButton };
			}
		});
		try {
			Swts.xUnit(JarDetailsLargeButtonFactory.class.getSimpleName(), root, "json", builder);
		} finally {
			builder.shutdown();
		}
	}
}
