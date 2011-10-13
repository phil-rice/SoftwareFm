package org.softwareFm.configuration.editor;

import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.display.data.DataGetterMock;

public class JavadocEditorTest extends AbstractJavadocSourceEditorTest {


	@Override
	protected DataGetterMock makeDataGetter(String eclipseValue, String softwareFmValue) {
		return new DataGetterMock(ConfigurationConstants.dataRawJavadocMutator, makeMutator(), ConfigurationConstants.dataRawJavadoc, eclipseValue, ConfigurationConstants.dataArtifactJavadoc, softwareFmValue);
	}

	@Override
	protected String getEclipseValueKey() {
		return ConfigurationConstants.dataRawJavadoc;
	}

	@Override
	protected String getSoftwareFmValueKey() {
		return ConfigurationConstants.dataArtifactJavadoc;
	}

	@Override
	protected String getMutatorKey() {
		return ConfigurationConstants.dataRawJavadocMutator;
	}

	@Override
	protected String getUrlTitleKey() {
		return ConfigurationConstants.urlJavadocTitle;
	}

}
