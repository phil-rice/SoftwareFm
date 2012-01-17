package org.softwareFm.card.editors;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.configuration.ICardConfigurator;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.okCancel.OkCancel;
import org.softwareFm.display.swt.SwtTest;

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

}
