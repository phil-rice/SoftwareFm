package org.softwareFm.displayJavadocAndSource;

import org.softwareFm.core.plugin.AbstractDisplayerTest;
import org.softwareFm.displayCore.api.IDisplayer;
import org.softwareFm.displayCore.api.SummaryIcon;

public class DisplayJavadocTest extends AbstractDisplayerTest<JavadocPanel, SummaryIcon> {

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
	protected IDisplayer<JavadocPanel, SummaryIcon> getDisplayer() {
		return new JavadocDisplayer();
	}

	@Override
	protected void checkData(Object sampleData, JavadocPanel largeControl, SummaryIcon smallControl) {
		fail();
	}

	@Override
	protected String getSmallImageKey() {
		return "Javadoc";
	}

}
