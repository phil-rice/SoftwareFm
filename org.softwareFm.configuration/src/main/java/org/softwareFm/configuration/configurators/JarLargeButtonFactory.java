package org.softwareFm.configuration.configurators;

import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.largeButton.ILargeButtonFactory;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;

public class JarLargeButtonFactory implements ILargeButtonFactory {

	@Override
	public LargeButtonDefn apply(GuiBuilder guiBuilder) throws Exception {
		return guiBuilder.largeButton("largeButton.jar",//
				guiBuilder.smallButton("smallButton.jar.details", "smallButton.jar.details.title", "smallButton.jar", ArtifactsAnchor.jarKey, //
						guiBuilder.displayer("displayer.text").title("jar.jarName.title").data("data.raw.jarPath").tooltip("jar.jarPath.tooltip"), //
						guiBuilder.displayer("displayer.text").title("project.name.title").data("data.project.project.name").actions(//
								guiBuilder.action("action.text.edit", ArtifactsAnchor.projectKey, "overlay.edit").tooltip("action.edit.tooltip").params("data.jar.project.url"),//
								guiBuilder.action("action.text.browse", GeneralAnchor.browseKey).tooltip("data.jar.project.url").params("data.jar.project.url")//
								).tooltip("data.jar.project.url"),//
						guiBuilder.displayer("displayer.text").title("organisation.name.title").data("data.organisation.name").actions(//
								guiBuilder.action("action.text.browse", GeneralAnchor.browseKey).tooltip("data.jar.organisation.url").params("data.jar.organisation.url")//
								).tooltip("data.organisation.url"),//
						guiBuilder.displayer("displayer.text").title("jar.javadoc.title").data("data.raw.javadoc").tooltip("data.raw.javadoc"), //
						guiBuilder.displayer("displayer.text").title("jar.source.title").data("data.raw.source").tooltip("data.raw.source")));
	}

}
