package org.arc4eclipse.displayJavadocAndSource;

import org.arc4eclipse.core.plugin.AbstractDisplayerTest;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.eclipse.swt.widgets.Control;


public class DisplayJavadocTest extends AbstractDisplayerTest<JavadocPanel, Control> {

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
	protected IDisplayer<JavadocPanel, Control> getDisplayer() {
		return new JavadocDisplayer();
	}

	@Override
	protected void checkData(Object sampleData, JavadocPanel largeControl, Control smallControl) {
		fail();
	}

}
