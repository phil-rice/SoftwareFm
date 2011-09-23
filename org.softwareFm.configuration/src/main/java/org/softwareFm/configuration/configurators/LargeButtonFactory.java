package org.softwareFm.configuration.configurators;

import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.actions.ActionDefn;
import org.softwareFm.display.largeButton.ILargeButtonFactory;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;

public abstract class LargeButtonFactory implements ILargeButtonFactory {

	public static final String blankKey = "blank.title";

	public static final String dataRawHexDigest= "data.raw.jar.hexDigest";
	public static final String dataRawJarName= "data.raw.jar.jarName";
	public static final String dataRawJarPath = "data.raw.jar.jarPath";
	public static final String dataJarProjectUrl = "data.jar.project.url";
	public static final String dataJarOrganisationUrl = "data.jar.organisation.url";
	public static final String dataRawJavadoc = "data.raw.jar.javadoc";
	public static final String dataRawSource = "data.raw.jar.source";

	public static final String hexDigestMissingTitle = "hexDigest.missing.title";
	public static final String projectUrlMissingTitle = "project.url.missing.title";
	public static final String projectNameMissingTitle = "project.name.missing.title";
	public static final String organisationUrlMissingTitle = "organisation.url.missing.title";
	public static final String organisationNameMissingTitle = "organisation.name.missing.title";

	public static final String dataProjectName = "data.project.project.name";
	public static final String dataProjectMailingList = "data.project.mailingLists";
	public static final String dataProjectIssues = "data.project.issues";
	public static final String dataProjectTweets = "data.project.tweets";

	public static final String dataOrganisationName = "data.organisation.name";
	public static final String dataOrganisationTweets = "data.organisation.tweets";

	protected ActionDefn[] makeMainListActions(GuiBuilder guiBuilder, String artifactId, String dataId) {
		return new ActionDefn[] { guiBuilder.//
				action("action.list.add", artifactId, OverlaysAnchor.addKey).params(dataId).//
				tooltip("action.add.tooltip") };
	}

	protected ActionDefn listDeleteAction(GuiBuilder guiBuilder, String artifactId) {
		return guiBuilder.action("action.list.delete", artifactId, OverlaysAnchor.deleteKey).tooltip("action.delete.tooltip");
	}

	protected ActionDefn listEditAction(GuiBuilder guiBuilder, String artifactId) {
		return guiBuilder.action("action.list.edit", artifactId, OverlaysAnchor.editKey).tooltip("action.edit.tooltip");
	}

	protected ActionDefn editTextButton(GuiBuilder guiBuilder, String artifactId) {
		return guiBuilder.action("action.text.edit", artifactId, "overlay.edit").tooltip("action.edit.tooltip");
	}

	protected ActionDefn editUrlButton(GuiBuilder guiBuilder, String artifactId, String alternativeDataId) {
		return guiBuilder.action("action.text.edit", artifactId, "overlay.edit").tooltip("action.edit.url.tooltip").params(alternativeDataId);
	}
	protected ActionDefn browseButton(GuiBuilder guiBuilder) {
		return browseButton(guiBuilder, "action.browse.tooltip");
	}
	protected ActionDefn browseButton(GuiBuilder guiBuilder, String tooltipId) {
		return guiBuilder.action("action.text.browse", GeneralAnchor.browseKey).tooltip(tooltipId);
		
	}

}
