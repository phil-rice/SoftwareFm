/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.usage.internal;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.softwareFm.crowdsource.api.UserData;
import org.softwareFm.crowdsource.httpClient.IHttpClient;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.httpClient.internal.MemoryResponseCallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.tests.IIntegrationTest;
import org.softwareFm.crowdsource.utilities.url.Urls;
import org.softwareFm.jarAndClassPath.api.IUserDataManager;
import org.softwareFm.swt.card.composites.CompositeWithCardMargin;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.editors.DataComposite;
import org.softwareFm.swt.editors.IDataComposite;
import org.softwareFm.swt.editors.IDataCompositeWithOkCancel;
import org.softwareFm.swt.explorer.IShowMyData;
import org.softwareFm.swt.explorer.IShowMyGroups;
import org.softwareFm.swt.explorer.internal.MySoftwareFm;
import org.softwareFm.swt.login.ILoginStrategy;
import org.softwareFm.swt.okCancel.IOkCancelForTests;
import org.softwareFm.swt.swt.Swts;
import org.springframework.jdbc.core.JdbcTemplate;

public class MySoftwareFmIntegrationTest extends ApiAndSwtTest implements IIntegrationTest {

	private MySoftwareFm mySoftwareFm;
	private Composite softwareFmComposite;

	private final String email = "a.b@c.com";
	private final String password = "pass1";
	private final String moniker = "moniker";

	private CardConfig cardConfig;
	private File userFile;
	private IUserDataManager userDataManager;

	public void testForgotPassword() throws Exception {
		signUp(email, moniker, password);
		String crypto = getTemplate().queryForObject("select crypto from users", String.class);

		mySoftwareFm.logout();
		mySoftwareFm.start();
		displayUntilEmailPassword().pressButton(0);
		IDataCompositeWithOkCancel<Composite> emailAndMessageComposite = displayUntilValueComposite("Email", "");// the empty string is the label for 'message'
		Swts.checkTextMatches(emailAndMessageComposite.getEditor(), email, "When your email address is correctly entered, please click the 'OK' button below. SoftwareFM will then email you a link to your password.");
		setValues(email);
		assertTrue(getOkCancel().isOkEnabled());
		getOkCancel().ok();
		displayUntilCompositeWithCardMargin();
		assertEquals("Reminder email sent to " + email + "\n\nClicking this panel will start login again", getText());

		assertEquals(new UserData(email, null, null), userDataManager.getUserData());

		mySoftwareFm.start();
		displayUntilValueComposite("Email", "Password");

		String magicString = getTemplate().queryForObject("select passwordResetKey from users", String.class);
		String newPassword = resetPassword(magicString);
		System.out.println("password: " + newPassword);
		setValues(email, newPassword);
		assertTrue(getOkCancel().isOkEnabled());
		getOkCancel().ok();
		displayUntilDataCompositeWithTitle("My Software Fm");
		checkLoggedInDisplay(crypto, "someNewSoftwareFmId0");

		assertEquals(new UserData(email, "someNewSoftwareFmId0", crypto), userDataManager.getUserData());
	}

	public void testSignupThenLogin() {
		assertSame(userDataManager.getUserData(), mySoftwareFm.getUserData());
		signUp(email, moniker, password);

		String crypto = getTemplate().queryForObject("select crypto from users", String.class);
		String softwareFmId = getTemplate().queryForObject("select softwarefmid from users", String.class);
		mySoftwareFm.logout();

		mySoftwareFm.start();
		assertNull(userDataManager.getUserData().crypto);
		displayUntilValueComposite("Email", "Password");
		setValues(email, password);
		assertTrue(getOkCancel().isOkEnabled());
		getOkCancel().ok();
		displayUntilDataCompositeWithTitle("My Software Fm");
		checkLoggedInDisplay(crypto, softwareFmId);

		assertSame(userDataManager.getUserData(), mySoftwareFm.getUserData());
	}

	public void testRestartedWhenAnotherWindowLogsInOrOut() {
		signUp(email, moniker, password);
		assertEquals(email, mySoftwareFm.getUserData().email());
		UserData newUserData = new UserData(null, null, null);
		userDataManager.setUserData(this, newUserData);
		displayUntilValueComposite("Email", "Password");
	}

	public void testLoggedInWhenAnotherWindowLogsIn() {
		UserData newUserData = new UserData(email, "sfmId", password);
		userDataManager.setUserData(this, newUserData);
		displayUntilDataCompositeWithTitle("My Software Fm");
	}

	public void testSignup() {
		String signUpSalt = signUp(email, moniker, password);
		JdbcTemplate template = getTemplate();

		assertEquals(1, template.queryForInt("select count(*) from users"));
		assertEquals(email, template.queryForObject("select email from users", String.class));
		assertEquals(signUpSalt, template.queryForObject("select salt from users", String.class));
		String passwordHash = Crypto.digest(signUpSalt, password);
		assertEquals(passwordHash, template.queryForObject("select password from users", String.class));
		assertNull(passwordHash, template.queryForObject("select passwordResetKey from users", String.class));

		String crypto = template.queryForObject("select crypto from users", String.class);
		String softwareFmId = template.queryForObject("select softwarefmid from users", String.class);

		assertEquals(new UserData(email, softwareFmId, crypto), userDataManager.getUserData());

		assertTrue(userFile.exists());
		Map<String, Object> userData = Json.mapFromEncryptedFile(userFile, crypto);
		assertEquals(Maps.stringObjectMap(LoginConstants.emailKey, email, LoginConstants.monikerKey, moniker), userData);
	}

	public void testLoginErrors() {
		signUp(email, moniker, password);
		getTemplate().queryForObject("select crypto from users", String.class);
		mySoftwareFm.logout();

		checkLoginError(email, "password", "Email / Password didn't match\n\nClicking this panel will start login again");
		checkLoginError("a@b.com", "password", "Email not recognised\n\nClicking this panel will start login again");

		assertEquals(new UserData("a@b.com", null, null), userDataManager.getUserData());
	}

	public void testStartRequestsSalt() {
		assertNull(mySoftwareFm.getSignupSalt());
		startAndGetSignupSalt();
		assertEquals(0, getTemplate().queryForInt("select count(*) from users"));

		assertNull(userDataManager.getUserData().crypto);
		assertNull(userDataManager.getUserData().softwareFmId);
		assertNull(userDataManager.getUserData().email);
		assertNotNull(mySoftwareFm.getSignupSalt());
	}

	private void checkLoginError(String email, String password, String errorMessage) {
		mySoftwareFm.logout();
		mySoftwareFm.start();
		assertNull(userDataManager.getUserData().crypto);
		assertNull(userDataManager.getUserData().softwareFmId);
		displayUntilValueComposite("Email", "Password");
		setValues(email, password);
		assertTrue(getOkCancel().isOkEnabled());
		getOkCancel().ok();
		displayUntilCompositeWithCardMargin();

		assertEquals(new UserData(email, null, null), userDataManager.getUserData());

		assertEquals(errorMessage, getText());
	}

	private String getText() {
		Control firstChild = softwareFmComposite.getChildren()[0];
		CompositeWithCardMargin compositeWithCardMargin = (CompositeWithCardMargin) firstChild;
		StyledText text = Swts.<StyledText> getDescendant(compositeWithCardMargin, 1, 0, 0);
		return text.getText();
	}

	protected IOkCancelForTests displayUntilEmailPassword() {
		return (IOkCancelForTests) displayUntilValueComposite("Email", "Password").getFooter();
	}

	private String resetPassword(final String magicString) {
		final MemoryResponseCallback memoryCallback = IResponseCallback.Utils.memoryCallback();
		getLocalApi().makeContainer().access(IHttpClient.class, new ICallback<IHttpClient>() {
			@Override
			public void process(IHttpClient client) throws Exception {
				client.get(Urls.compose(LoginConstants.passwordResetLinkPrefix, magicString)).execute(memoryCallback).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
			}
		}).get();
		String html = memoryCallback.response.asString();
		int start = html.indexOf(": ") + 2;
		int end = html.indexOf("</html");
		String password = html.substring(start, end);
		return password;
	}

	private String signUp(String email, String moniker, String password) {
		String signUpSalt = startAndGetSignupSalt();
		displayUntilEmailPassword().pressButton(1);
		displayUntilValueComposite("Email", "Moniker", "Password", "Confirm Password");
		setValues(email, moniker, password, password);
		assertTrue(getOkCancel().isOkEnabled());
		getOkCancel().ok();
		displayUntilDataCompositeWithTitle("My Software Fm");

		return signUpSalt;
	}

	public void testSignupNeedsValidEmailMonikerAndPasswords() {
		startAndGetSignupSalt();
		displayUntilEmailPassword().pressButton(1);
		displayUntilValueComposite("Email", "Moniker", "Password", "Confirm Password");
		assertFalse(getOkCancel().isOkEnabled());

		setValues("a.b@c.com", "mon", "pass1", "passDifferent");
		assertFalse(getOkCancel().isOkEnabled());

		setValues("notlegal", "mon", "pass1", "pass1");
		assertFalse(getOkCancel().isOkEnabled());

		setValues("a.b@c.com", "", "pass1", "pass1");
		assertFalse(getOkCancel().isOkEnabled());

		setValues("a.b@c.com", "mon", "pass1", "pass1");
		assertTrue(getOkCancel().isOkEnabled());
	}

	public void testNeedsValidEmailAddressForLogin() {
		mySoftwareFm.start();
		displayUntilValueComposite("Email", "Password");
		setValues("email", "password");
		assertFalse(getOkCancel().isOkEnabled());

		setValues("email@a.b", "password");
		assertTrue(getOkCancel().isOkEnabled());
	}

	public void testInitialUserDataIsTakenFromLoginStrategy() {
		assertSame(userDataManager.getUserData(), mySoftwareFm.getUserData());
	}

	private void displayUntilCompositeWithCardMargin() {
		dispatchUntil(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				Control firstChild = softwareFmComposite.getChildren()[0];
				boolean result = firstChild instanceof CompositeWithCardMargin;
				return result;
			}
		});
	}

	private void displayUntilDataCompositeWithTitle(final String title) {
		dispatchUntil(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				Control firstChild = softwareFmComposite.getChildren()[0];
				if (firstChild instanceof DataComposite<?>) {
					String actualText = ((DataComposite<?>) firstChild).getTitle().getText();
					return title.equals(actualText);
				}
				return false;
			}
		});
	}

	IOkCancelForTests getOkCancel() {
		@SuppressWarnings("unchecked")
		IDataCompositeWithOkCancel<Composite> valueComposite = (IDataCompositeWithOkCancel<Composite>) softwareFmComposite.getChildren()[0];
		return (IOkCancelForTests) valueComposite.getFooter();

	}

	private void setValues(final String... values) {
		@SuppressWarnings("unchecked")
		IDataCompositeWithOkCancel<Composite> valueComposite = (IDataCompositeWithOkCancel<Composite>) softwareFmComposite.getChildren()[0];
		process(valueComposite, new IEditorCallback<Void>() {
			@Override
			public Void check(List<Label> labels, List<Control> editors) {
				for (int i = 0; i < values.length; i++)
					Swts.setText(editors.get(i), values[i]);
				return null;
			}
		});
	}

	private <T> T process(IDataCompositeWithOkCancel<Composite> valueComposite, IEditorCallback<T> callback) {
		Composite editor = valueComposite.getEditor();
		Control[] children = editor.getChildren();
		List<Label> labelList = Lists.newList();
		List<Control> editorList = Lists.newList();
		int i = 0;
		while (i < children.length) {
			labelList.add((Label) children[i++]);
			editorList.add(children[i++]);
		}
		return callback.check(labelList, editorList);

	}

	static interface IEditorCallback<T> {
		T check(List<Label> labels, List<Control> editors);

	}

	private IDataCompositeWithOkCancel<Composite> displayUntilValueComposite(final String... expectedLabels) {
		Callable<Boolean> childIsValueComposite = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				Control[] children = softwareFmComposite.getChildren();
				if (children.length > 0)
					if (children[0] instanceof IDataCompositeWithOkCancel<?>) {
						@SuppressWarnings("unchecked")
						IDataCompositeWithOkCancel<Composite> valueComposite = (IDataCompositeWithOkCancel<Composite>) softwareFmComposite.getChildren()[0];
						return process(valueComposite, new IEditorCallback<Boolean>() {
							@Override
							public Boolean check(List<Label> labels, List<Control> editors) {
								if (expectedLabels.length == labels.size()) {
									for (int i = 0; i < labels.size(); i++) {
										Label label = labels.get(i);
										if (!expectedLabels[i].equals(label.getText()))
											return false;
									}
									return true;
								}
								return false;
							}
						});
					}
				return false;
			}
		};
		dispatchUntil(childIsValueComposite);
		@SuppressWarnings("unchecked")
		IDataCompositeWithOkCancel<Composite> valueComposite = (IDataCompositeWithOkCancel<Composite>) softwareFmComposite.getChildren()[0];
		return valueComposite;
	}

	private String startAndGetSignupSalt() {
		mySoftwareFm.start();
		dispatchUntil(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return mySoftwareFm.getSignupSalt() != null;
			}
		});
		return mySoftwareFm.getSignupSalt();
	}

	@SuppressWarnings("unchecked")
	protected void checkLoggedInDisplay(String crypto, String softwareFmId) {
		IDataComposite<Composite> mySoftwareFmComposite = (IDataComposite<Composite>) mySoftwareFm.getComposite().getChildren()[0];
		Control[] children = mySoftwareFmComposite.getEditor().getChildren();
		Table table = (Table) children[0];
		Swts.checkRow(table, 0, "Email", email);
		Swts.checkRow(table, 1, "Moniker", moniker);
		Swts.checkRow(table, 2, "Software Fm Id", userDataManager.getUserData().softwareFmId);

		assertEquals(3, children.length);
		assertTrue(children[1] instanceof Button);
		assertTrue(children[2] instanceof Button);

		assertEquals(new UserData(email, softwareFmId, crypto), userDataManager.getUserData());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		getServerApi().getServer();
		ILoginStrategy loginStrategy = ILoginStrategy.Utils.softwareFmLoginStrategy(display, getLocalApi().makeContainer(), CommonConstants.testTimeOutMs);
		cardConfig = ICardConfigurator.Utils.cardConfigForTests(display);
		userDataManager = IUserDataManager.Utils.userDataManager();
		mySoftwareFm = new MySoftwareFm(shell, getLocalApi().makeUserAndGroupsContainer(), cardConfig, loginStrategy, IShowMyData.Utils.exceptionShowMyData(), IShowMyGroups.Utils.exceptionShowMyGroups(), userDataManager);
		softwareFmComposite = mySoftwareFm.getComposite();
		userFile = new File(remoteRoot, Urls.compose(getUrlPrefix(), "users/so/me/someNewSoftwareFmId0", CommonConstants.dataFileName));
		truncateUsersTable();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		cardConfig.dispose();
		if (root.exists())
			assertTrue(Files.deleteDirectory(root));
	}
}