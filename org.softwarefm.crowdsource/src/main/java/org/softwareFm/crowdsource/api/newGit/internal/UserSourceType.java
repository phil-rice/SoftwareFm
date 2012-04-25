package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.Collections;
import java.util.List;

import org.softwareFm.crowdsource.api.newGit.IRepoData;
import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.ISourceType;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.crowdsource.utilities.url.Urls;

public class UserSourceType implements ISourceType {

	private final IUrlGenerator userUrlGenerator;

	public UserSourceType(IUrlGenerator userUrlGenerator) {
		this.userUrlGenerator = userUrlGenerator;
	}

	@Override
	public List<ISingleSource> makeSourcesFor(IRepoData reader, String rl, String file, String userId, String userCrypto, String cryptoKey) {
		String baseRl = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, userId));
		String baseDataRl = Urls.compose(baseRl, CommonConstants.dataFileName);
		String crypto = reader.readPropertyFromFirstLine(reader, new EncryptedSingleSource(baseDataRl, userCrypto), cryptoKey);
		if (crypto == null)
			return Collections.emptyList();
		return Collections.<ISingleSource> singletonList(new EncryptedSingleSource(Urls.compose(baseRl, rl, file), crypto));
	}

}
