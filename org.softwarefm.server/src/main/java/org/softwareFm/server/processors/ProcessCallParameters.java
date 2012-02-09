package org.softwareFm.server.processors;

import java.io.File;
import java.util.concurrent.Callable;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IUser;
import org.softwareFm.common.maps.UrlCache;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.server.processors.internal.EmailSaltRequester;
import org.softwareFm.server.processors.internal.ForgottonPasswordMailer;
import org.softwareFm.server.processors.internal.LoginChecker;
import org.softwareFm.server.processors.internal.MagicStringForPassword;
import org.softwareFm.server.processors.internal.PasswordResetter;
import org.softwareFm.server.processors.internal.SaltProcessor;
import org.softwareFm.server.processors.internal.SignUpChecker;

public class ProcessCallParameters {
	public final BasicDataSource dataSource;
	public final IGitOperations gitOperations;
	public final UrlCache<String> aboveRepostoryUrlCache;
	public final SaltProcessor saltProcessor;
	public final LoginChecker loginChecker;
	public final IUser user;
	public final SignUpChecker signUpChecker;
	public final IMagicStringForPassword magicStringForPassword;
	public final ForgottonPasswordMailer forgottonPasswordProcessor;
	public final IPasswordResetter resetter;
	public final EmailSaltRequester saltRequester;
	public final IPasswordChanger passwordChanger;
	public final File root;
	public final Callable<String> softwareFmIdGenerator;
	public final Callable<String> saltGenerator;

	public ProcessCallParameters(BasicDataSource dataSource, IGitOperations gitOperations) {
		this(dataSource, gitOperations, Callables.makeCryptoKey(), Callables.uuidGenerator(), IMailer.Utils.email("localhost", null, null));
	}

	public ProcessCallParameters(BasicDataSource dataSource, IGitOperations gitOperations, Callable<String> cryptoGenerator, Callable<String> softwareFmIdGenerator, IMailer mailer) {
		this.dataSource = dataSource;
		this.gitOperations = gitOperations;
		this.softwareFmIdGenerator = softwareFmIdGenerator;
		this.aboveRepostoryUrlCache = new UrlCache<String>();
		this.saltProcessor = new SaltProcessor();
		this.loginChecker = new LoginChecker(dataSource);
		this.user = IProcessCall.Utils.makeUser(gitOperations);
		this.signUpChecker = new SignUpChecker(dataSource, cryptoGenerator, user);
		this.magicStringForPassword = new MagicStringForPassword(dataSource, Callables.uuidGenerator());
		this.forgottonPasswordProcessor = new ForgottonPasswordMailer(mailer, magicStringForPassword);
		this.resetter = new PasswordResetter(dataSource);
		this.saltRequester = new EmailSaltRequester(dataSource);
		this.passwordChanger = IPasswordChanger.Utils.databasePasswordChanger(dataSource);
		this.root = gitOperations.getRoot();
		this.saltGenerator = Callables.uuidGenerator();
	}
}