package org.softwareFm.eclipse.user;

import java.util.List;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IUser;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;

public abstract class AbstractUserMembershipReader implements IUserMembershipReader {

	protected final IUrlGenerator userUrlGenerator;
	protected final IUser user;

	public AbstractUserMembershipReader(IUrlGenerator userUrlGenerator, IUser user) {
		this.userUrlGenerator = userUrlGenerator;
		this.user = user;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getMembershipProperty(String softwareFmId, String groupId, String property) {
		for (Map<String,Object> membershipProperties: walkGroupsFor(softwareFmId))
			if (groupId.equals(membershipProperties.get(GroupConstants.groupIdKey)))
				return (T) membershipProperties.get(property);
		return null;
	}

	abstract protected String getMembershipCrypto(String softwareFmId);

	@Override
	public List<Map<String,Object>> walkGroupsFor(String softwareFmId) {
		String url = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		String usersMembershipCrypto = getMembershipCrypto(softwareFmId);
		String text = getGroupFileAsText(IFileDescription.Utils.encrypted(url, GroupConstants.membershipFileName, usersMembershipCrypto));
		List<String> lines = Strings.splitIgnoreBlanks(text, "\n");
		return Lists.map(lines, Json.decryptAndMapMakeFn(usersMembershipCrypto));
	}


	abstract protected String getGroupFileAsText(IFileDescription fileDescription);
	
}
