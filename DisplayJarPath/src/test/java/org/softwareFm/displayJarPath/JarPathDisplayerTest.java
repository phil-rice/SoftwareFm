package org.softwareFm.displayJarPath;

import org.softwareFm.core.plugin.AbstractDisplayerTest;
import org.softwareFm.displayCore.api.IDisplayer;
import org.softwareFm.displayJavadocAndSource.JarSummaryImageButton;

public class JarPathDisplayerTest extends AbstractDisplayerTest<DisplayJarSummaryPanel, JarSummaryImageButton> {

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
	protected IDisplayer<DisplayJarSummaryPanel, JarSummaryImageButton> getDisplayer() {
		return new JarPathDisplayer();
	}

	@Override
	protected void checkData(Object sampleData, DisplayJarSummaryPanel largeControl, JarSummaryImageButton smallControl) {
		fail();

	}

	@Override
	protected String getSmallImageKey() {
		return "Jar";
	}

}
