package org.softwareFm.server.processors;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.http.RequestLine;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IUser;
import org.softwareFm.common.arrays.ArrayHelper;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.UrlCache;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.server.ICrowdSourcedServer;
import org.softwareFm.server.processors.internal.ChangePasswordProcessor;
import org.softwareFm.server.processors.internal.DeleteProcessor;
import org.softwareFm.server.processors.internal.EmailSaltRequester;
import org.softwareFm.server.processors.internal.FavIconProcessor;
import org.softwareFm.server.processors.internal.ForgottonPasswordProcessor;
import org.softwareFm.server.processors.internal.ForgottonPasswordWebPageProcessor;
import org.softwareFm.server.processors.internal.GetFileProcessor;
import org.softwareFm.server.processors.internal.GetIndexProcessor;
import org.softwareFm.server.processors.internal.GitGetProcessor;
import org.softwareFm.server.processors.internal.HeadThrowing404;
import org.softwareFm.server.processors.internal.LoginProcessor;
import org.softwareFm.server.processors.internal.MakeRootProcessor;
import org.softwareFm.server.processors.internal.MakeSaltForLoginProcessor;
import org.softwareFm.server.processors.internal.PostProcessor;
import org.softwareFm.server.processors.internal.RequestEmailSaltProcessor;
import org.softwareFm.server.processors.internal.RigidFileProcessor;
import org.softwareFm.server.processors.internal.SignupProcessor;

public interface IProcessCall {
	IProcessResult process(RequestLine requestLine, Map<String, Object> parameters);

	abstract public static class Utils {

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


		public static IProcessCall softwareFmProcessCall(ProcessCallParameters processCallParameters, IFunction1<ProcessCallParameters, IProcessCall[]> extraProcessCallFn) {
			File fileRoot = processCallParameters.root;
			ISaltProcessor saltProcessor = processCallParameters.saltProcessor;
			ILoginChecker loginChecker = processCallParameters.loginChecker;
			ISignUpChecker signUpChecker = processCallParameters.signUpChecker;
			IForgottonPasswordMailer forgottonPasswordProcessor = processCallParameters.forgottonPasswordProcessor;
			EmailSaltRequester saltRequester = processCallParameters.saltRequester;
			UrlCache<String> aboveRepostoryUrlCache = processCallParameters.aboveRepostoryUrlCache;
			IGitOperations gitOperations = processCallParameters.gitOperations;
			IProcessCall[] rawProcessCalls = new IProcessCall[] {//
					new FavIconProcessor(),//
					new RigidFileProcessor(fileRoot, "update"),// responds to get or head
					new LoginProcessor(saltProcessor, loginChecker), //
					new SignupProcessor(signUpChecker, saltProcessor, processCallParameters.softwareFmIdGenerator), //
					// usageProcessor,//
					new MakeSaltForLoginProcessor(saltProcessor),//
					new ForgottonPasswordProcessor(saltProcessor, forgottonPasswordProcessor),//
					new RequestEmailSaltProcessor(saltRequester),//
					new ForgottonPasswordWebPageProcessor(processCallParameters.resetter),//
					new ChangePasswordProcessor(processCallParameters.passwordChanger),//
					new HeadThrowing404(),// sweep up any heads
					new GetIndexProcessor(fileRoot),//
					new GetFileProcessor(fileRoot, "html", "jpg", "png", "css", "gif", "jar", "xml"), //
					new GitGetProcessor(gitOperations, aboveRepostoryUrlCache), //
					new MakeRootProcessor(aboveRepostoryUrlCache, gitOperations), //
					new DeleteProcessor(gitOperations),//
					new PostProcessor(gitOperations) };
			IProcessCall[] extraProcessCalls = Functions.call(extraProcessCallFn, processCallParameters);
			IProcessCall[] processCalls = ArrayHelper.insert(rawProcessCalls, extraProcessCalls);
			return chain(processCalls);// this sweeps up any posts, so ensure that commands appear in chain before it
		}

		public static IUser makeUser(IGitOperations gitOperations, Map<String, Callable<Object>>nameAndCallables) {
			IUser user = ICrowdSourcedServer.Utils.makeUserForServer(gitOperations, Strings.firstNSegments(3), nameAndCallables);
			return user;
		}

	}
}