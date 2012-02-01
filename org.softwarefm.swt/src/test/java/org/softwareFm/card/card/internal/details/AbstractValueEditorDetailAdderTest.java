/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card.internal.details;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.junit.Test;
import org.softwareFm.card.card.ILineItemFunction;
import org.softwareFm.card.card.LineItem;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.IMutableCardDataStore;
import org.softwareFm.card.details.IDetailAdder;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.card.editors.internal.IValueEditorForTests;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.runnable.Runnables;

public abstract class AbstractValueEditorDetailAdderTest<T extends IDetailAdder, TE extends IValueEditorForTests> extends AbstractDetailsAdderTest<T> {

	private AtomicInteger updateDataStoreCount;
	protected String updatedKey;
	protected Object updatedValue;

	@Test
	public void testWithNoneStringReturnsNull() {
		checkGetNull(detailFactory, mapValue);
		checkGetNull(detailFactory, collectionValue);
		checkGetNull(detailFactory, folderValue);
		checkGetNull(detailFactory, intValue);
		checkGetNull(detailFactory, typedValueNotCollection);
	}

	public void testWithStringReturnsTextEditor() {
		TE holder = makeHolder(stringValue);
		assertEquals(shell, holder.getControl().getParent());
	}

	public void testHasLabelWithTitle() {
		checkHasLabelBasedOnCardConfigNameFn("stringValue", justValue);
		checkHasLabelBasedOnCardConfigNameFn("pre_stringValue", addPrefixToValue);
	}

	private void checkHasLabelBasedOnCardConfigNameFn(String expected, ILineItemFunction<String> nameFn) {
		TE holder = makeHolder(cardConfig.withNameFn(nameFn), stringValue);
		assertEquals(expected, holder.getTitleText());
	}

	public void testHasTextWithValueBasedOnCardConfigValueFn() {
		checkHasTextBasedOnCardConfigValueFn("stringValue", justValue);
		checkHasTextBasedOnCardConfigValueFn("pre_stringValue", addPrefixToValue);
	}


	public void testOkButtonNotEnabledIfTextNotChanged() {
		TE textEditor = makeHolder(stringValue);
		Label okButton = textEditor.getOkCancel().okButton;
		assertFalse(okButton.isEnabled());

		textEditor.setValue("someothervalue");
		assertTrue(okButton.isEnabled());

		textEditor.setValue(cardConfig.valueFn.apply(null, stringValue));
		assertFalse(okButton.isEnabled());
	}

	public void testCancelButtonEnabled() {
		TE textEditor = makeHolder(stringValue);
		Label cancelButton = textEditor.getOkCancel().cancelButton;
		assertTrue(cancelButton.isEnabled());

		textEditor.setValue("some other value");
		assertTrue(cancelButton.isEnabled());

		textEditor.setValue(cardConfig.valueFn.apply(null, stringValue));
		assertTrue(cancelButton.isEnabled());

	}

	public void testUpdatesWhenOKPressed() {
		TE textEditor = makeHolder(stringValue);
		Label okButton = textEditor.getOkCancel().okButton;
		textEditor.setValue("some other value");
		checkCardDataStoreNotUpdated();
		okButton.setEnabled(true);
		Swts.Buttons.press(okButton);
		checkCardDataStoreUpdated(parentCard.url(), "key", "some other value");
	}

	public void testDisposesWhenOkPressed() {
		TE textEditor = makeHolder(stringValue);
		checkPressCausesDispose(textEditor, textEditor.getOkCancel().okButton);
	}

	public void testDisposesWhenCancelPressed() {
		TE textEditor = makeHolder(stringValue);
		checkPressCausesDispose(textEditor, textEditor.getOkCancel().cancelButton);
	}

	public void testDoesntUpdateWhenCancelPressed() {
		TE textEditor = makeHolder(stringValue);
		textEditor.setValue("some other value");
		checkPressCausesDispose(textEditor, textEditor.getOkCancel().cancelButton);
		checkCardDataStoreNotUpdated();
	}

	private void checkPressCausesDispose(TE textEditor, Label button) {
		button.setEnabled(true);
		assertFalse(textEditor.getControl().isDisposed());
		Swts.Buttons.press(button);
		assertTrue(textEditor.getControl().isDisposed());
	}

	private void checkCardDataStoreNotUpdated() {
		assertEquals(0, cardDataStore.rememberedPuts.size());
	}

	private void checkCardDataStoreUpdated(String url, String key, String value) {
		assertEquals(1, updateDataStoreCount.get());
		assertEquals(updatedKey, key);
		assertEquals(updatedValue, value);
		assertEquals(0, cardDataStore.rememberedPuts.size()); // The card data store should be updated by the callback, not by other means
	}



	private void checkHasTextBasedOnCardConfigValueFn(String expected, ILineItemFunction<String> valueFn) {
		TE holder = makeHolder(cardConfig.withValueFn(valueFn), stringValue);
		assertEquals(expected, holder.getValue());
	}

	private TE makeHolder(CardConfig cardConfig, LineItem lineItem) {
		return makeHolder(cardConfig, lineItem, Runnables.noRunnable);

	}

	private TE makeHolder(CardConfig cardConfig, LineItem lineItem, final Runnable afterEdit) {
		IHasControl actual = adder.add(shell, parentCard, cardConfig, lineItem.key, lineItem.value, new IDetailsFactoryCallback() {

			@Override
			public void cardSelected(String cardUrl) {
			}

			@Override
			public void gotData(Control control) {
			}

			@Override
			public void afterEdit(String url) {
				afterEdit.run();
			}

			@Override
			public void updateDataStore(IMutableCardDataStore store, String url, String key, Object value) {
				updateDataStoreCount.incrementAndGet();
				updatedKey = key;
				updatedValue = value;
				afterEdit.run();
			}
		});
		@SuppressWarnings("unchecked")
		TE holder = (TE) actual;
		return holder;
	}

	private TE makeHolder(LineItem lineItem) {
		return makeHolder(cardConfig, lineItem);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		updateDataStoreCount = new AtomicInteger();
	}

}