package org.softwareFm.displayMainUrl;

import org.softwareFm.core.plugin.AbstractDisplayerTest;
import org.softwareFm.displayCore.api.IDisplayer;
import org.softwareFm.displayCore.api.SummaryIcon;

public class MainUrlDisplayerTest extends AbstractDisplayerTest<MainUrlPanel, SummaryIcon> {

	@Override
	protected String getDataKey() {
		return "Key1";
	}

	@Override
	protected String getDisplayerKey() {
		return "mainUrl";
	}

	@Override
	protected Object getSampleData(String key) {
		return "data for " + key;
	}

	@Override
	protected IDisplayer<MainUrlPanel, SummaryIcon> getDisplayer() {
		return new MainUrlDisplayer();
	}

	@Override
	protected void checkData(Object sampleData, MainUrlPanel largeControl, SummaryIcon smallControl) {
		assertEquals(sampleData, largeControl.getText());
	}

	@Override
	protected String getSmallImageKey() {
		return "Clear";
	}

}
