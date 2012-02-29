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
import org.softwareFm.swt.editors.IDataCompositeWithOkCancel;
import org.softwareFm.swt.editors.NameAndValuesEditor.NameAndValuesEditorComposite;
import org.softwareFm.swt.okCancel.IOkCancelForTests;
import org.softwareFm.swt.swt.SwtTest;

public abstract class AbstractNameAndValuesEditorTest<T extends IHasComposite> extends SwtTest {
	abstract protected T makeEditor();

	abstract protected String getCardType();

	protected CardConfig cardConfig;
	protected AddCardCallbackMock callback;
	protected IOkCancelForTests okCancel;
	protected String cardType;
	protected T editor;
	protected Composite editorComposite;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardConfig = ICardConfigurator.Utils.cardConfigForTests(display);
		callback = new AddCardCallbackMock();
		cardType = getCardType();
		editor = makeEditor();
		editorComposite = ((NameAndValuesEditorComposite) editor.getComposite()).getEditor();
		@SuppressWarnings("unchecked")
		IDataCompositeWithOkCancel<SashForm> composite = (IDataCompositeWithOkCancel<SashForm>) editor.getComposite();
		okCancel = (IOkCancelForTests) composite.getFooter();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		cardConfig.dispose();
	}

}