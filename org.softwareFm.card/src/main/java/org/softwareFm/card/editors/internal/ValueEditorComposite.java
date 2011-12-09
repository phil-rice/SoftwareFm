/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.editors.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.card.card.LineItem;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.IMutableCardDataStore;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.card.editors.IValueComposite;
import org.softwareFm.card.title.Title;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.okCancel.OkCancel;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.resources.IResourceGetter;

abstract public class ValueEditorComposite<T extends Control> extends Composite implements IValueComposite<T> {

	public Title title;
	private final T editorControl;
	protected final OkCancel okCancel;
	protected final CardConfig cardConfig;
	protected final String originalValue;
	private final TitleSpec titleSpec;
	protected final ValueEditorBodyComposite body;

	public ValueEditorComposite(Composite parent, int style, final CardConfig cardConfig, final String url, String cardType, final String key, Object initialValue, TitleSpec titleSpec, final IDetailsFactoryCallback callback) {
		super(parent, style);
		this.cardConfig = cardConfig;
		this.titleSpec = titleSpec;
		LineItem lineItem = new LineItem(cardType, key, initialValue);
		String name = Functions.call(cardConfig.nameFn(), lineItem);
		title = new Title(this, cardConfig, titleSpec, name, url);
		body = new ValueEditorBodyComposite(this, cardConfig, titleSpec);

		originalValue = Functions.call(cardConfig.valueFn(), lineItem);
		editorControl = makeEditorControl(body.innerBody, originalValue);
		IResourceGetter resourceGetter = Functions.call(cardConfig.resourceGetterFn, null);
		okCancel = new OkCancel(body.innerBody, resourceGetter, new Runnable() {
			@Override
			public void run() {
				IMutableCardDataStore cardDataStore = (IMutableCardDataStore) cardConfig.cardDataStore;
				String value = getValue();
				if (!value.equals(originalValue))
					callback.updateDataStore(cardDataStore, url, key, value);
				ValueEditorComposite.this.dispose();
			}
		}, new Runnable() {
			@Override
			public void run() {
				ValueEditorComposite.this.dispose();
			}
		});
		addAnyMoreButtons();

		updateEnabledStatusOfButtons();
	
		editorControl.addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(Event e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE)
					okCancel.cancel();
			}
		});
	}

	protected void addAnyMoreButtons() {
	}

	abstract protected T makeEditorControl(Composite parent, String originalValue);

	abstract protected String getValue();

	abstract protected void updateEnabledStatusOfButtons();

	@Override
	public CardConfig getCardConfig() {
		return cardConfig;
	}

	@Override
	public Title getTitle() {
		return title;
	}

	@Override
	public Composite getBody() {
		return body;
	}

	@Override
	public Composite getInnerBody() {
		return body.innerBody;
	}

	@Override
	public T getEditor() {
		return editorControl;
	}

	@Override
	public OkCancel getOkCancel() {
		return okCancel;
	}

	public TitleSpec getTitleSpec() {
		return titleSpec;
	}

}