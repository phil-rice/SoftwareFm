package org.softwareFm.crowdsource.api.newGit.facard;

import org.softwareFm.crowdsource.api.UserData;
import org.softwareFm.crowdsource.api.newGit.IRepoData;
import org.softwareFm.crowdsource.api.newGit.exceptions.FileDigestMismatchException;
import org.softwareFm.crowdsource.api.newGit.exceptions.SecurityTokenMismatchException;
import org.softwareFm.crowdsource.api.newGit.exceptions.SecurityTokenNotPresentException;
import org.softwareFm.crowdsource.constants.GitMessages;

public interface ISecurityTokenChecker {
	/** This checks that the userdata and rl would produce this token. The file digests are checked first, then the security tokens compared 
	 * @param repoData TODO*/
	void validate(IRepoData repoData, SecurityToken token, UserData userData, String rl) throws FileDigestMismatchException, SecurityTokenMismatchException;
	
	public static class Utils {
		public static ISecurityTokenChecker checker(){
			return new ISecurityTokenChecker() {
				private final ISecurityTokenMaker maker = ISecurityTokenMaker.Utils.tokenMaker("Validation");
				@Override
				public void validate(IRepoData repoData, SecurityToken token, UserData userData, String rl) throws FileDigestMismatchException, SecurityTokenMismatchException {
					if (token.fileDigest == null)
						throw new SecurityTokenNotPresentException(GitMessages.fileDigestNotPresent, token, userData, rl);
					if (token.token == null)
						throw new SecurityTokenNotPresentException(GitMessages.tokenNotPresent, token, userData, rl);
					SecurityToken expectedToken = maker.generateToken(repoData, userData, rl);
					if (!expectedToken.fileDigest.equals(token.fileDigest))
						throw new FileDigestMismatchException(rl, expectedToken.fileDigest, token.fileDigest);
					if (!expectedToken.equals(token))
						throw new SecurityTokenMismatchException(userData, rl, expectedToken, token);
				}
			};
		}
	}
}
