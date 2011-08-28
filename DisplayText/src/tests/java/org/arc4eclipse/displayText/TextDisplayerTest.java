package org.arc4eclipse.displayText;

import org.arc4eclipse.core.plugin.AbstractDisplayerTest;
import org.arc4eclipse.displayCore.api.BoundTitleAndTextField;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.eclipse.swt.widgets.Control;


public class TextDisplayerTest extends AbstractDisplayerTest<BoundTitleAndTextField, Control> {

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
		return new TextDisplayer();
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
