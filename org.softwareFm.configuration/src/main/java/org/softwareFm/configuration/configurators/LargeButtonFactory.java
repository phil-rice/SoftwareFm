package org.softwareFm.configuration.configurators;

import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.actions.ActionDefn;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.largeButton.ILargeButtonFactory;
import org.softwareFm.display.smallButtons.SmallButtonDefn;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;
import org.softwareFm.utilities.arrays.ArrayHelper;

public abstract class LargeButtonFactory extends ConfigurationConstants implements ILargeButtonFactory {

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

	protected ActionDefn editStyledTextButton(GuiBuilder guiBuilder, String artifactId) {
		return guiBuilder.action("action.styled.text.edit", artifactId, "overlay.edit").tooltip("action.edit.tooltip");
	}

	protected ActionDefn editValueButton(GuiBuilder guiBuilder, String artifactId) {
		return guiBuilder.action("action.text.edit", artifactId, "overlay.edit").tooltip("action.edit.name.tooltip");
	}

	protected ActionDefn editUrlButton(GuiBuilder guiBuilder, String dataId) {
		return guiBuilder.action("action.text.edit", GeneralAnchor.browseKey, "overlay.edit").tooltip("action.edit.url.tooltip").params(dataId);
	}

	protected ActionDefn editUrlButton(GuiBuilder guiBuilder) {
		return guiBuilder.action("action.text.edit", GeneralAnchor.browseKey, "overlay.edit").tooltip("action.edit.url.tooltip");
	}

	protected ActionDefn browseButton(GuiBuilder guiBuilder) {
		return guiBuilder.action("action.text.browse", GeneralAnchor.browseKey).tooltip("action.browse.tooltip");
	}

	protected ActionDefn browseButton(GuiBuilder guiBuilder, String dataId) {
		return guiBuilder.action("action.text.browse", GeneralAnchor.browseKey).params(dataId).tooltip(dataId);

	}

	protected SmallButtonDefn makeSfmSmallButton(GuiBuilder guiBuilder, String smallButtonId, String smallButtonIcon, String id, String title, boolean includeJar, DisplayerDefn... moreDefns) {

		DisplayerDefn sfmId = guiBuilder.displayer("displayer.sfm.id").icon(GeneralAnchor.sfmKey).editor(editorSfmIdKey).//
				title(softwareFmIdTitle).data(dataJarGroupId).help("softwareFm.id.help").//
				guard(dataRawHexDigest, hexDigestMissingTitle, //
						dataJarGroupId, softwareFmIdMissingTitle, //
						dataJarArtifactId, softwareFmIdMissingTitle, //
						dataJarVersion, softwareFmIdMissingTitle);

		DisplayerDefn sfmName = guiBuilder.displayer("displayer.compressed.text").icon(ArtifactsAnchor.projectKey).//
				editor(editorTextKey).title(artifactNameTitle).data(dataArtifactName).help("softwareFm.name.help").
				guard(dataRawHexDigest, blankKey, dataJarArtifactId, softwareFmIdMissingTitle, dataArtifactName, artifactNameMissingTitle);
		
		DisplayerDefn jarName = guiBuilder.displayer("displayer.compressed.text").icon(ArtifactsAnchor.jarKey).editor(editorJarKey).help("jar.help").title(jarNameTitle).data(dataRawJarName).tooltip(dataRawJarPath).actions(//
				guiBuilder.action("action.url.search", GeneralAnchor.searchKey).tooltip("action.search.tooltip"));
		
		DisplayerDefn[] baseDefns = includeJar ? new DisplayerDefn[] { sfmId, jarName, sfmName } : new DisplayerDefn[] { sfmId, sfmName };
		DisplayerDefn[] defns = ArrayHelper.append(baseDefns, moreDefns);
		return guiBuilder.smallButton(id, title, smallButtonId, smallButtonIcon, defns).data(ConfigurationConstants.dataJarGroupId);
	}
}
