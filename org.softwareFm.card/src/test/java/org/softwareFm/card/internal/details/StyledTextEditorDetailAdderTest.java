package org.softwareFm.card.internal.details;

import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.IDetailsFactoryCallback;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.editors.StyledTextEditor;
import org.softwareFm.display.data.ResourceGetterMock;

public class StyledTextEditorDetailAdderTest extends AbstractDetailsAdderTest<StyledTextEditorDetailAdder> {

	public void testWithNonStringGetNull() {
		checkGetNull(detailFactory, collectionValue);
		checkGetNull(detailFactory, intValue);
		checkGetNull(detailFactory, folderValue);
		checkGetNull(detailFactory, typedValueNotCollection);
	}

	public void testWithStyledAdderWithListGetCardCollectionsHolder() {
		checkMakesStyledTextIfStringAndKeyInEditorStyledText(stringValue);
	}

	public void testTitleSpecDerivedFromCardConfig() throws Exception {
		StyledTextEditor actual = (StyledTextEditor) detailFactory.makeDetail(shell, parentCard, cardConfig, "key", "value", IDetailsFactoryCallback.Utils.noCallback());
		assertSame(cardConfig.titleSpecFn.apply(parentCard), actual.getTitleSpec());
	}

	private void checkMakesStyledTextIfStringAndKeyInEditorStyledText(KeyValue keyValue) {
		StyledTextEditor actual = (StyledTextEditor) detailFactory.makeDetail(shell, parentCard, cardConfig, keyValue.key, keyValue.value, IDetailsFactoryCallback.Utils.noCallback());
		assertEquals("stringValue", actual.getText().getText());
	}

	@Override
	protected CardConfig makeCardConfig() {
		CardConfig raw = super.makeCardConfig();
		return raw.withResourceGetter(raw.resourceGetter.with(new ResourceGetterMock(CardConstants.editorStyledText, "key,someOther")));
	}

	@Override
	protected StyledTextEditorDetailAdder makeDetailsAdder() {
		return new StyledTextEditorDetailAdder();
	}

}
