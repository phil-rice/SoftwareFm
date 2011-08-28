package org.arc4eclipse.displayJarPath;

import org.arc4eclipse.core.plugin.AbstractDisplayerTest;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.eclipse.swt.widgets.Control;

public class JarPathDisplayerTest extends AbstractDisplayerTest<DisplayJarPanel, Control> {

	@Override
	protected String getDisplayerKey() {
		return "jar";
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
	protected IDisplayer<DisplayJarPanel, Control> getDisplayer() {
		return new JarPathDisplayer();
	}

	@Override
	protected void checkData(Object sampleData, DisplayJarPanel largeControl, Control smallControl) {
		fail();

	}

	@Override
	protected String getSmallImageKey() {
		return "Jar";
	}

}
