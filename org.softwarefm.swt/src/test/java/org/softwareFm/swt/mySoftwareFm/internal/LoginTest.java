/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.mySoftwareFm.internal;

import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.tests.Tests;
import org.softwareFm.swt.card.editors.AbstractNameAndValuesEditorTest;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.explorer.internal.UserData;
import org.softwareFm.swt.swt.Swts;

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
		Swts.Buttons.press(okCancel.okButton());
		assertEquals(0, loginCallback.loggedInUserData.size());
		assertEquals(1, loginCallback.failedEmail.size());

	}

	public void testEmailAndCryptoSentToLoginStrategyWithEmailSaltOk() {
		checkOk("a.b.c@c.d", "somePassword", true);
		loginStrategy.setOk(true);
		loginStrategy.setEmailSetOk(true);
		Swts.Buttons.press(okCancel.okButton());

		assertEquals(new UserData("a.b.c@c.d", "someSoftwareFmId", loginStrategy.cryptoKey), Lists.getOnly(loginCallback.loggedInUserData));
	}

	public void testEmailAndCryptoSentToLoginStrategyWithEmailSaltFail() {
		checkOk("a.b.c@c.d", "somePassword", true);
		loginStrategy.setOk(true);
		Swts.Buttons.press(okCancel.okButton());
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