package org.softwarefm.eclipse.composite;

import org.easymock.EasyMock;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.jdtBinding.CodeData;

public class CodeCompositeTest extends AbstractSoftwareFmCompositeTest<CodeComposite> {
	private final CodeData codeData = new CodeData("packageName", "className");

	public void testClassAndMethodSelectionCausesBrowserToViewUrl() throws Exception {
		EasyMock.expect(strategy.findNode("selection", 1)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", 1)).andReturn(codeData);
		EasyMock.expect(strategy.findFile("selection", "node", 1)).andReturn(file);
		EasyMock.expect(strategy.findDigest("selection", "node", file, 1)).andReturn(fileNameAndNoDigest);
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
	protected CodeComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new CodeComposite(parent, container);
	}
}
