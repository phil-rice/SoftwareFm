package org.softwarefm.core.swt;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Layout;
import org.softwarefm.core.tests.SwtTest;

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

	public void testDisposeOfHasCompositeIsCalledWhenCompositeIsDisposed(){
		final AtomicInteger count = new AtomicInteger();
		HasComposite hasComposite = new HasComposite(shell){
			@Override
			public void dispose() {
				count.incrementAndGet();
			}
		};
		hasComposite.getComposite().dispose();
		assertEquals(1, count.get());
		
	}
}
