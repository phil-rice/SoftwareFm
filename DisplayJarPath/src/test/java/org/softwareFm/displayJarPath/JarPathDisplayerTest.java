package org.softwareFm.displayJarPath;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.core.plugin.AbstractDisplayerTest;
import org.softwareFm.displayCore.api.IDisplayer;

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
