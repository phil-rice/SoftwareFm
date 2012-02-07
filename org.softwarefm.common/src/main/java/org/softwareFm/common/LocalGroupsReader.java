package org.softwareFm.common;

import java.util.List;
import java.util.Map;

import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;

public class LocalGroupsReader implements IGroupsReader {

	 protected IUrlGenerator urlGenerator;
	 protected IGitLocal gitLocal;

	public LocalGroupsReader(IUrlGenerator urlGenerator, IGitLocal gitLocal) {
		this.urlGenerator = urlGenerator;
		this.gitLocal = gitLocal;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T>T getGroupProperty(String groupId, String groupCryptoKey, String propertyName) {
		IFileDescription groupFileDescription = findFileDescription(groupId, groupCryptoKey);
		String lines = gitLocal.getFileAsString(groupFileDescription);
		List<String> listOfLines = Strings.splitIgnoreBlanks(lines, "\n");
		if (listOfLines.size() == 0)
			throw new IllegalStateException(groupFileDescription.toString());
		String line = listOfLines.get(0);
		String decoded = Crypto.aesDecrypt(groupCryptoKey, line);
		Map<String, Object> data = Json.mapFromString(decoded);
		return (T) data.get(propertyName);
	}

	protected IFileDescription findFileDescription(String groupId, String groupCryptoKey) {
		String url = findUrl(groupId);
		IFileDescription groupFileDescription = IFileDescription.Utils.encrypted(url, CommonConstants.dataFileName, groupCryptoKey);
		return groupFileDescription;
	}

	@Override
	public Iterable<Map<String, Object>> users(String groupId, final String groupCryptoKey) {
		IFileDescription groupFileDescription = findFileDescription(groupId, groupCryptoKey);
		String lines = gitLocal.getFileAsString(groupFileDescription);
		List<String> listOfLines = Strings.splitIgnoreBlanks(lines, "\n");
		if (listOfLines.size() == 0)
			throw new IllegalStateException(groupFileDescription.toString());
		return Lists.map(Lists.tail(listOfLines), new IFunction1<String, Map<String, Object>>() {
			@Override
			public Map<String, Object> apply(String from) throws Exception {
				String decoded = Crypto.aesDecrypt(groupCryptoKey, from);
				Map<String, Object> data = Json.mapFromString(decoded);
				return data;
			}
		});
	}

	@Override
	public void refresh(String groupId) {
		gitLocal.clearCache(findUrl(groupId));
	}

	protected String findUrl(String groupId) {
		String url = urlGenerator.findUrlFor(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId));
		return url;
	}
}
