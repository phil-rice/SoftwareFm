package org.softwareFm.collections.mySoftwareFm.internal;

import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.editors.AbstractNameAndValuesEditorTest;
import org.softwareFm.collections.explorer.internal.UserData;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.tests.Tests;

public class LoginTest extends AbstractNameAndValuesEditorTest<Login> {

	private final String salt = "someSalt";
	private final LoginStrategyMock loginStrategy = new LoginStrategyMock();
	private final LoginDisplayStrategyMock loginDisplayStrategy = new LoginDisplayStrategyMock();
	private final LoginCallbackMock loginCallback = new LoginCallbackMock();

	public void testLoginEditorWhenDisplayed() {
		checkLabelsMatch(labels, "Email", "Password");
		checkTextMatches(values, "initialEmail", "");
		assertFalse(okCancel.isOkEnabled());
	}

	public void testInitialEmailAddedToCardData() {
		assertEquals(Maps.stringObjectMap(LoginConstants.emailKey, "initialEmail", CardConstants.slingResourceType, CardConstants.loginCardType), editor.content.data());
	}

	public void testNeedEmailAndPasswordToBeOk() {
		checkOk("a.b.c@c.d", "passwordValue", true);
		checkOk("a.b.c@c.d", "", false);
		checkOk("", "passwordValue", false);
		checkOk("", "", false);
	}

	public void testLoginEditorNeedsEmailAddress() {
		checkOk("emailValue", "passwordValue", false);
		checkOk("email@", "passwordValue", false);
		checkOk("@address", "passwordValue", false);

		checkOk("a.b.c@c.d", "passwordValue", true);

	}

	public void testEmailFailedLoginStrategy() {
		checkOk("a.b.c@c.d", "somePassword", true);
		assertEquals(0, loginCallback.loggedInUserData.size());
		assertEquals(0, loginCallback.failedEmail.size());
		Swts.Buttons.press(okCancel.okButton);
		assertEquals(0, loginCallback.loggedInUserData.size());
		assertEquals(1, loginCallback.failedEmail.size());

	}

	public void testEmailAndCryptoSentToLoginStrategyWithEmailSaltOk() {
		checkOk("a.b.c@c.d", "somePassword", true);
		loginStrategy.setOk(true);
		loginStrategy.setEmailSetOk(true);
		Swts.Buttons.press(okCancel.okButton);

		assertEquals(new UserData("a.b.c@c.d", "someSoftwareFmId", loginStrategy.cryptoKey), Lists.getOnly(loginCallback.loggedInUserData));
	}

	public void testEmailAndCryptoSentToLoginStrategyWithEmailSaltFail() {
		checkOk("a.b.c@c.d", "somePassword", true);
		loginStrategy.setOk(true);
		Swts.Buttons.press(okCancel.okButton);
		assertEquals(0, loginCallback.loggedInUserData.size());

		assertEquals("a.b.c@c.d", Lists.getOnly(loginCallback.failedEmail));
		assertEquals("someMessage", Lists.getOnly(loginCallback.failedMessage));
	}

	private void checkOk(String email, String password, boolean expectedOk) {
		Swts.setText(values.getChildren()[0], email);
		Swts.setText(values.getChildren()[1], password);
		assertEquals(expectedOk, okCancel.isOkEnabled());

	}

	@Override
	protected Login makeEditor() {
		return new Login(shell, cardConfig, salt, "initialEmail", loginStrategy, loginDisplayStrategy, loginCallback);
	}

	@Override
	protected String getCardType() {
		return CardConstants.loginCardType;
	}

	public static void main(String[] args) {
		while (true)
			Tests.executeTest(LoginTest.class);
	}

}
