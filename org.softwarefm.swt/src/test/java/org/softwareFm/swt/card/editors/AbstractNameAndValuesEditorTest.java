/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.editors;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.editors.AddCardCallbackMock;
import org.softwareFm.swt.editors.IValueComposite;
import org.softwareFm.swt.okCancel.OkCancel;
import org.softwareFm.swt.swt.SwtTest;

public abstract class AbstractNameAndValuesEditorTest<T extends IHasComposite> extends SwtTest {
	abstract protected T makeEditor();

	abstract protected String getCardType();

	protected CardConfig cardConfig;
	protected AddCardCallbackMock callback;
	protected OkCancel okCancel;
	protected Composite labels;
	protected Composite values;
	protected String cardType;
	protected T editor;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardConfig = ICardConfigurator.Utils.cardConfigForTests(display);
		callback = new AddCardCallbackMock();
		cardType = getCardType();
		editor = makeEditor();
		@SuppressWarnings("unchecked")
		IValueComposite<SashForm> composite = (IValueComposite<SashForm>) editor.getComposite();
		okCancel = composite.getOkCancel();
		SashForm sashForm = composite.getEditor();
		assertEquals(2, sashForm.getChildren().length);
		labels = (Composite) sashForm.getChildren()[0];
		values = (Composite) sashForm.getChildren()[1];
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		cardConfig.dispose();
	}

}