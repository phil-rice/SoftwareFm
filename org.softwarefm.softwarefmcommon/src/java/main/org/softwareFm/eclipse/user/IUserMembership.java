package org.softwareFm.eclipse.user;

public interface IUserMembership extends IUserMembershipReader {
	void addMembership(String softwareFmId, String groupId, String groupCrypto, String membershipStatus);

	void setMembershipProperty(String softwareFmId, String groupId, String property,  String value);

}
