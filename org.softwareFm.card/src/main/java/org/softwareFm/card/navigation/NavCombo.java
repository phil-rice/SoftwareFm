package org.softwareFm.card.navigation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;

public class NavCombo implements IHasControl {

	private Combo combo;
	private final String rootUrl;
	private Image image;
	private final CardConfig cardConfig;
	private String urlOffset;

	public NavCombo(Composite parent, final CardConfig cardConfig, final String rootUrl, String urlOffset, final ICallback<String> callbackToGotoUrl) {
		this.cardConfig = cardConfig;
		this.rootUrl = rootUrl;
		this.urlOffset = urlOffset;

		final ICardDataStore cardDataStore = cardConfig.cardDataStore;
		combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (combo.getSelectionIndex() == -1) {
					image = null;
					combo.redraw();
					return;
				}
				int selectionIndex = combo.getSelectionIndex();
				NavCombo.this.urlOffset = combo.getItem(selectionIndex);

				String newUrl = getSelectedUrl();
				ICallback.Utils.call(callbackToGotoUrl, newUrl);
				workOutImage();
			}

		});
		combo.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				Rectangle clientArea = combo.getClientArea();
				e.gc.setBackground(combo.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
				e.gc.fillRectangle(clientArea);
				if (image == null) {
					e.gc.drawText("/", 6, 0);
//					e.gc.drawRectangle(clientArea.x,clientArea.y,clientArea.width-1, clientArea.width-2);
				} else
					e.gc.drawImage(image, 0, 0);
			}
		});
		cardDataStore.processDataFor(rootUrl, new ICardDataStoreCallback<Void>() {
			@Override
			public Void process(String url, Map<String, Object> result) throws Exception {
				List<String> items = Lists.newList();
				for (Entry<String, Object> entry : result.entrySet())
					if (entry.getValue() instanceof Map<?, ?>)
						items.add(entry.getKey());
				Collections.sort(items);
				setDropdownItems(items);
				return null;
			}

			@Override
			public Void noData(String url) throws Exception {
				return process(url, Collections.<String, Object> emptyMap());
			}
		});
		workOutImage();

	}

	private void workOutImage() {
		if (urlOffset == null || urlOffset.length() == 0)
			return;
		String url = rootUrl + "/" + urlOffset;
		cardConfig.cardDataStore.processDataFor(url, new ICardDataStoreCallback<Void>() {
			@Override
			public Void process(String url, Map<String, Object> result) throws Exception {
				image = cardConfig.cardIconFn.apply(result);
				if (!combo.isDisposed())
					combo.redraw();
				return null;
			}

			@Override
			public Void noData(String url) throws Exception {
				return process(url, Collections.<String, Object> emptyMap());
			}
		});
	}

	private String getSelectedUrl() {
		int selectionIndex = combo.getSelectionIndex();
		String postFix = combo.getItem(selectionIndex);
		String newUrl = rootUrl + "/" + postFix;
		return newUrl;
	}

	public void setDropdownItems(List<String> items) {
		combo.removeAll();
		for (String item : items)
			combo.add(item);
	}

	@Override
	public Control getControl() {
		return combo;
	}
}
