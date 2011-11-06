package org.softwareFm.card.internal.details;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.junit.Test;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.IMutableCardDataStore;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.card.internal.editors.IValueEditor;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.ResourceGetterMock;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.runnable.Runnables;

public abstract class AbstractValueEditorDetailAdderTest<T extends IDetailAdder, TE extends IValueEditor> extends AbstractDetailsAdderTest<T> {

	private AtomicInteger updateDataStoreCount;
	protected String updatedKey;
	protected Object updatedValue;

	@Test
	public void testWithNoneStringReturnsNull() {
		checkGetNull(detailFactory, mapValue);
		checkGetNull(detailFactory, collectionValue);
		checkGetNull(detailFactory, folderValue);
		checkGetNull(detailFactory, intValue);
	}

	public void testWithStringReturnsTextEditor() {
		TE holder = makeHolder(stringValue);
		assertEquals(shell, holder.getControl().getParent());
	}

	public void testHasLabelWithTitle() {
		checkHasLabelBasedOnCardConfigNameFn("stringValue", justValue);
		checkHasLabelBasedOnCardConfigNameFn("pre_stringValue", addPrefixToValue);
	}

	private void checkHasLabelBasedOnCardConfigNameFn(String expected, IFunction1<KeyValue, String> nameFn) {
		TE holder = makeHolder(cardConfig.withNameFn(nameFn), stringValue);
		assertEquals(expected, holder.getTitleText());
	}

	public void testHasTextWithValueBasedOnCardConfigValueFn() {
		checkHasTextBasedOnCardConfigValueFn("stringValue", justValue);
		checkHasTextBasedOnCardConfigValueFn("pre_stringValue", addPrefixToValue);
	}

	public void testHasOkButtonWithNameFromCardConfigResourceGetter() {
		checkHasOkButtonWithNameFromCardConfigResourceGetter("ok");
		checkHasOkButtonWithNameFromCardConfigResourceGetter("_ok_");
	}

	public void testHasCancelButtonWithNameFromCardConfigResourceGetter() {
		checkHasCancelButtonWithNameFromCardConfigResourceGetter("cancel");
		checkHasCancelButtonWithNameFromCardConfigResourceGetter("_cancel_S");
	}

	public void testOkButtonNotEnabledIfTextNotChanged() {
		TE textEditor = makeHolder(stringValue);
		Button okButton = textEditor.getOkCancel().okButton;
		assertFalse(okButton.isEnabled());

		textEditor.setValue("some other value");
		assertTrue(okButton.isEnabled());

		textEditor.setValue(Functions.call(cardConfig.valueFn, stringValue));
		assertFalse(okButton.isEnabled());
	}

	public void testCancelButtonEnabled() {
		TE textEditor = makeHolder(stringValue);
		Button cancelButton = textEditor.getOkCancel().cancelButton;
		assertTrue(cancelButton.isEnabled());

		textEditor.setValue("some other value");
		assertTrue(cancelButton.isEnabled());

		textEditor.setValue(Functions.call(cardConfig.valueFn, stringValue));
		assertTrue(cancelButton.isEnabled());

	}

	public void testUpdatesWhenOKPressed() {
		TE textEditor = makeHolder(stringValue);
		Button okButton = textEditor.getOkCancel().okButton;
		textEditor.setValue("some other value");
		checkCardDataStoreNotUpdated();
		okButton.notifyListeners(SWT.Selection, new Event());
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

	private void checkPressCausesDispose(TE textEditor, Button button) {
		assertFalse(textEditor.getControl().isDisposed());
		button.notifyListeners(SWT.Selection, new Event());
		assertTrue(textEditor.getControl().isDisposed());
	}

	private void checkCardDataStoreNotUpdated() {
		assertEquals(0, cardDataStore.rememberedPuts.size());
	}

	private void checkCardDataStoreUpdated(String url,String key, String value) {
		assertEquals(1, updateDataStoreCount.get());
		assertEquals(updatedKey, key);
		assertEquals(updatedValue, value);
		assertEquals(0, cardDataStore.rememberedPuts.size()); // The card data store should be updated by the callback, not by other means
	}

	private void checkHasOkButtonWithNameFromCardConfigResourceGetter(String expected) {
		TE textEditor = makeWithResourceGetterWith(DisplayConstants.buttonOkTitle, expected);
		assertEquals(expected, textEditor.getOkCancel().okButton.getText());
	}

	private void checkHasCancelButtonWithNameFromCardConfigResourceGetter(String expected) {
		TE textEditor = makeWithResourceGetterWith(DisplayConstants.buttonCancelTitle, expected);
		assertEquals(expected, textEditor.getOkCancel().cancelButton.getText());
	}

	private TE makeWithResourceGetterWith(String key, String expected) {
		IResourceGetter with = IResourceGetter.Utils.noResources().//
				with(cardConfig.resourceGetter).//
				with(new ResourceGetterMock(key, expected));//
		TE textEditor = makeHolder(cardConfig.withResourceGetter(with), stringValue);
		return textEditor;
	}

	private void checkHasTextBasedOnCardConfigValueFn(String expected, IFunction1<KeyValue, String> valueFn) {
		TE holder = makeHolder(cardConfig.withValueFn(valueFn), stringValue);
		assertEquals(expected, holder.getValue());
	}

	private TE makeHolder(CardConfig cardConfig, KeyValue keyValue) {
		return makeHolder(cardConfig, keyValue, Runnables.noRunnable);

	}

	private TE makeHolder(CardConfig cardConfig, KeyValue keyValue, final Runnable afterEdit) {
		IHasControl actual = adder.add(shell, parentCard, cardConfig, keyValue.key, keyValue.value, new IDetailsFactoryCallback() {

			@Override
			public void cardSelected(ICard card) {
			}

			@Override
			public void gotData(Control hasControl) {
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
		TE holder = (TE) actual;
		return holder;
	}

	private TE makeHolder(KeyValue keyValue) {
		return makeHolder(cardConfig, keyValue);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		updateDataStoreCount = new AtomicInteger();
	}

}
