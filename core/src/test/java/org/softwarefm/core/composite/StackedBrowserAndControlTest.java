package org.softwarefm.core.composite;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.composite.StackedBrowserAndControlTest.HasCompositeWithDisposeCount;
import org.softwarefm.core.constants.UrlConstants;
import org.softwarefm.core.swt.HasComposite;
import org.softwarefm.utilities.functions.IFunction1;

public class StackedBrowserAndControlTest extends AbstractSoftwareFmCompositeTest<StackedBrowserAndControl<HasCompositeWithDisposeCount>> {

	static class HasCompositeWithDisposeCount extends HasComposite {

		int count;

		public HasCompositeWithDisposeCount(Composite parent) {
			super(parent);
		}

		@Override
		public void dispose() {
			super.dispose();
			count++;
		}

	}

	protected HasCompositeWithDisposeCount child;

	public void testDisposePassesMessageToChild() {
		assertEquals(0, child.count);
		panel.dispose();
		assertEquals(1, child.count);
	}

	public void testShowSecondaryControlPlacesItOnTopAndLaysout() {
		int startCount = getLayoutCount();
		panel.showSecondaryControl();
		assertEquals(panel.getSecondaryControl().getControl(), panel.getTopControl());
		assertEquals(startCount + 1, getLayoutCount());
	}

	public void testSetTextEnsuresBrowserInShown() {
		panel.showSecondaryControl();
		panel.setText("some text");
		// assertEquals("some text", panel.getBrowser().getText()); //would like  to assert that the text goes into the browser...but was unable to work out how to do so
		assertEquals(panel.getBrowser().getControl(), panel.getTopControl());
	}

	public void testSetUrlAndShowEnsuresBrowserInShown() {
		panel.showSecondaryControl();
		panel.setUrlAndShow(UrlConstants.notJarUrl);
		// assertEquals(UrlConstants.notJarUrl, panel.getBrowser().getUrl());
		assertEquals(panel.getBrowser().getControl(), panel.getTopControl());
	}

	private int getLayoutCount() {
		return panel.layoutCount;
	}

	@Override
	protected StackedBrowserAndControl<HasCompositeWithDisposeCount> makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new StackedBrowserAndControl<HasCompositeWithDisposeCount>(parent, container, new IFunction1<Composite, HasCompositeWithDisposeCount>() {
			public HasCompositeWithDisposeCount apply(Composite from) throws Exception {
				return child = new HasCompositeWithDisposeCount(from);
			}
		});
	}

}
