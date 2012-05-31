package org.softwarefm.views;

import org.easymock.EasyMock;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;

public class ClassAndMethodPanelTest extends AbstractSoftwareFmPanelTest<ClassAndMethodView, ClassAndMethodPanel> {
	private final ExpressionData expressionData = new ExpressionData("packageName", "className");

	public void testClassAndMethodSelectionCausesBrowserToViewUrl() throws Exception {
		EasyMock.expect(strategy.findNode("selection", 1)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", 1)).andReturn(expressionData);
		EasyMock.expect(strategy.findFileAndDigest("selection", "node", 1)).andReturn(fileNameAndNoDigest);
		EasyMock.replay(strategy);

		execute(new Runnable() {
			@Override
			public void run() {
				selectedArtifactSelectionManager.selectionOccured("selection");
			}
		});
		EasyMock.verify(strategy);
		assertEquals("localhost/wiki/java/packageName/className", panel.getUrl());
	}

	@Override
	protected ClassAndMethodView makeView() {
		return new ClassAndMethodView();
	}
}
