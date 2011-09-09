package org.softwareFm.displayComposite;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.core.plugin.AbstractDisplayerTest;
import org.softwareFm.displayCore.api.BoundTitleAndTextField;
import org.softwareFm.displayCore.api.IDisplayer;

public class CompositeDisplayerTest extends AbstractDisplayerTest<BoundTitleAndTextField, Control> {

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
	protected IDisplayer<BoundTitleAndTextField, Control> getDisplayer() {
		return new CompositeDisplayer();
	}

	@Override
	protected void checkData(Object sampleData, BoundTitleAndTextField largeControl, Control smallControl) {
		assertEquals(sampleData, largeControl.getText());
	}

	@Override
	protected String getSmallImageKey() {
		return "Clear";
	}

}
