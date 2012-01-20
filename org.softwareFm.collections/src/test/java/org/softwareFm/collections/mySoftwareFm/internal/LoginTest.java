package org.softwareFm.collections.mySoftwareFm.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.editors.AbstractNameAndValuesEditorTest;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.collections.Lists;

public class LoginTest extends AbstractNameAndValuesEditorTest<Login> {

	private final String salt = "someSalt";
	private final LoginStrategyMock loginStrategy = new LoginStrategyMock();
	private final LoginDisplayStrategyMock loginDisplayStrategy = new LoginDisplayStrategyMock();
	private final LoginCallbackMock loginCallback = new LoginCallbackMock();

	public void testLoginEditorWhenDisplayed() {
		checkLabelsMatch(labels, "Email", "Password");
		checkTextMatches(values, "", "");
		assertFalse(okCancel.isOkEnabled());
	}

	public void testNeedEmailAndPasswordToBeOk() {
		checkOk("emailValue", "passwordValue", true);
		checkOk("emailValue", "", false);
		checkOk("", "passwordValue", false);
		checkOk("", "", false);
	}

	public void testEmailFailedLoginStrategy() {
		checkOk("someEmail", "somePassword", true);
		assertEquals(0, loginCallback.loggedInEmail.size());
		assertEquals(0, loginCallback.failedEmail.size());
		okCancel.okButton.notifyListeners(SWT.Selection, new Event());
		assertEquals(0, loginCallback.loggedInEmail.size());
		assertEquals(1, loginCallback.failedEmail.size());

	}

	public void testEmailAndCryptoSentToLoginStrategyWithEmailSaltOk() {
		checkOk("someEmail", "somePassword", true);
		loginStrategy.setOk(true);
		loginStrategy.setEmailSetOk(true);
		okCancel.okButton.notifyListeners(SWT.Selection, new Event());
		assertEquals("someEmail", Lists.getOnly(loginCallback.loggedInEmail));
		assertEquals(loginStrategy.cryptoKey, Lists.getOnly(loginCallback.loggedInCrypto));
		assertEquals("someEmail", Lists.getOnly(loginCallback.loggedInEmail));
	}

	public void testEmailAndCryptoSentToLoginStrategyWithEmailSaltFail() {
		checkOk("someEmail", "somePassword", true);
		loginStrategy.setOk(true);
		okCancel.okButton.notifyListeners(SWT.Selection, new Event());
		assertEquals(0, loginCallback.loggedInEmail.size());
		
		assertEquals("someEmail", Lists.getOnly(loginCallback.failedEmail));
		assertEquals("someMessage", Lists.getOnly(loginCallback.failedMessage));
	}

	private void checkOk(String email, String password, boolean expectedOk) {
		Swts.setText(values.getChildren()[0], email);
		Swts.setText(values.getChildren()[1], password);
		assertEquals(expectedOk, okCancel.isOkEnabled());

	}

	@Override
	protected Login makeEditor() {
		return new Login(shell, cardConfig, salt, loginStrategy, loginDisplayStrategy, loginCallback);
	}

	@Override
	protected String getCardType() {
		return CardConstants.loginCardType;
	}

}
