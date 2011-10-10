package org.softwareFm.configuration.configurators;

import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.configuration.displayers.JavadocSourceSummaryDisplayerFactory;
import org.softwareFm.display.displayer.AggregateTextDisplayerFactory;
import org.softwareFm.display.displayer.CompressedStyledTextDisplayerFactory;
import org.softwareFm.display.displayer.CompressedTextDisplayerFactory;
import org.softwareFm.display.displayer.DisplayerStore;
import org.softwareFm.display.displayer.IDisplayerStoreConfigurator;
import org.softwareFm.display.displayer.ListDisplayerFactory;

public class DisplayerStoreConfigurator implements IDisplayerStoreConfigurator {

	@Override
	public void process(DisplayerStore displayerStore) throws Exception {
		displayerStore.//

				displayer("displayer.compressed.text", new CompressedTextDisplayerFactory()).//
				displayer("displayer.compressed.styled.text", new CompressedStyledTextDisplayerFactory()).//
				displayer("displayer.sfm.id", new AggregateTextDisplayerFactory(ConfigurationConstants.sfmIdPattern, ConfigurationConstants.dataJarGroupId, ConfigurationConstants.dataJarArtifactId, ConfigurationConstants.dataJarVersion)).//
				displayer("displayer.summary.javadoc", new JavadocSourceSummaryDisplayerFactory(ConfigurationConstants.dataRawJavadoc, ConfigurationConstants.dataArtifactJavadoc)).//
				displayer("displayer.summary.source", new JavadocSourceSummaryDisplayerFactory(ConfigurationConstants.dataRawSource, ConfigurationConstants.dataArtifactSource)).//
				displayer("displayer.list", new ListDisplayerFactory());
	}

}
