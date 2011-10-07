package org.softwareFm.configuration.configurators;

import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.actions.ActionDefn;
import org.softwareFm.display.largeButton.ILargeButtonFactory;
import org.softwareFm.display.smallButtons.SmallButtonDefn;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;

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

	protected SmallButtonDefn makeSfmSmallButton(GuiBuilder guiBuilder, String id, String title) {
		return guiBuilder.smallButton(id, title, "smallButton.data", GeneralAnchor.sfmKey, //
				guiBuilder.displayer("displayer.sfm.id").title(groupIdTitle).data(dataJarGroupId).//
						guard(dataRawHexDigest, hexDigestMissingTitle, //
								dataJarGroupId, groupIdMissingTitle, //
								dataJarArtifactId, artifactIdMissingTitle, //
								dataJarVersion, versionMissingTitle).//
						actions(guiBuilder.action("action.softwareFm.id.edit", GeneralAnchor.sfmKey, "overlay.edit").tooltip("action.edit.tooltip")), //
				guiBuilder.displayer("displayer.compressed.text").title(artifactNameTitle).data(dataArtifactName).tooltip(artifactNameTitle).//
						guard(dataRawHexDigest, blankKey, dataJarArtifactId, artifactIdMissingTitle, dataArtifactName, artifactNameMissingTitle).//
						actions(editTextButton(guiBuilder, ArtifactsAnchor.projectKey)//
						)).data(ConfigurationConstants.dataJarGroupId);
	}

}
