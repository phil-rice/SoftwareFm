package org.softwarefm.eclipse.composite;

import java.util.Arrays;

import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.TextKeys;
import org.softwarefm.eclipse.tests.SwtTest;
import org.softwarefm.labelAndText.Form;

public class MavenImportCompositeTest extends SwtTest {

	private MavenImportComposite mavenImportComposite;
	private Form form;

	public void testUrlIsBlankAndImportButtonIsDisabledAtStart() {
		assertEquals(Arrays.asList(TextKeys.keyMavenImportPomUrl), form.getKeys());
		assertEquals("", mavenImportComposite.form.getText(TextKeys.keyMavenImportPomUrl));
		assertFalse(form.getButton(TextKeys.btnMavenImportUsePom).isEnabled());
	}
	
	public void testButtonIsOnlyEnabledWhenUrlHasLegalValue(){
		checkButtonEnabled("", false);
		checkButtonEnabled("legal.Url", true);
		checkButtonEnabled("legal.Url/a1/b2/c3/d4", true);
		checkButtonEnabled("$*(&*&illlegalUrl/1/2/3/4", false);
		checkButtonEnabled("", false);
	}

	private void checkButtonEnabled(String string, boolean expected) {
		form.setText(TextKeys.keyMavenImportPomUrl, string);
		assertEquals(expected, form.getButton(TextKeys.btnMavenImportUsePom).isEnabled());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mavenImportComposite = new MavenImportComposite(shell, SoftwareFmContainer.makeForTests());
		form = mavenImportComposite.form;
	}

}
