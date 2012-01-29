package org.softwareFm.server.processors;

import java.util.Map;

import org.apache.http.RequestLine;

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

//		public static IProcessCall softwareFmProcessCallWithoutMail(DataSource dataSource, IGitServer server, IFunction1<Map<String, Object>, String> cryptoFn, Callable<String> cryptoGenerator, File fileRoot, Callable<String> monthGetter, Callable<Integer> dayGetter, Callable<String> softwareFmIdGenerator,String groupIdKey, String artifactIdKey) {
//			return softwareFmProcessCall(dataSource, server, cryptoFn, cryptoGenerator, fileRoot, IMailer.Utils.noMailer(), monthGetter, dayGetter, softwareFmIdGenerator, groupIdKey, artifactIdKey);
//		}
//
//		public static IProcessCall softwareFmProcessCall(DataSource dataSource, IGitServer server, IFunction1<Map<String, Object>, String> cryptoFn, Callable<String> cryptoGenerator, File fileRoot, IMailer mailer, Callable<String> monthGetter, Callable<Integer> dayGetter, Callable<String> softwareFmIdGenerator,String groupIdKey, String artifactIdKey) {
//			UrlCache<String> aboveRepostoryUrlCache = new UrlCache<String>();
//			SaltProcessor saltProcessor = new SaltProcessor();
//			LoginChecker loginChecker = new LoginChecker(dataSource);
//			SignUpChecker signUpChecker = new SignUpChecker(dataSource, cryptoGenerator);
//			ForgottonPasswordMailer forgottonPasswordProcessor = new ForgottonPasswordMailer(dataSource, mailer);
//			IPasswordResetter resetter = new PasswordResetter(dataSource);
//			EmailSaltRequester saltRequester = new EmailSaltRequester(dataSource);
//			IPasswordChanger passwordChanger = IPasswordChanger.Utils.databasePasswordChanger(dataSource);
//			IUser user = makeUser(server, cryptoFn, groupIdKey, artifactIdKey);
//			UsageProcessor usageProcessor1 = new UsageProcessor(server, user, monthGetter, dayGetter);
//			UsageProcessor usageProcessor = usageProcessor1;
//			return chain(new FavIconProcessor(),//
//					new RigidFileProcessor(fileRoot, "update"),// responds to get or head
//					new LoginProcessor(saltProcessor, loginChecker), //
//					new SignupProcessor(signUpChecker, saltProcessor, softwareFmIdGenerator, user), //
//					usageProcessor,//
//					new MakeSaltForLoginProcessor(saltProcessor),//
//					new ForgottonPasswordProcessor(saltProcessor, forgottonPasswordProcessor),//
//					new RequestEmailSaltProcessor(saltRequester),//
//					new ForgottonPasswordWebPageProcessor(resetter),//
//					new ChangePasswordProcessor(passwordChanger),//
//					new HeadThrowing404(),// sweep up any heads
//					new GetIndexProcessor(fileRoot),//
//					new GetFileProcessor(fileRoot, "html", "jpg", "png", "css", "gif", "jar", "xml"), //
//					new GitGetProcessor(server, aboveRepostoryUrlCache), //
//					new MakeRootProcessor(aboveRepostoryUrlCache, server), //
//					new DeleteProcessor(server),//
//					new PostProcessor(server));// this sweeps up any posts, so ensure that commands appear in chain before it
//		}
//
//		public static IUser makeUser(IGitServer server, IFunction1<Map<String, Object>, String> cryptoFn, String groupIdKey, String artifactIdKey) {
//			IUser user = IUser.Utils.makeUserDetails(server, ServerConstants.userGenerator(), ServerConstants.projectGenerator(), cryptoFn, groupIdKey, artifactIdKey);
//			return user;
//		}
	}
}