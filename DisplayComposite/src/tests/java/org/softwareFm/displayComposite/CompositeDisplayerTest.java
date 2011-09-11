package org.softwareFm.displayComposite;

import org.softwareFm.core.plugin.AbstractDisplayerTest;
import org.softwareFm.displayCore.api.IDisplayContainer;
import org.softwareFm.displayCore.api.IDisplayer;
import org.softwareFm.displayCore.api.SummaryIcon;

public class CompositeDisplayerTest extends AbstractDisplayerTest<IDisplayContainer, SummaryIcon> {

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
	protected IDisplayer<IDisplayContainer, SummaryIcon> getDisplayer() {
		return new CompositeDisplayer();
	}

	@Override
	protected void checkData(Object sampleData, IDisplayContainer largeControl, SummaryIcon smallControl) {
		fail();
	}

	@Override
	protected String getSmallImageKey() {
		return "Clear";
	}

}
