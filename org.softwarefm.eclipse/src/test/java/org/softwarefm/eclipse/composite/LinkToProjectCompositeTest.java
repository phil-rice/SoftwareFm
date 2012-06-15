package org.softwarefm.eclipse.composite;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;

public class LinkToProjectCompositeTest extends AbstractSoftwareFmCompositeTest<LinkToProjectComposite> {

	public void testMessages() {
		assertEquals("", panel.getText());//initial
		listenerManager.classAndMethodSelectionOccured(classExpressionData, 0);
		assertEquals("", panel.getText());
		listenerManager.classAndMethodSelectionOccured(classAndMethodExpressionData, 0);
		assertEquals("", panel.getText());
	}

	@Override
	protected LinkToProjectComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new LinkToProjectComposite(parent, container);
	}

}
