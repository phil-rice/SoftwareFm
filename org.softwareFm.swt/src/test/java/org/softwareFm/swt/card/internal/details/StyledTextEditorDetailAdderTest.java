/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.internal.details;

import org.eclipse.swt.graphics.Image;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1WithDispose;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.resources.ResourceGetterMock;
import org.softwareFm.images.BasicImageRegisterConfigurator;
import org.softwareFm.swt.card.LineItem;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.details.IDetailsFactoryCallback;
import org.softwareFm.swt.editors.IEditorDetailAdder;
import org.softwareFm.swt.editors.internal.StyledTextEditor;
import org.softwareFm.swt.editors.internal.StyledTextEditorDetailAdder;

public class StyledTextEditorDetailAdderTest extends AbstractDetailsAdderTest<StyledTextEditorDetailAdder> {

	private IFunction1WithDispose<String, Image> imageFn;

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
		IResourceGetter resourceGetter = raw.with(new ResourceGetterMock());
		imageFn = BasicImageRegisterConfigurator.imageFnForTests(shell);
		return cardConfig.//
				withImageFn(imageFn).//
				withResourceGetterFn(Functions.<String, IResourceGetter> constant(resourceGetter));
	}
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		imageFn.dispose();
	}

	@Override
	protected StyledTextEditorDetailAdder makeDetailsAdder() {
		return (StyledTextEditorDetailAdder) IEditorDetailAdder.Utils.styledText();
	}

}