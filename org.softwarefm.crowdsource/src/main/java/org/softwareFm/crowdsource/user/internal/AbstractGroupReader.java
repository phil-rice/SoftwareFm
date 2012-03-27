/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.user.internal;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.utilities.collections.Iterables;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.crowdsource.utilities.url.Urls;

public abstract class AbstractGroupReader implements IGroupsReader {

	protected IUrlGenerator groupUrlGenerator;
	protected final IContainer container;

	public AbstractGroupReader(IContainer container, IUrlGenerator groupUrlGenerator) {
		this.container = container;
		this.groupUrlGenerator = groupUrlGenerator;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getGroupProperty(String groupId, String groupCryptoKey, String propertyName) {
		IFileDescription groupFileDescription = findFileDescription(groupId, groupCryptoKey);
		Map<String, Object> data = getGroupMap(groupFileDescription);
		return (T) data.get(propertyName);
	}

	protected Map<String, Object> getGroupMap(final IFileDescription groupFileDescription) {
		return container.accessGitReader(new IFunction1<IGitReader, Map<String, Object>>() {
			@Override
			public Map<String, Object> apply(IGitReader git) throws Exception {
				Iterable<Map<String, Object>> maps = git.getFileAsListOfMaps(groupFileDescription);
				Iterator<Map<String, Object>> iterator = maps.iterator();
				if (!iterator.hasNext())
					throw new IllegalStateException(groupFileDescription.toString());
				return iterator.next();
			}
		}).get(container.defaultTimeOutMs());
	}

	@Override
	public Map<String, Object> getUsageReport(final String groupId, final String groupCryptoKey, final String month) {
		return container.accessGitReader(new IFunction1<IGitReader, Map<String, Object>>() {
			@Override
			public Map<String, Object> apply(IGitReader git) throws Exception {
				IFileDescription groupFileDescription = findReportFileDescription(groupId, groupCryptoKey, month);
				return git.getFile(groupFileDescription);
			}
		}).get(container.defaultTimeOutMs());
	}

	@Override
	public Iterable<Map<String, Object>> users(final String groupId, final String groupCryptoKey) {
		IFileDescription groupFileDescription = findFileDescription(groupId, groupCryptoKey);
		return Iterables.tail(IGitReader.Utils.getFileAsListOfMaps(container, groupFileDescription));
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
	public int membershipCount(final String groupId, final String groupCryptoKey) {
		return container.accessGitReader(new IFunction1<IGitReader, Integer>() {
			@Override
			public Integer apply(IGitReader git) throws Exception {
				IFileDescription groupFileDescription = findFileDescription(groupId, groupCryptoKey);
				String lines = git.getFileAsString(groupFileDescription);
				List<String> listOfLines = Strings.splitIgnoreBlanks(lines, "\n");
				return listOfLines.size() - 1;
			}
		}).get(container.defaultTimeOutMs());
	}
}