package org.softwareFm.configuration.configurators;

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
				displayer("displayer.button.javadoc", new JavadocOrSourceButtonDisplayerFactory("javadoc", "data.raw.jar.javadoc", "data.artifact.javadoc", "javadocMutator")).//
				displayer("displayer.button.source", new JavadocOrSourceButtonDisplayerFactory("source", "data.raw.jar.source", "data.artifact.source", "sourceMutator")).//
				displayer("displayer.list", new ListDisplayerFactory());
	}

}
