package org.softwareFm.eclipse.user;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.IUser;
import org.softwareFm.common.url.IUrlGenerator;

public class UserMembershipReaderForLocal extends AbstractUserMembershipReader {

	private final IGitLocal gitLocal;
	private final String membershipCrypto;

	public UserMembershipReaderForLocal(IUrlGenerator userUrlGenerator, IGitLocal gitLocal, IUser user, String membershipCrypto) {
		super(userUrlGenerator, user);
		this.gitLocal = gitLocal;
		this.membershipCrypto = membershipCrypto;
	}

	@Override
	protected String getGroupFileAsText(IFileDescription fileDescription) {
		return gitLocal.getFileAsString(fileDescription);
	}

	@Override
	protected String getMembershipCrypto(String softwareFmId) {
		return membershipCrypto;
	}

}
