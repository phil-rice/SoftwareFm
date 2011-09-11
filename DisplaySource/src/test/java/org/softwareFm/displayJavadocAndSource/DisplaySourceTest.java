package org.softwareFm.displayJavadocAndSource;

import org.softwareFm.core.plugin.AbstractDisplayerTest;
import org.softwareFm.displayCore.api.IDisplayer;
import org.softwareFm.displayCore.api.SummaryIcon;

public class DisplaySourceTest extends AbstractDisplayerTest<SourcePanel, SummaryIcon> {

	@Override
	protected String getDisplayerKey() {
		return "src";
	}

	@Override
	protected String getDataKey() {
		return "Key1";
	}

	@Override
	protected Object getSampleData(String key) {
		return null;
	}

	@Override
	protected IDisplayer<SourcePanel, SummaryIcon> getDisplayer() {
		return new SourceDisplayer();
	}

	@Override
	protected void checkData(Object sampleData, SourcePanel largeControl, SummaryIcon smallControl) {
		fail();
	}

	@Override
	protected String getSmallImageKey() {
		return "Source";
	}

}
