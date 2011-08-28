package org.arc4eclipse.displayUrl;

import org.arc4eclipse.core.plugin.AbstractDisplayerTest;
import org.arc4eclipse.displayCore.api.BoundTitleAndTextField;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.eclipse.swt.widgets.Control;


public class UrlDisplayerTest extends AbstractDisplayerTest<BoundTitleAndTextField, Control> {

	public void testForName() throws ClassNotFoundException {
		Class.forName("org.arc4eclipse.displayUrl.UrlDisplayer");
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
	protected IDisplayer<BoundTitleAndTextField, Control> getDisplayer() {
		return new UrlDisplayer();
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
