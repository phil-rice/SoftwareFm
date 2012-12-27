package org.softwarefm.core.composite;

import java.util.Arrays;

import org.eclipse.swt.widgets.Control;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.constants.TextKeys;
import org.softwarefm.core.tests.SwtTest;
import org.softwarefm.labelAndText.Form;

public class MavenImportCompositeTest extends SwtTest {

	private MavenImportComposite mavenImportComposite;
	private Form form;

	public void testUrlIsBlankAndImportButtonIsDisabledAtStart() {
		assertEquals(Arrays.asList(TextKeys.keyMavenImportPomUrl), form.getKeys());
		assertEquals("", mavenImportComposite.form.getText(TextKeys.keyMavenImportPomUrl));
		assertFalse(form.getButton(TextKeys.btnMavenImportUsePom).isEnabled());
	}

	public void testButtonIsOnlyEnabledWhenUrlHasLegalValue() {
		checkButtonEnabled("", false);
		checkButtonEnabled("legal.Url", true);
		checkButtonEnabled("legal.Url/a1/b2/c3/d4", true);
		checkButtonEnabled("$*(&*&illlegalUrl/1/2/3/4", false);
		checkButtonEnabled("", false);
	}

	private void checkButtonEnabled(String string, boolean expected) {
		form.setText(TextKeys.keyMavenImportPomUrl, string);
		Control button = form.getButton(TextKeys.btnMavenImportUsePom);
		assertEquals(expected, button.isEnabled());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mavenImportComposite = new MavenImportComposite(shell, SoftwareFmContainer.makeForTests(display));
		form = mavenImportComposite.form;
	}

}
