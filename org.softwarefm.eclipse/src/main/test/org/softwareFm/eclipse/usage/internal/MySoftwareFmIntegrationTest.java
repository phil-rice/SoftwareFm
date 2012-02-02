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
import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.client.http.requests.MemoryResponseCallback;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.collections.Files;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.tests.IIntegrationTest;
import org.softwareFm.common.tests.Tests;
import org.softwareFm.common.url.Urls;
import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.swt.card.composites.CompositeWithCardMargin;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.editors.IValueComposite;
import org.softwareFm.swt.explorer.internal.MySoftwareFm;
import org.softwareFm.swt.explorer.internal.UserData;
import org.softwareFm.swt.mySoftwareFm.ILoginStrategy;
import org.softwareFm.swt.okCancel.OkCancel;
import org.softwareFm.swt.swt.SwtAndServiceTest;
import org.softwareFm.swt.swt.Swts;
import org.springframework.jdbc.core.JdbcTemplate;

public class MySoftwareFmIntegrationTest extends SwtAndServiceTest implements IIntegrationTest {

	private IHttpClient client;
	private MySoftwareFm mySoftwareFm;
	private BasicDataSource dataSource;
	private JdbcTemplate template;
	private Composite softwareFmComposite;
	private ISoftwareFmServer server;

	private final String email = "a.b@c.com";
	private final String password = "pass1";
	private CardConfig cardConfig;
	private String key;
	private File root;
	private File userFile;
	private File remoteRoot;

	public void testSignupThenLogin() {
		signUp(email, password);
		String crypto = template.queryForObject("select crypto from users", String.class);
		String softwareFmId = template.queryForObject("select softwarefmid from users", String.class);
		mySoftwareFm.logout();

		mySoftwareFm.start();
		assertNull(mySoftwareFm.userData.crypto);
		displayUntilValueComposite("Email", "Password");
		setValues(email, password);
		assertTrue(getOkCancel().isOkEnabled());
		getOkCancel().ok();
		displayUntilText();

		assertEquals(new UserData(email, softwareFmId, crypto), mySoftwareFm.userData);
	}

	public void testSignup() {
		String signUpSalt = signUp(email, password);

		assertEquals(1, template.queryForInt("select count(*) from users"));
		assertEquals(email, template.queryForObject("select email from users", String.class));
		assertEquals(signUpSalt, template.queryForObject("select salt from users", String.class));
		String passwordHash = Crypto.digest(signUpSalt, password);
		assertEquals(passwordHash, template.queryForObject("select password from users", String.class));
		assertNull(passwordHash, template.queryForObject("select passwordResetKey from users", String.class));

		String crypto = template.queryForObject("select crypto from users", String.class);
		String softwareFmId = template.queryForObject("select softwarefmid from users", String.class);

		assertEquals(new UserData(email, softwareFmId, crypto), mySoftwareFm.userData);

		assertTrue(userFile.exists());
		Json.mapFromEncryptedFile(userFile, crypto);
	}

	public void testLoginErrors() {
		signUp(email, password);
		template.queryForObject("select crypto from users", String.class);
		mySoftwareFm.logout();

		checkLoginError(email, "password", "Email / Password didn't match\n\nClicking this panel will start login again");
		checkLoginError("a@b.com", "password", "Email not recognised\n\nClicking this panel will start login again");

		assertEquals(new UserData("a@b.com", null, null), mySoftwareFm.userData);
	}

	public void testStartRequestsSalt() {
		assertNull(mySoftwareFm.getSignupSalt());
		startAndGetSignupSalt();
		assertEquals(0, template.queryForInt("select count(*) from users"));

		assertNull(mySoftwareFm.userData.crypto);
		assertNull(mySoftwareFm.userData.softwareFmId);
		assertNull(mySoftwareFm.userData.email);
		assertNotNull(mySoftwareFm.getSignupSalt());
	}

	private void checkLoginError(String email, String password, String errorMessage) {
		mySoftwareFm.logout();
		mySoftwareFm.start();
		assertNull(mySoftwareFm.userData.crypto);
		assertNull(mySoftwareFm.userData.softwareFmId);
		displayUntilValueComposite("Email", "Password");
		setValues(email, password);
		assertTrue(getOkCancel().isOkEnabled());
		getOkCancel().ok();
		displayUntilText();

		assertEquals(new UserData(email, null, null), mySoftwareFm.userData);

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
		signUp(email, password);
		String crypto = template.queryForObject("select crypto from users", String.class);

		mySoftwareFm.logout();
		mySoftwareFm.start();
		displayUntilValueComposite("Email", "Password").getOkCancel().pressButton(0);
		displayUntilValueComposite("Email");
		setValues(email);
		assertTrue(getOkCancel().isOkEnabled());
		getOkCancel().ok();
		displayUntilText();
		assertEquals("Reminder email sent to " + email + "\n\nClicking this panel will start login again", getText());

		assertEquals(new UserData(email, null, null), mySoftwareFm.userData);

		mySoftwareFm.start();
		displayUntilValueComposite("Email", "Password");

		String magicString = template.queryForObject("select passwordResetKey from users", String.class);
		String newPassword = resetPassword(magicString);
		System.out.println("password: " + newPassword);
		setValues(email, newPassword);
		assertTrue(getOkCancel().isOkEnabled());
		getOkCancel().ok();
		displayUntilText();

		assertEquals("Welcome to softwareFm.\n\n\nYou are logged in as " + email, getText());

		assertEquals(new UserData(email, "someNewSoftwareFmId0", crypto), mySoftwareFm.userData);
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

	private String signUp(String email, String password) {
		String signUpSalt = startAndGetSignupSalt();
		displayUntilValueComposite("Email", "Password").getOkCancel().pressButton(1);
		displayUntilValueComposite("Email", "Password", "Confirm Password");
		setValues(email, password, password);
		assertTrue(getOkCancel().isOkEnabled());
		getOkCancel().ok();
		displayUntilText();
		assertEquals("Welcome to softwareFm.\n\n\nYou are logged in as " + email, getText());
		return signUpSalt;
	}

	public void testSignupNeedsValidEmailAndPasswords() {
		startAndGetSignupSalt();
		displayUntilValueComposite("Email", "Password").getOkCancel().pressButton(1);
		displayUntilValueComposite("Email", "Password", "Confirm Password");
		assertFalse(getOkCancel().isOkEnabled());

		setValues("a.b@c.com", "pass1", "passDifferent");
		assertFalse(getOkCancel().isOkEnabled());

		setValues("notlegal", "pass1", "pass1");
		assertFalse(getOkCancel().isOkEnabled());

		setValues("a.b@c.com", "pass1", "pass1");
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

	private void displayUntilText() {
		dispatchUntil(display, CommonConstants.testTimeOutMs, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				Control firstChild = softwareFmComposite.getChildren()[0];
				boolean result = firstChild instanceof CompositeWithCardMargin;
				return result;
			}
		});
	}

	OkCancel getOkCancel() {
		@SuppressWarnings("unchecked")
		IValueComposite<Composite> valueComposite = (IValueComposite<Composite>) softwareFmComposite.getChildren()[0];
		return valueComposite.getOkCancel();

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

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		root = Tests.makeTempDirectory(getClass().getSimpleName());
		remoteRoot = new File(root, "remote");
		dataSource = AbstractLoginDataAccessor.defaultDataSource();
		Callable<String> softwareFmIdGenerator = Callables.patternWithCount("someNewSoftwareFmId{0}");
		key = Crypto.makeKey();
		IFunction1<Map<String, Object>, String> cryptoFn = Functions.constant(key);
		Callable<String> cryptoGenerator = Callables.value(key);
		// IRepoFinder repoFinder = IRepoFinder.Utils.forTests(remoteRoot);
		IGitOperations remoteOperations = IGitOperations.Utils.gitOperations(remoteRoot);
		// IGitOperations remoteOperations = IGitOperations.Utils.gitOperations(remoteRoot);
		// IGitLocal gitLocal = IGitLocal.Utils.localReader(repoFinder, localOperations, new GitWriterForTests(remoteOperations), remoteRoot.getAbsolutePath(), CommonConstants.staleCachePeriodForTest);
		server = ISoftwareFmServer.Utils.testServerPort(IProcessCall.Utils.softwareFmProcessCallWithoutMail(dataSource, remoteOperations, cryptoFn, cryptoGenerator, null, softwareFmIdGenerator), ICallback.Utils.rethrow());
		client = IHttpClient.Utils.builder("localhost", 8080);
		ILoginStrategy loginStrategy = ILoginStrategy.Utils.softwareFmLoginStrategy(display, service, client);
		cardConfig = ICardConfigurator.Utils.cardConfigForTests(display);
		mySoftwareFm = new MySoftwareFm(shell, cardConfig, loginStrategy);
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