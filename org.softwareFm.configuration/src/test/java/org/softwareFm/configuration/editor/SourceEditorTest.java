package org.softwareFm.configuration.editor;

import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.display.data.DataGetterMock;
import org.softwareFm.jdtBinding.api.JdtConstants;

public class SourceEditorTest extends AbstractJavadocSourceEditorTest {


	@Override
	protected DataGetterMock makeDataGetter(String eclipseValue, String softwareFmValue) {
		return new DataGetterMock(ConfigurationConstants.dataRawSourceMutator, makeMutator(), ConfigurationConstants.dataRawSource, eclipseValue, ConfigurationConstants.dataJarSource, softwareFmValue);
	}

	@Override
	protected String getEclipseValueKey() {
		return ConfigurationConstants.dataRawSource;
	}

	@Override
	protected String getSoftwareFmValueKey() {
		return ConfigurationConstants.dataJarSource;
	}

	@Override
	protected String getMutatorKey() {
		return  ConfigurationConstants.dataRawSourceMutator;
	}
	@Override
	protected String getUrlTitleKey() {
		return ConfigurationConstants.urlSourceTitle;
	}

	@Override
	protected String getKeyForUpdateing() {
		return JdtConstants.sourceKey;
	}


}
