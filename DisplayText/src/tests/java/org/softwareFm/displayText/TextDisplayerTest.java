package org.softwareFm.displayText;

import org.softwareFm.core.plugin.AbstractDisplayerTest;
import org.softwareFm.displayCore.api.BoundTitleAndTextField;
import org.softwareFm.displayCore.api.IDisplayer;
import org.softwareFm.displayCore.api.SummaryIcon;

public class TextDisplayerTest extends AbstractDisplayerTest<BoundTitleAndTextField, SummaryIcon> {

	@Override
	protected String getDataKey() {
		return "Key1";
	}

	@Override
	protected String getDisplayerKey() {
		return "text";
	}

	@Override
	protected Object getSampleData(String key) {
		return "data for " + key;
	}

	@Override
	protected IDisplayer<BoundTitleAndTextField, SummaryIcon> getDisplayer() {
		return new TextDisplayer();
	}

	@Override
	protected void checkData(Object sampleData, BoundTitleAndTextField largeControl, SummaryIcon smallControl) {
		assertEquals(sampleData, largeControl.getText());
	}

	@Override
	protected String getSmallImageKey() {
		return "Clear";
	}

}
