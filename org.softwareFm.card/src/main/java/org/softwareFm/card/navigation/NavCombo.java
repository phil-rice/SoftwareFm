package org.softwareFm.card.navigation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;

public class NavCombo implements IHasControl {

	private Combo combo;
	private final String rootUrl;
	private Image image;

	public NavCombo(Composite parent, final CardConfig cardConfig, final String rootUrl, final ICallback<String> callbackToGotoUrl) {
		this.rootUrl = rootUrl;
		final ImageRegistry imageRegistry = new ImageRegistry();
		new BasicImageRegisterConfigurator().registerWith(parent.getDisplay(), imageRegistry);
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
				String newUrl = getSelectedUrl();
				ICallback.Utils.call(callbackToGotoUrl, newUrl);
				cardDataStore.processDataFor(rootUrl, new ICardDataStoreCallback<Void>() {
					@Override
					public Void process(String url, Map<String, Object> result) throws Exception {
						image = cardConfig.cardIconFn.apply(result);
						combo.redraw();
						return null;
					}

					@Override
					public Void noData(String url) throws Exception {
						return process(url, Collections.<String, Object> emptyMap());
					}
				});
			}

		});
		combo.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				if (image != null)
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
