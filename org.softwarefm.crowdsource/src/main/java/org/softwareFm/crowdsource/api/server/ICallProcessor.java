/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.server;

import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.http.RequestLine;
import org.softwareFm.crowdsource.api.ICrowdSourcedServer;
import org.softwareFm.crowdsource.api.IIdAndSaltGenerator;
import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.IUserCryptoAccess;
import org.softwareFm.crowdsource.api.ServerConfig;
import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.user.IUser;
import org.softwareFm.crowdsource.server.callProcessor.internal.AcceptInviteGroupProcessor;
import org.softwareFm.crowdsource.server.callProcessor.internal.ChangePasswordProcessor;
import org.softwareFm.crowdsource.server.callProcessor.internal.CommentProcessor;
import org.softwareFm.crowdsource.server.callProcessor.internal.ForgottonPasswordProcessor;
import org.softwareFm.crowdsource.server.callProcessor.internal.ForgottonPasswordWebPageProcessor;
import org.softwareFm.crowdsource.server.callProcessor.internal.GitGetProcessor;
import org.softwareFm.crowdsource.server.callProcessor.internal.InviteGroupProcessor;
import org.softwareFm.crowdsource.server.callProcessor.internal.KickFromGroupCommandProcessor;
import org.softwareFm.crowdsource.server.callProcessor.internal.LeaveGroupCommandProcessor;
import org.softwareFm.crowdsource.server.callProcessor.internal.LoginProcessor;
import org.softwareFm.crowdsource.server.callProcessor.internal.MakeRootProcessor;
import org.softwareFm.crowdsource.server.callProcessor.internal.MakeSaltForLoginProcessor;
import org.softwareFm.crowdsource.server.callProcessor.internal.RequestEmailSaltProcessor;
import org.softwareFm.crowdsource.server.callProcessor.internal.SignupProcessor;
import org.softwareFm.crowdsource.server.callProcessor.internal.TakeOnGroupProcessor;
import org.softwareFm.crowdsource.server.doers.internal.DeleteProcessor;
import org.softwareFm.crowdsource.server.doers.internal.PostProcessor;
import org.softwareFm.crowdsource.utilities.arrays.ArrayHelper;
import org.softwareFm.crowdsource.utilities.strings.Strings;

public interface ICallProcessor extends IServerDoer{
	IProcessResult process(RequestLine requestLine, Map<String, Object> parameters);

	abstract public static class Utils {

		public static ICallProcessor chain(final ICallProcessor... processors) {
			return new ICallProcessor() {
				@Override
				public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
					for (ICallProcessor processor : processors) {
						IProcessResult result = processor.process(requestLine, parameters);
						if (result != null)
							return result;
					}
					throw new IllegalArgumentException(Utils.toString(requestLine, parameters));
				}
			};
		}

		public static ICallProcessor toStringProcessCall() {
			return new ICallProcessor() {
				@Override
				public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
					return IProcessResult.Utils.processString(Utils.toString(requestLine, parameters));
				}

			};
		}

		private static String toString(RequestLine requestLine, Map<String, Object> parameters) {
			return "<" + requestLine + ", " + parameters + ">";
		}


		public static ICallProcessor softwareFmProcessCall(IUserAndGroupsContainer container, IServerDoers serverDoers, ServerConfig serverConfig) {
			IUserCryptoAccess userCryptoAccess = serverConfig.userCryptoAccess;
			IIdAndSaltGenerator idAndSaltGenerator = serverConfig.idAndSaltGenerator;
			ICallProcessor[] rawProcessCalls = new ICallProcessor[] {//
					new MakeRootProcessor(serverConfig.aboveRepostoryUrlCache, container), //

					new LoginProcessor(serverDoers.getSaltProcessor(), serverDoers.getLoginChecker()), //
					new SignupProcessor(serverDoers.getSignUpChecker(), serverDoers.getSaltProcessor(), idAndSaltGenerator), //
					new MakeSaltForLoginProcessor(serverDoers.getSaltProcessor()),//
					new ForgottonPasswordProcessor(serverDoers.getSaltProcessor(), serverDoers.getForgottonPasswordMailer()),//
					new RequestEmailSaltProcessor(serverDoers.getEmailSaltRequester()),//
					new ForgottonPasswordWebPageProcessor(serverDoers.getPasswordResetter()),//
					new ChangePasswordProcessor(userCryptoAccess),//
					
					new TakeOnGroupProcessor(serverDoers, userCryptoAccess, idAndSaltGenerator, serverConfig.cryptoGenerators, container),//
					new InviteGroupProcessor(container, serverDoers, userCryptoAccess, idAndSaltGenerator),//
					new AcceptInviteGroupProcessor(userCryptoAccess, container),//
					new LeaveGroupCommandProcessor(container, userCryptoAccess),//
					new KickFromGroupCommandProcessor(container, userCryptoAccess),//
					new GitGetProcessor(container, serverConfig.aboveRepostoryUrlCache), //
					new CommentProcessor(container, userCryptoAccess),//
					new DeleteProcessor(container),//
					new PostProcessor(container.gitOperations()) };
			ICallProcessor[] extraProcessCalls = serverConfig.extraCallProcessors.makeExtraCalls(container, serverConfig);
			ICallProcessor[] processCalls = ArrayHelper.insert(rawProcessCalls, extraProcessCalls);
			return chain(processCalls);
		}

		public static IUser makeUser(IGitOperations gitOperations, Map<String, Callable<Object>>nameAndCallables, String prefix) {
			IUser user = ICrowdSourcedServer.Utils.makeUserForServer(gitOperations, Strings.firstNSegments(3), nameAndCallables, prefix);
			return user;
		}

	}
}