package org.softwareFm.configuration.largebuttons;

import org.softwareFm.configuration.configurators.LargeButtonFactory;
import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;

public class JarLargeButtonFactory extends LargeButtonFactory {

	@Override
	public LargeButtonDefn apply(GuiBuilder guiBuilder) throws Exception {

		return guiBuilder.largeButton("largeButton.jar",//
				guiBuilder.smallButton("smallButton.jar.details", "smallButton.jar.details.title", "smallButton.jar", ArtifactsAnchor.jarKey, //
						guiBuilder.displayer("displayer.text").title("jar.jarName.title").data(dataRawJarName).tooltip(dataRawJarPath).guard(dataRawHexDigest, hexDigestMissingTitle), //
						guiBuilder.displayer("displayer.button.javadoc").title("jar.javadoc.title").guard(dataRawJavadoc, blankKey).tooltip(dataRawJavadoc).//
								actions(browseButton(guiBuilder, dataRawJavadoc)), //
						guiBuilder.displayer("displayer.button.source").title("jar.source.title").guard(dataRawSource, blankKey).tooltip(dataRawSource).//
								actions(browseButton(guiBuilder, dataRawJavadoc)), //
						guiBuilder.displayer("displayer.text").title("project.name.title").data(dataProjectName).//
								guard(dataRawHexDigest, blankKey, dataJarProjectUrl, projectUrlMissingTitle, dataProjectName, projectNameMissingTitle).//
								actions(editUrlButton(guiBuilder, ArtifactsAnchor.projectKey, dataJarProjectUrl),//
										browseButton(guiBuilder, "data.jar.project.url")//
								).tooltip("data.jar.project.url"),//
						guiBuilder.displayer("displayer.text").title("organisation.name.title").data(dataOrganisationName).//
								guard(dataRawHexDigest, blankKey, dataJarOrganisationUrl, organisationUrlMissingTitle, dataOrganisationName, organisationNameMissingTitle).//
								actions(//
								editUrlButton(guiBuilder, ArtifactsAnchor.organisationKey, dataJarOrganisationUrl),//
								browseButton(guiBuilder, dataJarOrganisationUrl)//
								).tooltip("data.jar.organisation.url")//
						));
	}

}
