/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.navigation.internal;

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
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.dataStore.CardDataStoreCallbackAdapter;
import org.softwareFm.swt.dataStore.ICardDataStore;
import org.softwareFm.swt.dataStore.ICardDataStoreCallback;

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
				e.gc.fillRectangle(clientArea);
				if (image == null) {
					e.gc.drawText("/", 3, 0);
					// e.gc.drawRectangle(clientArea.x,clientArea.y,clientArea.width-1, clientArea.width-2);
				} else
					e.gc.drawImage(image, 1, 0);
			}
		});
		cardDataStore.processDataFor(rootUrl, new CardDataStoreCallbackAdapter<Void>() {
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
				image = cardConfig.navIconFn.apply(result);
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
		if (combo.isDisposed())
			return;
		combo.removeAll();
		for (String item : items)
			combo.add(item);
	}

	@Override
	public Control getControl() {
		return combo;
	}
}