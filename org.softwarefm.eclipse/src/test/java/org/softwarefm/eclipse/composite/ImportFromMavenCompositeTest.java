package org.softwarefm.eclipse.composite;

import java.util.Arrays;

import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.SwtTest;
import org.softwarefm.eclipse.constants.SwtConstants;
import org.softwarefm.labelAndText.Form;

public class ImportFromMavenCompositeTest extends SwtTest {

	private ImportFromMavenComposite importFromMavenComposite;
	private Form form;

	public void testUrlIsBlankAndImportButtonIsDisabledAtStart() {
		assertEquals(Arrays.asList(SwtConstants.pomUrlKey), form.getKeys());
		assertEquals("", importFromMavenComposite.form.getText(SwtConstants.pomUrlKey));
		assertFalse(form.getButton(SwtConstants.linkFromMavenButtonText).isEnabled());
	}
	
	public void testButtonIsOnlyEnabledWhenUrlHasLegalValue(){
		checkButtonEnabled("", false);
		checkButtonEnabled("legal.Url", true);
		checkButtonEnabled("legal.Url/a1/b2/c3/d4", true);
		checkButtonEnabled("$*(&*&illlegalUrl/1/2/3/4", false);
		checkButtonEnabled("", false);
	}

	private void checkButtonEnabled(String string, boolean expected) {
		form.setText(SwtConstants.pomUrlKey, string);
		assertEquals(expected, form.getButton(SwtConstants.linkFromMavenButtonText).isEnabled());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		importFromMavenComposite = new ImportFromMavenComposite(shell, SoftwareFmContainer.makeForTests());
		form = importFromMavenComposite.form;
	}

}
