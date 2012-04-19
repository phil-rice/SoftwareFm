/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.editors.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.swt.card.LineItem;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.dataStore.IMutableCardDataStore;
import org.softwareFm.swt.details.IDetailsFactoryCallback;
import org.softwareFm.swt.editors.DataWithOkCancelComposite;
import org.softwareFm.swt.title.TitleSpec;

abstract public class ValueEditorComposite<T extends Control> extends DataWithOkCancelComposite<T> {

	private final T editorControl;
	protected final String originalValue;
	private final String url;
	private final String key;
	private final IDetailsFactoryCallback detailsFactoryCallback;

	public ValueEditorComposite(Composite parent, int style, final CardConfig cardConfig, final String url, String cardType, final String key, Object initialValue, TitleSpec titleSpec, final IDetailsFactoryCallback callback) {
		super(parent, cardConfig, cardType, Functions.call(cardConfig.nameFn(), new LineItem(cardType, key, initialValue)));
		this.url = url;
		this.key = key;
		this.detailsFactoryCallback = callback;

		originalValue = Functions.call(cardConfig.valueFn(), new LineItem(cardType, key, initialValue));
		editorControl = makeEditorControl(getInnerBody(), originalValue);

		addAnyMoreButtons();

		updateEnabledStatusOfButtons();

		editorControl.addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(Event e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE)
					getFooter().cancel();
			}
		});
	}

	@Override
	protected void ok() {
		IMutableCardDataStore cardDataStore = getCardConfig().cardDataStore;
		String value = getValue();
		if (!value.equals(originalValue))
			detailsFactoryCallback.updateDataStore(cardDataStore, url, key, value);
		cancel();
	}

	@Override
	protected void cancel() {
		ValueEditorComposite.this.dispose();
	}

	protected void addAnyMoreButtons() {
	}

	abstract protected T makeEditorControl(Composite parent, String originalValue);

	abstract protected String getValue();

	abstract protected void updateEnabledStatusOfButtons();

	@Override
	public T getEditor() {
		return editorControl;
	}

}