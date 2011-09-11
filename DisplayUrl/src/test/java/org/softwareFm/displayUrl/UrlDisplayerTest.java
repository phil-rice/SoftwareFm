package org.softwareFm.displayUrl;

import org.softwareFm.core.plugin.AbstractDisplayerTest;
import org.softwareFm.displayCore.api.BoundTitleAndTextField;
import org.softwareFm.displayCore.api.IDisplayer;
import org.softwareFm.displayCore.api.SummaryIcon;

public class UrlDisplayerTest extends AbstractDisplayerTest<BoundTitleAndTextField, SummaryIcon> {

	public void testForName() throws ClassNotFoundException {
		Class.forName("org.softwareFm.displayUrl.UrlDisplayer");
	}

	@Override
	protected String getDisplayerKey() {
		return "url";
	}

	@Override
	protected String getDataKey() {
		return "Key1";
	}

	@Override
	protected Object getSampleData(String key) {
		return "http://www.google.com";
	}

	@Override
	protected IDisplayer<BoundTitleAndTextField, SummaryIcon> getDisplayer() {
		return new UrlDisplayer();
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
