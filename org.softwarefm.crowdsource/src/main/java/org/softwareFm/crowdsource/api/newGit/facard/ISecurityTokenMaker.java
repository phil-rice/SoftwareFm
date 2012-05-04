package org.softwareFm.crowdsource.api.newGit.facard;

import java.text.MessageFormat;
import java.util.Map;

import org.apache.log4j.Logger;
import org.softwareFm.crowdsource.api.UserData;
import org.softwareFm.crowdsource.api.newGit.IRepoData;
import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.constants.SecurityConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.strings.Strings;

/**
 * In order to reduce the threat of spammers and replay attacks, a security token is sent with any command to modify a file. This is derived from:
 * <ul>
 * <li>UserId
 * <li>Email
 * <li>UserCrypto
 * <li>rl
 * <li>CurrentValueOfFile
 * </ul>
 * 
 * <p>
 * Note that this also enforces that you are There are some attacks that this won't prevent...for example deleting a file which has been reinstated with exactly the same value. <br />
 * To avoid these attacks would require (I think) the exchange of a token between client and server, making this much more stateful
 * 
 * <p>
 * The reason that the security token also returns the current digest of the file, is to differentiate versions changing issues from security issues
 */
public interface ISecurityTokenMaker {
	SecurityToken generateToken(IRepoData repoData, UserData userData, String rl);

	public static Logger logger = Logger.getLogger(ISecurityTokenMaker.class);

	public static class Utils {
		/** Makes a new ISecurityTokenMaker. "purpose" is used in log messages" */
		public static ISecurityTokenMaker tokenMaker(final String purpose) {
			return new ISecurityTokenMaker() {
				@Override
				public SecurityToken generateToken(IRepoData repoData, UserData userData, String rl) {
					ISingleSource singleSource = ISingleSource.Utils.raw(rl);
					StringBuilder builder = new StringBuilder();
					builder.append(userData.softwareFmId);
					builder.append(userData.email);
					builder.append(userData.crypto);
					builder.append(rl);
					String text = Strings.join(repoData.readRaw(singleSource), "\n");
					String fileDigest = Crypto.digest(text);
					builder.append(fileDigest);
					String token = Crypto.digest(builder.toString());
					if (logger.isDebugEnabled())
						logger.debug(MessageFormat.format("Making security token for {0}. RL is {1}. {2}\n{3}\nFile Digest {4}\nToken {5}", purpose, rl, userData, text, fileDigest, token));
					return new SecurityToken(token, fileDigest);
				}
			};
		}

		public static void addSecurityToken(Map<String, Object> map, SecurityToken token) {
			map.put(SecurityConstants.securityTokenKey, token.token);
			map.put(SecurityConstants.fileDigestKey, token.fileDigest);
		}

		public static SecurityToken fromMap(Map<String, Object> map) {
			return new SecurityToken((String) map.get(SecurityConstants.securityTokenKey), (String) map.get(SecurityConstants.fileDigestKey));
		}

	}
}
