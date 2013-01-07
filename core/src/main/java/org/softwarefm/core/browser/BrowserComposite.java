package org.softwarefm.core.browser;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.swt.HasComposite;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.IListenerList;
import org.softwarefm.utilities.functions.IFunction1;

public class BrowserComposite extends HasComposite {

	protected final Browser browser;
	protected final BrowserToolbar browserToolbar;
	protected final Composite rowComposite;
	protected final BrowserUrlCombo browserUrlCombo;
	private final IListenerList<IEditingListener> editingListeners;
	private boolean editing;

	public BrowserComposite(final Composite parent, SoftwareFmContainer<?> container) {
		super(parent);
		editingListeners = IListenerList.Utils.list(container.listenerList, IEditingListener.class, this);
		ImageRegistry imageRegistry = container.imageRegistry;
		rowComposite = Swts.newComposite(getComposite(), SWT.NULL, "TopRow");
		browser = new Browser(getComposite(), SWT.NULL);

		browserToolbar = new BrowserToolbar(rowComposite, browser, imageRegistry);
		browserUrlCombo = new BrowserUrlCombo(rowComposite, browser, browserToolbar);

		addMoreControls();

		Swts.Grid.oneRowWithControlGrabbingWidth(rowComposite, browserUrlCombo.getControl());
		Swts.Grid.addGrabHorizontalAndFillGridDataToAllChildren(getComposite());
		Swts.Grid.addGrabVerticalToGridData(browser, true);
		Swts.addPaintListenerToDrawLineUnderChild(getComposite(), rowComposite, SWT.COLOR_GRAY);

		browser.addLocationListener(new LocationAdapter() {
			@Override
			public void changed(LocationEvent event) {
				final boolean newEditing = event.location.contains("action=edit");
				if (editing != newEditing)
					editingListeners.fire(new ICallback<IEditingListener>() {
						@Override
						public void process(IEditingListener t) throws Exception {
							t.editingState(newEditing);
						}
					});
				editing = newEditing;

			}
		});
	}

	public void addEditingListener(IEditingListener listener) {
		editingListeners.addListener(listener);
	}

	public void removeEditingListener(IEditingListener listener) {
		editingListeners.removeListener(listener);
	}

	protected void addMoreControls() {
	}

	public void setUrlAndWait(final String url) {
		executeAndWaitToLoad(new Runnable() {
			public void run() {
				setUrl(url);
			}
		});
	}

	public void setUrl(String url) {
		browser.setUrl(url);
		browserToolbar.updateButtonStatus();
	}

	void executeAndWaitToLoad(Runnable runnable) {
		final CountDownLatch latch = new CountDownLatch(1);
		LocationListener listener = new LocationListener() {

			@Override
			public void changing(LocationEvent event) {
			}

			@Override
			public void changed(LocationEvent event) {
				latch.countDown();
			}
		};
		try {
			browser.addLocationListener(listener);
			runnable.run();
			Swts.dispatchUntilLatch(browser.getDisplay(), latch);
		} finally {
			browser.removeLocationListener(listener);
		}
	}

	public Object evaluateScript(String javaScript) {
		return browser.evaluate(javaScript);
	}

	public Object evaluateScriptAndWaitForLoad(final String javaScript) {
		final AtomicReference<Object> result = new AtomicReference<Object>();
		executeAndWaitToLoad(new Runnable() {
			public void run() {
				Object evaluateScript = evaluateScript(javaScript);
				result.set(evaluateScript);
			}
		});
		return result.get();
	}

	public String getHtml() {
		return browser.getText();
	}

	public static void main(String[] args) {
		Swts.Show.display(BrowserComposite.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			public Composite apply(Composite from) throws Exception {
				SoftwareFmContainer<Object> container = SoftwareFmContainer.makeForTests(from.getDisplay());
				BrowserComposite browserComposite = new BrowserComposite(from, container);
				browserComposite.setUrl("www.google.com");
				return browserComposite.getComposite();
			}
		});
	}

}
