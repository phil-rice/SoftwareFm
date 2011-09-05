package org.softwareFm.displayJavadocAndSource;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.core.plugin.AbstractDisplayerTest;
import org.softwareFm.displayCore.api.IDisplayer;

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

	@Override
	protected String getSmallImageKey() {
		return "Javadoc";
	}

}
