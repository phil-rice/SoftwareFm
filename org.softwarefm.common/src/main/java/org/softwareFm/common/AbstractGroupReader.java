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
import org.softwareFm.common.url.Urls;

public abstract class AbstractGroupReader implements IGroupsReader {

	protected IUrlGenerator groupUrlGenerator;

	public AbstractGroupReader(IUrlGenerator groupUrlGenerator) {
		this.groupUrlGenerator = groupUrlGenerator;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getGroupProperty(String groupId, String groupCryptoKey, String propertyName) {
		IFileDescription groupFileDescription = findFileDescription(groupId, groupCryptoKey);
		Map<String, Object> data = getGroupMap(groupFileDescription);
		return (T) data.get(propertyName);
	}

	protected Map<String, Object> getGroupMap(IFileDescription groupFileDescription) {
		String lines = getFileAsString(groupFileDescription);
		List<String> listOfLines = Strings.splitIgnoreBlanks(lines, "\n");
		if (listOfLines.size() == 0)
			throw new IllegalStateException(groupFileDescription.toString());
		String line = listOfLines.get(0);
		String decoded = Crypto.aesDecrypt(groupFileDescription.crypto(), line);
		Map<String, Object> data = Json.mapFromString(decoded);
		return data;
	}

	@Override
	public Map<String, Object> getUsageReport(String groupId, String groupCryptoKey, String month) {
		IFileDescription groupFileDescription = findReportFileDescription(groupId, groupCryptoKey, month);
		String raw = getFileAsString(groupFileDescription);
		return raw == null ? null : Json.mapFromString(Crypto.aesDecrypt(groupCryptoKey, raw));
	}

	@Override
	public Iterable<Map<String, Object>> users(String groupId, final String groupCryptoKey) {
		IFileDescription groupFileDescription = findFileDescription(groupId, groupCryptoKey);
		String lines = getFileAsString(groupFileDescription);
		List<String> listOfLines = Strings.splitIgnoreBlanks(lines, "\n");
		if (listOfLines.size() == 0)
			throw new IllegalStateException(groupFileDescription.toString()+"\n" + listOfLines);
		return Lists.map(Lists.tail(listOfLines), new IFunction1<String, Map<String, Object>>() {
			@Override
			public Map<String, Object> apply(String from) throws Exception {
				String decoded = Crypto.aesDecrypt(groupCryptoKey, from);
				Map<String, Object> data = Json.mapFromString(decoded);
				return data;
			}
		});
	}

	abstract protected String getFileAsString(IFileDescription groupFileDescription);

	protected IFileDescription findFileDescription(String groupId, String groupCryptoKey) {
		String url = findUrl(groupId);
		IFileDescription groupFileDescription = IFileDescription.Utils.encrypted(url, CommonConstants.dataFileName, groupCryptoKey);
		return groupFileDescription;
	}

	protected String findUrl(String groupId) {
		String url = groupUrlGenerator.findUrlFor(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId));
		return url;
	}

	protected IFileDescription findReportFileDescription(String groupId, String groupCryptoKey, String month) {
		String groupUrl = groupUrlGenerator.findUrlFor(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId));
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(Urls.compose(groupUrl, GroupConstants.usageReportDirectory), month, groupCryptoKey);
		return fileDescription;
	}

	@Override
	public int membershipCount(String groupId, String groupCryptoKey) {
		IFileDescription groupFileDescription = findFileDescription(groupId, groupCryptoKey);
		String lines = getFileAsString(groupFileDescription);
		List<String> listOfLines = Strings.splitIgnoreBlanks(lines, "\n");
		return listOfLines.size() - 1;
	}
}
