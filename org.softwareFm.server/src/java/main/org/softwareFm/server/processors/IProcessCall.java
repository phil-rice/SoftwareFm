package org.softwareFm.server.processors;

import java.io.File;
import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.IGitServer;
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
import org.softwareFm.utilities.maps.UrlCache;

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

		public static IProcessCall softwareFmProcessCall(IGitServer server, File fileRoot) {
			UrlCache<String> aboveRepostoryUrlCache = new UrlCache<String>();
			SaltProcessor saltProcessor = new SaltProcessor();
			LoginChecker loginChecker = new LoginChecker();
			SignUpChecker signUpChecker = new SignUpChecker();
			ForgottonPasswordMailer forgottonPasswordProcessor = new ForgottonPasswordMailer(null, null, null);
			IPasswordResetter resetter = new PasswordResetter();
			EmailSaltRequester saltRequester = new EmailSaltRequester();
			return chain(new FavIconProcessor(),//
					new RigidFileProcessor(fileRoot, "update"),// responds to get or head
					new LoginProcessor(saltProcessor, loginChecker), //
					new SignupProcessor(signUpChecker, saltProcessor), //
					new MakeSaltForLoginProcessor(saltProcessor),//
					new ForgottonPasswordProcessor(saltProcessor, forgottonPasswordProcessor),//
					new RequestEmailSaltProcessor(saltRequester),//
					new ForgottonPasswordWebPageProcessor(resetter),//
					new HeadThrowing404(),// sweep up any heads
					new GetIndexProcessor(fileRoot),//
					new GetFileProcessor(fileRoot, "html", "jpg", "png", "css", "gif", "jar", "xml"), //
					new GitGetProcessor(server, aboveRepostoryUrlCache), //
					new MakeRootProcessor(aboveRepostoryUrlCache, server), //
					new DeleteProcessor(server),//
					new PostProcessor(server));// this sweeps up any posts, so ensure that commands appear in chain before it
		}
	}
}