package org.softwareFm.configuration.configurators;

import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.configuration.displayers.JavadocOrSourceButtonDisplayerFactory;
import org.softwareFm.display.displayer.ButtonDisplayerFactory;
import org.softwareFm.display.displayer.DisplayerStore;
import org.softwareFm.display.displayer.IDisplayerStoreConfigurator;
import org.softwareFm.display.displayer.ListDisplayerFactory;
import org.softwareFm.display.displayer.TextDisplayerFactory;

public class DisplayerStoreConfigurator implements IDisplayerStoreConfigurator {

	@Override
	public void process(DisplayerStore displayerStore) throws Exception {
		displayerStore.//
				displayer("displayer.text", new TextDisplayerFactory()).//
				displayer("displayer.url", new TextDisplayerFactory()).//
				displayer("displayer.button", new ButtonDisplayerFactory()).//
				displayer("displayer.button.javadoc", new JavadocOrSourceButtonDisplayerFactory("javadoc", ConfigurationConstants.dataRawJavadoc, ConfigurationConstants.dataArtifactJavadoc, "javadocMutator")).//
				displayer("displayer.button.source", new JavadocOrSourceButtonDisplayerFactory("source", ConfigurationConstants.dataRawSource, ConfigurationConstants.dataArtifactSource, "sourceMutator")).//
				displayer("displayer.list", new ListDisplayerFactory());
	}

}
