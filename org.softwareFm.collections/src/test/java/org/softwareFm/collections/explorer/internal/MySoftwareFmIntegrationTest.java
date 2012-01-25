package org.softwareFm.collections.explorer.internal;

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
import org.softwareFm.card.card.composites.CompositeWithCardMargin;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.configuration.ICardConfigurator;
import org.softwareFm.card.editors.IValueComposite;
import org.softwareFm.collections.mySoftwareFm.ILoginStrategy;
import org.softwareFm.display.okCancel.OkCancel;
import org.softwareFm.display.swt.SwtAndServiceTest;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.requests.MemoryResponseCallback;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.AbstractLoginDataAccessor;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.crypto.Crypto;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.runnable.Callables;
import org.softwareFm.utilities.tests.IIntegrationTest;
import org.softwareFm.utilities.tests.Tests;
import org.softwareFm.utilities.url.Urls;
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
		assertNotNull(crypto);
		assertEquals(crypto, mySoftwareFm.crypto);
		assertEquals(softwareFmId, mySoftwareFm.softwareFmId);
		assertEquals(email, mySoftwareFm.email);
		
		assertTrue(userFile.exists());
		Json.mapFromEncryptedFile(userFile, crypto);
	}

	public void testSignupThenLogin() {
		signUp(email, password);
		String crypto = template.queryForObject("select crypto from users", String.class);
		String softwareFmId = template.queryForObject("select softwarefmid from users", String.class);
		mySoftwareFm.logout();

		mySoftwareFm.start();
		assertNull(mySoftwareFm.crypto);
		displayUntilValueComposite("Email", "Password");
		setValues(email, password);
		assertTrue(getOkCancel().isOkEnabled());
		getOkCancel().ok();
		displayUntilText();
		assertEquals(crypto, mySoftwareFm.crypto);
		assertEquals(email, mySoftwareFm.email);
		assertEquals(softwareFmId, mySoftwareFm.softwareFmId);
	}

	public void testStartRequestsSalt() {
		assertNull(mySoftwareFm.getSignupSalt());
		startAndGetSignupSalt();
		assertEquals(0, template.queryForInt("select count(*) from users"));

		assertNull(mySoftwareFm.crypto);
		assertNull(mySoftwareFm.softwareFmId);
		assertNull(mySoftwareFm.email);
		assertNotNull(mySoftwareFm.getSignupSalt());
	}

	public void testLoginErrors() {
		signUp(email, password);
		template.queryForObject("select crypto from users", String.class);
		mySoftwareFm.logout();

		checkLoginError(email, "password", "Email / Password didn't match\n\nClicking this panel will start login again");
		checkLoginError("a@b.com", "password", "Email not recognised\n\nClicking this panel will start login again");
		assertNull(mySoftwareFm.crypto);
		assertNull(mySoftwareFm.softwareFmId);
	}

	private void checkLoginError(String email, String password, String errorMessage) {
		mySoftwareFm.logout();
		mySoftwareFm.start();
		assertNull(mySoftwareFm.crypto);
		assertNull(mySoftwareFm.softwareFmId);
		displayUntilValueComposite("Email", "Password");
		setValues(email, password);
		assertTrue(getOkCancel().isOkEnabled());
		getOkCancel().ok();
		displayUntilText();
		assertNull(mySoftwareFm.crypto);
		assertNull(mySoftwareFm.softwareFmId);
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
		assertNull(mySoftwareFm.crypto);
		assertNull(mySoftwareFm.email);
		assertNull(mySoftwareFm.softwareFmId);

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
		assertEquals(crypto, mySoftwareFm.crypto);
		assertEquals("someNewSoftwareFmId0", mySoftwareFm.softwareFmId);

		assertEquals(email, mySoftwareFm.email);
	}

	private String resetPassword(String magicString) throws InterruptedException, ExecutionException, TimeoutException {
		MemoryResponseCallback memoryCallback = IResponseCallback.Utils.memoryCallback();
		client.get(Urls.compose(ServerConstants.passwordResetLinkPrefix, magicString)).execute(memoryCallback).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
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
		dispatchUntil(display, ServerConstants.clientTimeOut * 1000, new Callable<Boolean>() {
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
		dispatchUntil(display, ServerConstants.clientTimeOut * 1000, childIsValueComposite);
		@SuppressWarnings("unchecked")
		IValueComposite<Composite> valueComposite = (IValueComposite<Composite>) softwareFmComposite.getChildren()[0];
		return valueComposite;
	}

	private String startAndGetSignupSalt() {
		mySoftwareFm.start();
		dispatchUntil(display, ServerConstants.clientTimeOut * 1000, new Callable<Boolean>() {
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
		dataSource = AbstractLoginDataAccessor.defaultDataSource();
		Callable<String> monthGetter = Callables.value("someMonth");
		Callable<Integer> dayGetter = Callables.value(3);
		Callable<String> softwareFmIdGenerator = Callables.patternWithCount("someNewSoftwareFmId{0}");
		key = Crypto.makeKey();
		IFunction1<Map<String, Object>, String> cryptoFn = Functions.constant(key);
		Callable<String> cryptoGenerator = Callables.value(key);
		root = Tests.makeTempDirectory(getClass().getSimpleName());
		IGitServer gitServer = IGitServer.Utils.gitServer(root, "not used");
		server = ISoftwareFmServer.Utils.testServerPort(IProcessCall.Utils.softwareFmProcessCallWithoutMail(dataSource, gitServer, cryptoFn, cryptoGenerator, null, monthGetter, dayGetter, softwareFmIdGenerator), ICallback.Utils.rethrow());
		client = IHttpClient.Utils.builder("localhost", 8080);
		ILoginStrategy loginStrategy = ILoginStrategy.Utils.softwareFmLoginStrategy(display, service, client);
		cardConfig = ICardConfigurator.Utils.cardConfigForTests(display);
		mySoftwareFm = new MySoftwareFm(shell, cardConfig, loginStrategy);
		template = new JdbcTemplate(dataSource);
		template.update("truncate users");
		softwareFmComposite = mySoftwareFm.getComposite();

		userFile = new File(root, Urls.compose("users/so/me/someNewSoftwareFmId0", ServerConstants.dataFileName));
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
