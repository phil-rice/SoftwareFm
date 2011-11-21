package org.softwareFm.card.card.internal.details;

import org.softwareFm.card.card.CardConfig;
import org.softwareFm.card.card.LineItem;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.card.editors.internal.StyledTextEditor;
import org.softwareFm.card.editors.internal.StyledTextEditorDetailAdder;
import org.softwareFm.display.data.ResourceGetterMock;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.resources.IResourceGetter;

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

	private void checkMakesStyledTextIfStringAndKeyInEditorStyledText(LineItem lineItem) {
		StyledTextEditor actual = (StyledTextEditor) detailFactory.makeDetail(shell, parentCard, cardConfig, lineItem.key, lineItem.value, IDetailsFactoryCallback.Utils.noCallback());
		assertEquals("stringValue", actual.getText().getText());
	}

	@Override
	protected CardConfig makeCardConfig() {
		CardConfig cardConfig = super.makeCardConfig();
		IResourceGetter raw = Functions.call(cardConfig.resourceGetterFn, null);
		IResourceGetter resourceGetter = raw.with(new ResourceGetterMock(CardConstants.editorStyledText, "key,someOther"));
		return cardConfig.withResourceGetterFn(Functions.<String,IResourceGetter>constant(resourceGetter));
	}

	@Override
	protected StyledTextEditorDetailAdder makeDetailsAdder() {
		return new StyledTextEditorDetailAdder();
	}

}
