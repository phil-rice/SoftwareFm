package org.softwarefm.eclipse.browser;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.composite.SoftwareFmComposite;
import org.softwarefm.eclipse.constants.ImageConstants;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.utilities.functions.IFunction1;

public class BrowserComposite extends SoftwareFmComposite {

	private final Browser browser;
	private final ToolBar toolbar;
	private final Composite rowComposite;
	private final Combo urlCombo;
	private final ToolItem backButton;
	private final ToolItem forwardButton;
	private final ToolItem stopButton;
	private final ToolItem refreshButton;

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
		rowComposite = Swts.newComposite(getComposite(), SWT.NULL, "TopRow");
		toolbar = new ToolBar(rowComposite, SWT.NULL);
		urlCombo = new Combo(rowComposite, SWT.DROP_DOWN);

		ImageRegistry imageRegistry = container.imageRegistry;
		backButton = new ToolItem(toolbar, SWT.PUSH);
		backButton.setImage(imageRegistry.get(ImageConstants.backButton));

		forwardButton = new ToolItem(toolbar, SWT.PUSH);
		forwardButton.setImage(imageRegistry.get(ImageConstants.forwardButton));

		stopButton = new ToolItem(toolbar, SWT.PUSH);
		stopButton.setImage(imageRegistry.get(ImageConstants.stopButton));

		refreshButton = new ToolItem(toolbar, SWT.PUSH);
		refreshButton.setImage(imageRegistry.get(ImageConstants.refreshButton));

		Swts.Grid.oneRowWithControlGrabbingWidth(rowComposite, urlCombo);

		browser = new Browser(getComposite(), SWT.NULL);

		updateButtonStatus();
		browser.addLocationListener(new LocationListener() {
			public void changing(LocationEvent event) {
			}

			public void changed(LocationEvent event) {
				if (event.top) {
					Swts.addItemToStartOfCombo(urlCombo, event.location, 10);
				}
				updateButtonStatus();
			}
		});
		backButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				browser.back();
			}
		});
		forwardButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				browser.forward();
			}
		});
		stopButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				browser.stop();
			}
		});
		refreshButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				browser.refresh();
			}
		});
		urlCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				browser.setUrl(urlCombo.getText());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);

			}
		});
		urlCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateButtonStatus();
			}
		});
		browser.setUrl("www.google.com");
		setLayout(new BrowserCompositeLayout());
		getComposite().addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Point rowSize = rowComposite.getSize();
				e.gc.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));
				e.gc.drawLine(0, rowSize.y , rowSize.x, rowSize.y );
			}
		});
	}

	private void updateButtonStatus() {
		backButton.setEnabled(browser.isBackEnabled());
		forwardButton.setEnabled(browser.isForwardEnabled());
	}

	public void setUrl(String url) {
		browser.setUrl(url);
		updateButtonStatus();
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
