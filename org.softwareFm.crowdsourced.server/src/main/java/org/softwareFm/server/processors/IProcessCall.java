package org.softwareFm.server.processors;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.sql.DataSource;

import org.apache.http.RequestLine;
import org.softwareFm.server.IGitOperations;
import org.softwareFm.server.IUser;
import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.server.processors.internal.ChangePasswordProcessor;
import org.softwareFm.server.processors.internal.DeleteProcessor;
import org.softwareFm.server.processors.internal.EmailSaltRequester;
import org.softwareFm.server.processors.internal.FavIconProcessor;
import org.softwareFm.server.processors.internal.ForgottonPasswordMailer;
import org.softwareFm.server.processors.internal.ForgottonPasswordProcessor;
import org.softwareFm.server.processors.internal.ForgottonPasswordWebPageProcessor;
import org.softwareFm.server.processors.internal.GetFileProcessor;
import org.softwareFm.server.processors.internal.GetIndexProcessor;
import org.softwareFm.server.processors.internal.GitGetProcessor;
import org.softwareFm.server.processors.internal.HeadThrowing404;
import org.softwareFm.server.processors.internal.LoginChecker;
import org.softwareFm.server.processors.internal.LoginProcessor;
import org.softwareFm.server.processors.internal.MakeRootProcessor;
import org.softwareFm.server.processors.internal.MakeSaltForLoginProcessor;
import org.softwareFm.server.processors.internal.PasswordResetter;
import org.softwareFm.server.processors.internal.PostProcessor;
import org.softwareFm.server.processors.internal.RequestEmailSaltProcessor;
import org.softwareFm.server.processors.internal.RigidFileProcessor;
import org.softwareFm.server.processors.internal.SaltProcessor;
import org.softwareFm.server.processors.internal.SignUpChecker;
import org.softwareFm.server.processors.internal.SignupProcessor;
import org.softwareFm.utilities.arrays.ArrayHelper;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.UrlCache;
import org.softwareFm.utilities.strings.Strings;

public interface IProcessCall {
	IProcessResult process(RequestLine requestLine, Map<String, Object> parameters);

	public static class Utils {

		public static IProcessCall chain(final IProcessCall... processors) {
			return new IProcessCall() {
				@Override
				public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
					for (IProcessCall processor : processors) {
						IProcessResult result = processor.process(requestLine, parameters);
						if (result != null)
							return result;
					}
					throw new IllegalArgumentException(Utils.toString(requestLine, parameters));
				}
			};
		}

		public static IProcessCall toStringProcessCall() {
			return new IProcessCall() {
				@Override
				public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
					return IProcessResult.Utils.processString(Utils.toString(requestLine, parameters));
				}

			};
		}

		private static String toString(RequestLine requestLine, Map<String, Object> parameters) {
			return "<" + requestLine + ", " + parameters + ">";
		}

		public static IProcessCall softwareFmProcessCallWithoutMail(DataSource dataSource, IGitOperations gitOperations, IFunction1<Map<String, Object>, String> cryptoFn, Callable<String> cryptoGenerator, File fileRoot, Callable<String> monthGetter, Callable<Integer> dayGetter, Callable<String> softwareFmIdGenerator) {
			return softwareFmProcessCall(dataSource, gitOperations, cryptoFn, cryptoGenerator, fileRoot, IMailer.Utils.noMailer(), softwareFmIdGenerator);
		}

		public static IProcessCall softwareFmProcessCall(DataSource dataSource, IGitOperations gitOperations, IFunction1<Map<String, Object>, String> cryptoFn, Callable<String> cryptoGenerator, File fileRoot, IMailer mailer, Callable<String> softwareFmIdGenerator, IProcessCall... extraProcessCalls) {
			UrlCache<String> aboveRepostoryUrlCache = new UrlCache<String>();
			SaltProcessor saltProcessor = new SaltProcessor();
			LoginChecker loginChecker = new LoginChecker(dataSource);
			SignUpChecker signUpChecker = new SignUpChecker(dataSource, cryptoGenerator);
			ForgottonPasswordMailer forgottonPasswordProcessor = new ForgottonPasswordMailer(dataSource, mailer);
			IPasswordResetter resetter = new PasswordResetter(dataSource);
			EmailSaltRequester saltRequester = new EmailSaltRequester(dataSource);
			IPasswordChanger passwordChanger = IPasswordChanger.Utils.databasePasswordChanger(dataSource);
			IUser user = makeUser(gitOperations);
			IProcessCall[] rawProcessCalls = new IProcessCall[] { new FavIconProcessor(),//
					new RigidFileProcessor(fileRoot, "update"),// responds to get or head
					new LoginProcessor(saltProcessor, loginChecker), //
					new SignupProcessor(signUpChecker, saltProcessor, softwareFmIdGenerator, user), //
					// usageProcessor,//
					new MakeSaltForLoginProcessor(saltProcessor),//
					new ForgottonPasswordProcessor(saltProcessor, forgottonPasswordProcessor),//
					new RequestEmailSaltProcessor(saltRequester),//
					new ForgottonPasswordWebPageProcessor(resetter),//
					new ChangePasswordProcessor(passwordChanger),//
					new HeadThrowing404(),// sweep up any heads
					new GetIndexProcessor(fileRoot),//
					new GetFileProcessor(fileRoot, "html", "jpg", "png", "css", "gif", "jar", "xml"), //
					new GitGetProcessor(gitOperations, aboveRepostoryUrlCache), //
					new MakeRootProcessor(aboveRepostoryUrlCache, gitOperations), //
					new DeleteProcessor(gitOperations),//
					new PostProcessor(gitOperations) };
			IProcessCall[] processCalls = ArrayHelper.insert(rawProcessCalls, extraProcessCalls);
			return chain(processCalls);// this sweeps up any posts, so ensure that commands appear in chain before it
		}

		public static IUser makeUser(IGitOperations gitOperations) {
			IUser user = IUser.Utils.makeUserForServer(gitOperations, LoginConstants.userGenerator(), Strings.firstNSegments(3));
			return user;
		}

	}
}