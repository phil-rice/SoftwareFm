package org.softwarefm.eclipse.composite;

import org.easymock.EasyMock;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;

public class ClassAndMethodCompositeTest extends AbstractSoftwareFmCompositeTest<ClassAndMethodComposite> {
	private final ExpressionData expressionData = new ExpressionData("packageName", "className");

	public void testClassAndMethodSelectionCausesBrowserToViewUrl() throws Exception {
		EasyMock.expect(strategy.findNode("selection", 1)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", 1)).andReturn(expressionData);
		EasyMock.expect(strategy.findFileAndDigest("selection", "node", 1)).andReturn(fileNameAndNoDigest);
		EasyMock.replay(strategy);

		execute(new Runnable() {
			public void run() {
				selectedArtifactSelectionManager.selectionOccured("selection");
			}
		});
		EasyMock.verify(strategy);
		assertEquals(classAndMethodNameUrl, panel.getUrl());
	}

	@Override
	protected ClassAndMethodComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new ClassAndMethodComposite(parent, container);
	}
}
