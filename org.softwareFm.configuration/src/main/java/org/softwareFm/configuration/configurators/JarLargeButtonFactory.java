package org.softwareFm.configuration.configurators;

import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;

public class JarLargeButtonFactory extends LargeButtonFactory {

	@Override
	public LargeButtonDefn apply(GuiBuilder guiBuilder) throws Exception {

		return guiBuilder.largeButton("largeButton.jar",//
				guiBuilder.smallButton("smallButton.jar.details", "smallButton.jar.details.title", "smallButton.jar", ArtifactsAnchor.jarKey, //
						guiBuilder.displayer("displayer.text").title("jar.jarPath.title").data(dataRawJarPath).tooltip("jar.jarPath.tooltip").guard(dataRawHexDigest, hexDigestMissingTitle), //
						guiBuilder.displayer("displayer.text").title("jar.jarName.title").data(dataRawJarName).tooltip("jar.jarPath.tooltip"), //
						guiBuilder.displayer("displayer.text").title("project.name.title").data(dataProjectName).//
								guard(dataRawHexDigest, blankKey, dataJarProjectUrl, projectUrlMissingTitle, dataProjectName, projectNameMissingTitle).//
								actions(//
								editUrlButton(guiBuilder, ArtifactsAnchor.projectKey, dataJarProjectUrl),//
								guiBuilder.action("action.text.browse", GeneralAnchor.browseKey).tooltip("data.jar.project.url")//
								).tooltip("data.jar.project.url"),//
						guiBuilder.displayer("displayer.text").title("organisation.name.title").data(dataOrganisationName).//
								guard(dataRawHexDigest, blankKey, dataJarOrganisationUrl, organisationUrlMissingTitle, dataOrganisationName, organisationNameMissingTitle).//
								actions(//
								editUrlButton(guiBuilder, ArtifactsAnchor.organisationKey, dataJarOrganisationUrl),//
								guiBuilder.action("action.text.browse", GeneralAnchor.browseKey).tooltip(dataJarOrganisationUrl)//
								).tooltip("data.organisation.url"),//
						guiBuilder.displayer("displayer.text").title("jar.javadoc.title").data(dataRawJavadoc).tooltip(dataRawJavadoc), //
						guiBuilder.displayer("displayer.text").title("jar.source.title").data(dataRawSource).tooltip(dataRawSource)));
	}

}
