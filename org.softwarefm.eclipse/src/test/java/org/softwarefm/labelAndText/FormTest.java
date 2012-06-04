package org.softwarefm.labelAndText;

import org.eclipse.swt.SWT;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.SwtTest;
import org.softwarefm.eclipse.constants.SwtConstants;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.utilities.collections.Lists;

public class FormTest extends SwtTest {

	private Form form;
	private ButtonExecutorMock ok;
	private ButtonExecutorMock cancel;
	private IButtonConfigurator buttonConfigurator;
	private SoftwareFmContainer<Object> container;
	private String[] keys;

	public void testUsesButtonExecutorsOnConstructorToSetButtonStatus() {
		checkCanExecuteCounts(1, 1);
		assertFalse(form.getButton(SwtConstants.okButton).isEnabled());
		assertFalse(form.getButton(SwtConstants.cancelButton).isEnabled());

		ok.canExecute = true;
		form = new Form(shell, SWT.NULL, container, buttonConfigurator, keys);
		assertTrue(form.getButton(SwtConstants.okButton).isEnabled());
		assertFalse(form.getButton(SwtConstants.cancelButton).isEnabled());
	}

	public void testSetTextCausesButtonStatusToChange() {
		assertFalse(form.getButton(SwtConstants.okButton).isEnabled());
		assertFalse(form.getButton(SwtConstants.cancelButton).isEnabled());
		ok.canExecute = true;
		form.setText(SwtConstants.groupIdKey, "g");
		assertTrue(form.getButton(SwtConstants.okButton).isEnabled());
		assertFalse(form.getButton(SwtConstants.cancelButton).isEnabled());
	}

	public void testUsesButtonExecutorsWhenSetText() {
		checkCanExecuteCounts(1, 1);
		form.setText(SwtConstants.groupIdKey, "g");
		checkCanExecuteCounts(2, 2);
		form.setText(SwtConstants.artifactIdKey, "a");
		checkCanExecuteCounts(3, 3);
		form.setText(SwtConstants.versionKey, "v");
		checkCanExecuteCounts(4, 4);
		form.setText(SwtConstants.groupIdKey, "g");
		checkCanExecuteCounts(5, 5);
	}

	private void checkCanExecuteCounts(int okCount, int cancelCount) {
		assertEquals(okCount, ok.canExecuteCount.get());
		assertEquals(Lists.times(okCount, form), ok.getTextWithKeys);

		assertEquals(cancelCount, cancel.canExecuteCount.get());
		assertEquals(Lists.times(cancelCount, form), cancel.getTextWithKeys);

	}

	public void testSetGetTextAndOkCancel() {
		checkText("", "", "");
		setText("g", "a", "v");
		checkText("g", "a", "v");
		checkCounts(0, 0);
		Swts.Buttons.press(form.getButton(SwtConstants.okButton));
		checkCounts(1, 0);
		Swts.Buttons.press(form.getButton(SwtConstants.cancelButton));
		checkCounts(1, 1);
	}

	private void checkCounts(int okCount, int cancelCount) {
		assertEquals(okCount, ok.executeCount.get());
		assertEquals(cancelCount, cancel.executeCount.get());
	}

	private void setText(String g, String a, String v) {
		form.setText(SwtConstants.groupIdKey, g);
		form.setText(SwtConstants.artifactIdKey, a);
		form.setText(SwtConstants.versionKey, v);

	}

	private void checkText(String g, String a, String v) {
		assertEquals(g, form.getText(SwtConstants.groupIdKey));
		assertEquals(a, form.getText(SwtConstants.artifactIdKey));
		assertEquals(v, form.getText(SwtConstants.versionKey));

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ok = new ButtonExecutorMock(SwtConstants.okButton);
		cancel = new ButtonExecutorMock(SwtConstants.cancelButton);
		container = SoftwareFmContainer.makeForTests();
		buttonConfigurator = IButtonConfigurator.Utils.make(ok, cancel);
		keys = new String[] { SwtConstants.groupIdKey, SwtConstants.artifactIdKey, SwtConstants.versionKey };
		form = new Form(shell, SWT.NULL, container, buttonConfigurator, keys);
	}
}
