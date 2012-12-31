package org.softwarefm.core.browser;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.composite.SoftwareFmComposite;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.utilities.functions.IFunction1;

public class BrowserComposite extends SoftwareFmComposite {

	private final Browser browser;
	private final BrowserToolbar browserToolbar;
	private final Composite rowComposite;
	private final BrowserUrlCombo browserUrlCombo;

	public static class BrowserCompositeLayout extends Layout {

		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			Control[] children = composite.getChildren();
			assert children.length == 2 : children;
			return Swts.computeSizeForVerticallyStackedControl(SWT.DEFAULT, SWT.DEFAULT, children);
		}

		@Override
		protected void layout(Composite composite, boolean flushCache) {
			Control[] children = composite.getChildren();
			assert children.length == 2 : children;
			Control topRow = children[0];
			Control main = children[1];
			Rectangle ca = composite.getClientArea();
			Point topRowSize = topRow.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			int y = topRowSize.y;
			int middleY = 1;
			topRow.setBounds(ca.x, ca.y, ca.width, y);

			main.setBounds(ca.x, ca.y + y + middleY, ca.width, ca.height - y - middleY);
		}

	}

	public BrowserComposite(final Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
		ImageRegistry imageRegistry = container.imageRegistry;
		IBrowserGetter browserGetter = getBrowserGetter();

		rowComposite = Swts.newComposite(getComposite(), SWT.NULL, "TopRow");
		browser = new Browser(getComposite(), SWT.NULL);

		browserToolbar = new BrowserToolbar(rowComposite, browser, imageRegistry);
		browserUrlCombo = new BrowserUrlCombo(rowComposite, browser, browserToolbar);

		Swts.Grid.oneRowWithControlGrabbingWidth(rowComposite, browserUrlCombo.getControl());


		browserToolbar.updateButtonStatus();
	
		browser.setUrl("www.google.com");
		setLayout(new BrowserCompositeLayout());
		getComposite().addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Point rowSize = rowComposite.getSize();
				e.gc.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));
				e.gc.drawLine(0, rowSize.y, rowSize.x, rowSize.y);
			}
		});
	}

	private IBrowserGetter getBrowserGetter() {
		return new IBrowserGetter() {
			@Override
			public void updateButtonStatus() {
				updateButtonStatus();
			}
			
			@Override
			public Browser getBrowser() {
				if (browser == null)
					throw new IllegalStateException("Cannot access browser yet: not instantiated");
				return browser;
			}
		};
	}

	public void setUrl(String url) {
		browser.setUrl(url);
		browserToolbar.updateButtonStatus();
	}

	public static void main(String[] args) {
		Swts.Show.display(BrowserComposite.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			public Composite apply(Composite from) throws Exception {
				SoftwareFmContainer<Object> container = SoftwareFmContainer.makeForTests(from.getDisplay());
				return new BrowserComposite(from, container).getComposite();
			}
		});
	}
}
