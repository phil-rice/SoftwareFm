package org.softwareFm.card.internal.details;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.junit.Test;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.card.internal.editors.TextEditor;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.ResourceGetterMock;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.runnable.Runnables;
import org.softwareFm.utilities.runnable.Runnables.CountRunnable;

public class TextEditorDetailAdderTest extends AbstractDetailsAdderTest<TextEditorDetailAdder> {

	@Test
	public void testWithNoneStringReturnsNull() {
		checkGetNull(detailFactory, listValue);
		checkGetNull(detailFactory, collectionValue);
		checkGetNull(detailFactory, folderValue);
		checkGetNull(detailFactory, intValue);
	}

	public void testWithStringReturnsTextEditor() {
		TextEditor holder = makeHolder(stringValue);
		assertEquals(shell, holder.getControl().getParent());
	}

	public void testHasLabelWithTitle() {
		checkHasLabelBasedOnCardConfigNameFn("stringValue", justValue);
		checkHasLabelBasedOnCardConfigNameFn("pre_stringValue", addPrefixToValue);
	}

	private void checkHasLabelBasedOnCardConfigNameFn(String expected, IFunction1<KeyValue, String> nameFn) {
		TextEditor holder = makeHolder(cardConfig.withNameFn(nameFn), stringValue);
		assertEquals(expected, holder.getTitleLabel().getText());
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
		TextEditor textEditor = makeHolder(stringValue);
		Button okButton = textEditor.getOkButton();
		assertFalse(okButton.isEnabled());

		textEditor.getText().setText("some other value");
		assertTrue(okButton.isEnabled());

		textEditor.getText().setText(Functions.call(cardConfig.valueFn, stringValue));
		assertFalse(okButton.isEnabled());
	}

	public void testCancelButtonEnabled() {
		TextEditor textEditor = makeHolder(stringValue);
		Button cancelButton = textEditor.getCancelButton();
		assertTrue(cancelButton.isEnabled());

		textEditor.getText().setText("some other value");
		assertTrue(cancelButton.isEnabled());

		textEditor.getText().setText(Functions.call(cardConfig.valueFn, stringValue));
		assertTrue(cancelButton.isEnabled());

	}

	public void testUpdatesWhenOKPressed() {
		TextEditor textEditor = makeHolder(stringValue);
		Button okButton = textEditor.getOkButton();
		textEditor.getText().setText("some other value");
		checkCardDataStoreNotUpdated();
		okButton.notifyListeners(SWT.Selection, new Event());
		checkCardDataStoreUpdated(parentCard.url(), "key", "some other value");
	}

	public void testDisposesWhenOkPressed() {
		TextEditor textEditor = makeHolder(stringValue);
		checkPressCausesDispose(textEditor, textEditor.getOkButton());
	}

	public void testDisposesWhenCancelPressed() {
		TextEditor textEditor = makeHolder(stringValue);
		checkPressCausesDispose(textEditor, textEditor.getCancelButton());
	}

	@Override
	public void testAfterEditHappensAfterCardDataStoreUpdated() {
		CountRunnable count = new CountRunnable(){
			@Override
			protected void detail() {
				assertEquals(getCount(), cardDataStore.rememberedPuts.size());
			}
		};
		TextEditor textEditor = makeHolder(cardConfig, stringValue, count);
		Button okButton = textEditor.getOkButton();
		textEditor.getText().setText("some other value");
		assertEquals(0, count.getCount());
		okButton.notifyListeners(SWT.Selection, new Event());
		assertEquals(1, count.getCount());
	}

	public void testDoesntUpdateWhenCancelPressed() {
		TextEditor textEditor = makeHolder(stringValue);
		textEditor.getText().setText("some other value");
		checkPressCausesDispose(textEditor, textEditor.getCancelButton());
		checkCardDataStoreNotUpdated();
	}

	private void checkPressCausesDispose(TextEditor textEditor, Button button) {
		assertFalse(textEditor.getControl().isDisposed());
		button.notifyListeners(SWT.Selection, new Event());
		assertTrue(textEditor.getControl().isDisposed());
	}

	private void checkCardDataStoreNotUpdated() {
		assertEquals(0, cardDataStore.rememberedPuts.size());
	}

	private void checkCardDataStoreUpdated(String url, Object... expected) {
		assertEquals(1, cardDataStore.rememberedPuts.size());
		List<Map<String, Object>> list = cardDataStore.rememberedPuts.get(url);
		assertEquals(1, list.size());
		Map<String, Object> actual = list.get(0);
		assertEquals(Maps.makeMap(expected), actual);
	}

	private void checkHasOkButtonWithNameFromCardConfigResourceGetter(String expected) {
		TextEditor textEditor = makeWithResourceGetterWith(DisplayConstants.buttonOkTitle, expected);
		assertEquals(expected, textEditor.getOkButton().getText());
	}

	private void checkHasCancelButtonWithNameFromCardConfigResourceGetter(String expected) {
		TextEditor textEditor = makeWithResourceGetterWith(DisplayConstants.buttonCancelTitle, expected);
		assertEquals(expected, textEditor.getCancelButton().getText());
	}

	private TextEditor makeWithResourceGetterWith(String key, String expected) {
		IResourceGetter with = IResourceGetter.Utils.noResources().//
				with(cardConfig.resourceGetter).//
				with(new ResourceGetterMock(key, expected));//
		TextEditor textEditor = makeHolder(cardConfig.withResourceGetter(with), stringValue);
		return textEditor;
	}

	private void checkHasTextBasedOnCardConfigValueFn(String expected, IFunction1<KeyValue, String> valueFn) {
		TextEditor holder = makeHolder(cardConfig.withValueFn(valueFn), stringValue);
		assertEquals(expected, holder.getText().getText());
	}

	private TextEditor makeHolder(CardConfig cardConfig, KeyValue keyValue) {
		return makeHolder(cardConfig, keyValue, Runnables.noRunnable);

	}

	private TextEditor makeHolder(CardConfig cardConfig, KeyValue keyValue, Runnable afterEdit) {
		IHasControl actual = adder.add(shell, parentCard, cardConfig, keyValue, ICardSelectedListener.Utils.noListener(), afterEdit);
		TextEditor holder = (TextEditor) actual;
		return holder;
	}

	private TextEditor makeHolder(KeyValue keyValue) {
		return makeHolder(cardConfig, keyValue);
	}

	@Override
	protected TextEditorDetailAdder makeDetailsAdder() {
		return new TextEditorDetailAdder();
	}

}
