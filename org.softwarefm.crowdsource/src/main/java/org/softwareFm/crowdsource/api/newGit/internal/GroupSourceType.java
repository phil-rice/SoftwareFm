package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.newGit.IRepoData;
import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.ISourceType;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.crowdsource.utilities.url.Urls;

public class GroupSourceType implements ISourceType {

	private final IUrlGenerator userUrlGenerator;
	private final IUrlGenerator groupUrlGenerator;

	public GroupSourceType(IUrlGenerator userUrlGenerator, IUrlGenerator groupUrlGenerator) {
		this.userUrlGenerator = userUrlGenerator;
		this.groupUrlGenerator = groupUrlGenerator;
	}

	@Override
	public List<ISingleSource> makeSourcesFor(IRepoData repoData, final String rl, final String file, String userId, String userCrypto, final String cryptoKey) {
		String userRl = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, userId));
		String userDataRl = Urls.compose(userRl, CommonConstants.dataFileName);
		EncryptedSingleSource userDataSource = new EncryptedSingleSource(userDataRl, userCrypto);
		String userMembershipCrypto = repoData.readPropertyFromFirstLine(repoData, userDataSource, GroupConstants.membershipCryptoKey);
		String userMembershipRl = Urls.compose(userRl, GroupConstants.membershipFileName);
		EncryptedSingleSource membershipDataSource = new EncryptedSingleSource(userMembershipRl, userMembershipCrypto);

		List<Map<String, Object>> membershipList = repoData.readAllRows(membershipDataSource);
		List<ISingleSource> result = Lists.newList();
		for (Map<String, Object> groupData : membershipList) {
			String crypto = (String) groupData.get(cryptoKey);
			if (crypto != null) {
				String groupId = (String) groupData.get(GroupConstants.groupIdKey);
				String baseGroupRl = groupUrlGenerator.findUrlFor(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId));
				String fullGroupRl = Urls.compose(baseGroupRl, rl, file);
				EncryptedSingleSource source = new EncryptedSingleSource(fullGroupRl, crypto);
				result.add(source);
			}
		}
		return result;

	}
}
