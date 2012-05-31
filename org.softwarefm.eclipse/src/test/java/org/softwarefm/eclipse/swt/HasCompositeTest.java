package org.softwarefm.eclipse.swt;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Layout;
import org.softwarefm.eclipse.SwtTest;

public class HasCompositeTest extends SwtTest {

	public void testGetCompositeAndGetControlReturnContent() {
		HasComposite hasComposite = new HasComposite(shell);
		assertSame(hasComposite.getComposite(), hasComposite.getControl());
		assertSame(shell, hasComposite.getComposite().getParent());
	}

	public void testSetLayoutChangesContentsLayout() {
		Layout layout = new FillLayout();
		HasComposite hasComposite = new HasComposite(shell);
		hasComposite.setLayout(layout);
		assertSame(layout, hasComposite.getComposite().getLayout());
	}

}
