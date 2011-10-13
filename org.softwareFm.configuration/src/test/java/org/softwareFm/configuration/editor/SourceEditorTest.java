package org.softwareFm.configuration.editor;

import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.display.data.DataGetterMock;

public class SourceEditorTest extends AbstractJavadocSourceEditorTest {


	@Override
	protected DataGetterMock makeDataGetter(String eclipseValue, String softwareFmValue) {
		return new DataGetterMock(ConfigurationConstants.dataRawSourceMutator, makeMutator(), ConfigurationConstants.dataRawSource, eclipseValue, ConfigurationConstants.dataArtifactSource, softwareFmValue);
	}

	@Override
	protected String getEclipseValueKey() {
		return ConfigurationConstants.dataRawSource;
	}

	@Override
	protected String getSoftwareFmValueKey() {
		return ConfigurationConstants.dataArtifactSource;
	}

	@Override
	protected String getMutatorKey() {
		return  ConfigurationConstants.dataRawSourceMutator;
	}
	@Override
	protected String getUrlTitleKey() {
		return ConfigurationConstants.urlSourceTitle;
	}


}
