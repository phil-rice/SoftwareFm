package org.softwareFm.eclipse.user;

import java.util.List;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.LocalGroupsReader;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.Urls;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;

public class GroupUsageReaderForLocal extends LocalGroupsReader implements IGroupUsageReader {

	public GroupUsageReaderForLocal(IUrlGenerator urlGenerator, IGitLocal gitLocal) {
		super(urlGenerator, gitLocal);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Map<String, Map<String, List<Integer>>>> getProjectDetails(String groupId, String groupCryptoKey, String month) {
		String baseUrl = findUrl(groupId);
		String projectUrl = Urls.compose(baseUrl, SoftwareFmConstants.projectDirectoryName);
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(projectUrl, month, groupCryptoKey);
		return (Map) gitLocal.getFile(fileDescription);
	}

}
