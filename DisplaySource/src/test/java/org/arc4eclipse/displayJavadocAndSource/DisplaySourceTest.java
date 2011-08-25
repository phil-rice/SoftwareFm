package org.arc4eclipse.displayJavadocAndSource;

import org.arc4eclipse.core.plugin.AbstractDisplayerTest;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayJavadocAndSource.SourceDisplayer;
import org.arc4eclipse.displayJavadocAndSource.SourcePanel;
import org.eclipse.swt.widgets.Control;


public class DisplaySourceTest extends AbstractDisplayerTest<SourcePanel, Control> {

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
	protected IDisplayer<SourcePanel, Control> getDisplayer() {
		return new SourceDisplayer();
	}

	@Override
	protected void checkData(Object sampleData, SourcePanel largeControl, Control smallControl) {
		fail();
	}

}
