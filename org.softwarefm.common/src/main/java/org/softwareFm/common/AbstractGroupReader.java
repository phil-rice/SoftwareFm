/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.Urls;

public abstract class AbstractGroupReader<GR extends IGitReader> implements IGroupsReader {

	protected IUrlGenerator groupUrlGenerator;
	protected final GR git;

	public AbstractGroupReader(IUrlGenerator groupUrlGenerator, GR gitReader) {
		this.groupUrlGenerator = groupUrlGenerator;
		this.git = gitReader;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getGroupProperty(String groupId, String groupCryptoKey, String propertyName) {
		IFileDescription groupFileDescription = findFileDescription(groupId, groupCryptoKey);
		Map<String, Object> data = getGroupMap(groupFileDescription);
		return (T) data.get(propertyName);
	}

	protected Map<String, Object> getGroupMap(IFileDescription groupFileDescription) {
		Iterable<Map<String, Object>> maps = git.getFileAsListOfMaps(groupFileDescription);
		Iterator<Map<String, Object>> iterator = maps.iterator();
		if (!iterator.hasNext())
			throw new IllegalStateException(groupFileDescription.toString());
		return iterator.next();
	}

	@Override
	public Map<String, Object> getUsageReport(String groupId, String groupCryptoKey, String month) {
		IFileDescription groupFileDescription = findReportFileDescription(groupId, groupCryptoKey, month);
		return git.getFile(groupFileDescription);
	}

	@Override
	public Iterable<Map<String, Object>> users(String groupId, final String groupCryptoKey) {
		IFileDescription groupFileDescription = findFileDescription(groupId, groupCryptoKey);
		return Lists.tail(git.getFileAsListOfMaps(groupFileDescription));
	}


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
		String lines = git.getFileAsString(groupFileDescription);
		List<String> listOfLines = Strings.splitIgnoreBlanks(lines, "\n");
		return listOfLines.size() - 1;
	}
}