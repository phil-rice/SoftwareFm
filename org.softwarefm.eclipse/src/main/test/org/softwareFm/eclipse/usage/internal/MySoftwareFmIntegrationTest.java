/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.usage.internal;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.dbcp.BasicDataSource;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.client.http.requests.MemoryResponseCallback;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.collections.Files;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.tests.IIntegrationTest;
import org.softwareFm.common.tests.Tests;
import org.softwareFm.common.url.Urls;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.server.ICrowdSourcedServer;
import org.softwareFm.server.processors.IMailer;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.ProcessCallParameters;
import org.softwareFm.softwareFmServer.SoftwareFmServer;
import org.softwareFm.swt.card.composites.CompositeWithCardMargin;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.editors.IValueComposite;
import org.softwareFm.swt.explorer.IShowMyData;
import org.softwareFm.swt.explorer.IShowMyGroups;
import org.softwareFm.swt.explorer.IUserDataManager;
import org.softwareFm.swt.explorer.internal.MySoftwareFm;
import org.softwareFm.swt.explorer.internal.UserData;
import org.softwareFm.swt.mySoftwareFm.ILoginStrategy;
import org.softwareFm.swt.okCancel.IOkCancelForTests;
import org.softwareFm.swt.swt.SwtAndServiceTest;
import org.softwareFm.swt.swt.Swts;
import org.springframework.jdbc.core.JdbcTemplate;

public class MySoftwareFmIntegrationTest extends SwtAndServiceTest implements IIntegrationTest {

	private IHttpClient client;
	private MySoftwareFm mySoftwareFm;
	private BasicDataSource dataSource;
	private JdbcTemplate template;
	private Composite softwareFmComposite;
	private ICrowdSourcedServer server;

	private final String email = "a.b@c.com";
	private final String password = "pass1";
	private final String moniker = "moniker";
	private CardConfig cardConfig;
	private String key;
	private File root;
	private File userFile;
	private File remoteRoot;
	private IUserDataManager userDataManager;

	public void testSignupThenLogin() {
		assertSame(userDataManager.getUserData(), mySoftwareFm.getUserData());

		signUp(email, moniker, password);
		String crypto = template.queryForObject("select crypto from users", String.class);
		String softwareFmId = template.queryForObject("select softwarefmid from users", String.class);
		mySoftwareFm.logout();

		mySoftwareFm.start();
		assertNull(userDataManager.getUserData().crypto);
		displayUntilValueComposite("Email", "Password");
		setValues(email, password);
		assertTrue(getOkCancel().isOkEnabled());
		getOkCancel().ok();
		displayUntilCompositeWithCardMargin();
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
		displayUntilCompositeWithCardMargin();
	}

	public void testSignup() {
		String signUpSalt = signUp(email, moniker, password);

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
		template.queryForObject("select crypto from users", String.class);
		mySoftwareFm.logout();

		checkLoginError(email, "password", "Email / Password didn't match\n\nClicking this panel will start login again");
		checkLoginError("a@b.com", "password", "Email not recognised\n\nClicking this panel will start login again");

		assertEquals(new UserData("a@b.com", null, null), userDataManager.getUserData());
	}

	public void testStartRequestsSalt() {
		assertNull(mySoftwareFm.getSignupSalt());
		startAndGetSignupSalt();
		assertEquals(0, template.queryForInt("select count(*) from users"));

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
		Swts.layoutDump(compositeWithCardMargin);
		StyledText text = (StyledText) Swts.getDescendant(compositeWithCardMargin, 1, 0, 0);
		return text.getText();
	}

	public void testForgotPassword() throws Exception {
		signUp(email, moniker, password);
		String crypto = template.queryForObject("select crypto from users", String.class);

		mySoftwareFm.logout();
		mySoftwareFm.start();
		displayUntilEmailPassword().pressButton(0);
		displayUntilValueComposite("Email");
		setValues(email);
		assertTrue(getOkCancel().isOkEnabled());
		getOkCancel().ok();
		displayUntilCompositeWithCardMargin();
		assertEquals("Reminder email sent to " + email + "\n\nClicking this panel will start login again", getText());

		assertEquals(new UserData(email, null, null), userDataManager.getUserData());

		mySoftwareFm.start();
		displayUntilValueComposite("Email", "Password");

		String magicString = template.queryForObject("select passwordResetKey from users", String.class);
		String newPassword = resetPassword(magicString);
		System.out.println("password: " + newPassword);
		setValues(email, newPassword);
		assertTrue(getOkCancel().isOkEnabled());
		getOkCancel().ok();
		displayUntilCompositeWithCardMargin();
		checkLoggedInDisplay(crypto, "someNewSoftwareFmId0");

		assertEquals(new UserData(email, "someNewSoftwareFmId0", crypto), userDataManager.getUserData());
	}

	protected IOkCancelForTests displayUntilEmailPassword() {
		return (IOkCancelForTests) displayUntilValueComposite("Email", "Password").getOkCancel();
	}

	private String resetPassword(String magicString) throws InterruptedException, ExecutionException, TimeoutException {
		MemoryResponseCallback memoryCallback = IResponseCallback.Utils.memoryCallback();
		client.get(Urls.compose(LoginConstants.passwordResetLinkPrefix, magicString)).execute(memoryCallback).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
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
		displayUntilCompositeWithCardMargin();

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
		dispatchUntil(display, CommonConstants.testTimeOutMs, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				Control firstChild = softwareFmComposite.getChildren()[0];
				boolean result = firstChild instanceof CompositeWithCardMargin;
				return result;
			}
		});
	}

	IOkCancelForTests getOkCancel() {
		@SuppressWarnings("unchecked")
		IValueComposite<Composite> valueComposite = (IValueComposite<Composite>) softwareFmComposite.getChildren()[0];
		return (IOkCancelForTests) valueComposite.getOkCancel();

	}

	private void setValues(final String... values) {
		@SuppressWarnings("unchecked")
		IValueComposite<Composite> valueComposite = (IValueComposite<Composite>) softwareFmComposite.getChildren()[0];
		process(valueComposite, new IEditorCallback<Void>() {
			@Override
			public Void check(List<Label> labels, List<Control> editors) {
				for (int i = 0; i < values.length; i++)
					Swts.setText(editors.get(i), values[i]);
				return null;
			}
		});
	}

	private <T> T process(IValueComposite<Composite> valueComposite, IEditorCallback<T> callback) {
		Composite editor = valueComposite.getEditor();
		Control[] children = editor.getChildren();
		assertEquals(2, children.length);
		Composite labels = (Composite) children[0];
		Composite editors = (Composite) children[1];
		List<Label> labelList = Lists.newList();
		List<Control> editorList = Lists.newList();
		for (int i = 0; i < labels.getChildren().length; i++) {
			labelList.add((Label) labels.getChildren()[i]);
			editorList.add(editors.getChildren()[i]);
		}
		if (labels.getChildren().length != editors.getChildren().length)
			assertEquals(labels.getChildren().length, editors.getChildren().length);
		return callback.check(labelList, editorList);

	}

	static interface IEditorCallback<T> {
		T check(List<Label> labels, List<Control> editors);

	}

	private IValueComposite<Composite> displayUntilValueComposite(final String... expectedLabels) {
		Callable<Boolean> childIsValueComposite = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				Control[] children = softwareFmComposite.getChildren();
				if (children.length > 0)
					if (children[0] instanceof IValueComposite<?>) {
						@SuppressWarnings("unchecked")
						IValueComposite<Composite> valueComposite = (IValueComposite<Composite>) softwareFmComposite.getChildren()[0];
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
		dispatchUntil(display, CommonConstants.testTimeOutMs, childIsValueComposite);
		@SuppressWarnings("unchecked")
		IValueComposite<Composite> valueComposite = (IValueComposite<Composite>) softwareFmComposite.getChildren()[0];
		return valueComposite;
	}

	private String startAndGetSignupSalt() {
		mySoftwareFm.start();
		dispatchUntil(display, CommonConstants.testTimeOutMs, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return mySoftwareFm.getSignupSalt() != null;
			}
		});
		return mySoftwareFm.getSignupSalt();
	}

	protected void checkLoggedInDisplay(String crypto, String softwareFmId) {
		Table table = (Table) Swts.getDescendant(mySoftwareFm.getControl(), 0, 0, 0);
		Swts.checkRow(table, 0, "Email", email);
		Swts.checkRow(table, 1, "Moniker", moniker);
		Swts.checkRow(table, 2, "Software Fm Id", userDataManager.getUserData().softwareFmId);

		assertEquals(new UserData(email, softwareFmId, crypto), userDataManager.getUserData());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		root = Tests.makeTempDirectory(getClass().getSimpleName());
		remoteRoot = new File(root, "remote");
		dataSource = AbstractLoginDataAccessor.defaultDataSource();
		Callable<String> softwareFmIdGenerator = Callables.patternWithCount("someNewSoftwareFmId{0}");
		key = Crypto.makeKey();
		Callable<String> cryptoGenerator = Callables.value(key);
		IGitOperations remoteOperations = IGitOperations.Utils.gitOperations(remoteRoot);
		IFunction1<Map<String, Object>, String> userCryptoFn = ICrowdSourcedServer.Utils.cryptoFn(dataSource);
		Map<String, Callable<Object>> defaultValues = SoftwareFmServer.makeDefaultProperties();
		ProcessCallParameters processCallParameters = new ProcessCallParameters(dataSource, remoteOperations, cryptoGenerator, softwareFmIdGenerator, userCryptoFn, IMailer.Utils.noMailer(), defaultValues, SoftwareFmConstants.urlPrefix);
		IProcessCall softwareFmProcessCall = IProcessCall.Utils.softwareFmProcessCall(processCallParameters, Functions.<ProcessCallParameters, IProcessCall[]> constant(new IProcessCall[0]));
		server = ICrowdSourcedServer.Utils.testServerPort(softwareFmProcessCall, ICallback.Utils.rethrow());
		client = IHttpClient.Utils.builder("localhost", 8080);
		ILoginStrategy loginStrategy = ILoginStrategy.Utils.softwareFmLoginStrategy(display, service, client);
		cardConfig = ICardConfigurator.Utils.cardConfigForTests(display);
		IUserReader userReader = IUserReader.Utils.mockReader(LoginConstants.emailKey, email, LoginConstants.monikerKey, moniker);
		userDataManager = IUserDataManager.Utils.userDataManager();
		mySoftwareFm = new MySoftwareFm(shell, cardConfig, loginStrategy, IShowMyData.Utils.exceptionShowMyData(), IShowMyGroups.Utils.exceptionShowMyGroups(), userReader, userDataManager);
		template = new JdbcTemplate(dataSource);
		template.update("truncate users");
		softwareFmComposite = mySoftwareFm.getComposite();

		userFile = new File(remoteRoot, Urls.compose("softwareFm/users/so/me/someNewSoftwareFmId0", CommonConstants.dataFileName));
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		server.shutdown();
		client.shutdown();
		dataSource.close();
		cardConfig.dispose();
		if (root.exists())
			assertTrue(Files.deleteDirectory(root));
	}
}