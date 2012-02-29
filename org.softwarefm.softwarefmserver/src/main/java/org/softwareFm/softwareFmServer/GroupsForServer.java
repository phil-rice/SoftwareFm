/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.softwareFmServer;

import java.io.File;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.common.AbstractGroupReader;
import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IGroups;
import org.softwareFm.common.collections.Files;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;

public class GroupsForServer extends AbstractGroupReader<IGitOperations> implements IGroups {

	private final IFunction1<String, String> repoUrlGenerator;
	private final Map<String, Callable<Object>> defaultProperties;

	public GroupsForServer(IUrlGenerator urlGenerator, IGitOperations gitOperations, IFunction1<String, String> repoUrlGenerator) {
		this(urlGenerator, gitOperations, repoUrlGenerator, Collections.<String, Callable<Object>> emptyMap());
	}

	public GroupsForServer(IUrlGenerator urlGenerator, IGitOperations gitOperations, IFunction1<String, String> repoUrlGenerator, Map<String, Callable<Object>> defaultProperties) {
		super(urlGenerator, gitOperations);
		this.repoUrlGenerator = repoUrlGenerator;
		this.defaultProperties = defaultProperties;
	}

	@Override
	public void refresh(String groupId) {
		git.clearCaches();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getGroupProperty(String groupId, String groupCryptoKey, String propertyName) {
		Object result = super.getGroupProperty(groupId, groupCryptoKey, propertyName);
		if (result == null) {
			Callable<Object> creator = defaultProperties.get(propertyName);
			if (creator != null) {
				result = Callables.call(creator);
				setGroupProperty(groupId, groupCryptoKey, propertyName, result);
			}
		}
		return (T) result;
	}

	@Override
	public void setUserProperty(String groupId, String groupCrypto, final String softwareFmId, final String property, final String value) {
		IFileDescription fileDescription = findFileDescription(groupId, groupCrypto);
		int changedCount = git.map(fileDescription, new IFunction1<Map<String, Object>, Boolean>() {
			@Override
			public Boolean apply(Map<String, Object> from) throws Exception {
				return softwareFmId.equals(from.get(LoginConstants.softwareFmIdKey));
			}
		}, new IFunction1<Map<String, Object>, Map<String, Object>>() {
			@Override
			public Map<String, Object> apply(Map<String, Object> from) throws Exception {
				return Maps.with(from, property, value);
			}
		}, "setUserProperty(" + groupId + ", " + softwareFmId + ", " + property + ", " + value);
		if (changedCount != 1)
			throw new IllegalArgumentException(MessageFormat.format(GroupConstants.errorSettingUserProperty, groupId, softwareFmId, property, value, changedCount));
	}

	@Override
	public void setGroupProperty(String groupId, String groupCryptoKey, String property, Object value) {
		// TODO rewrite with git.map
		IFileDescription fileDescription = findFileDescription(groupId, groupCryptoKey);
		String lines = git.getFileAsString(fileDescription);
		List<String> listOfLines = Strings.splitIgnoreBlanks(lines, "\n");
		Map<String, Object> data = initialMap(fileDescription, listOfLines);
		Map<String, Object> newData = Maps.with(data, property, value);
		String newLine0 = Crypto.aesEncrypt(groupCryptoKey, Json.toString(newData));
		String newValue = Strings.join(Lists.addAtStart(Lists.tail(listOfLines), newLine0), "\n") + "\n";
		File file = fileDescription.getFile(git.getRoot());
		makeRepoIfNecessary(fileDescription);
		Files.setText(file, newValue);
		String message = "setGroupProperty " + property + "," + value;
		addAllAndCommit(fileDescription, message);
	}

	protected void makeRepoIfNecessary(IFileDescription fileDescription) {
		File file = fileDescription.getFile(git.getRoot());
		String url = fileDescription.url();
		if (!file.exists()) {
			Files.makeDirectoryForFile(file);
			File repositoryUrl = fileDescription.findRepositoryUrl(git.getRoot());
			if (repositoryUrl == null) {
				git.init(Functions.call(repoUrlGenerator, url));
			}
		}
	}

	protected void addAllAndCommit(IFileDescription fileDescription, String message) {
		String repositoryUrl = IFileDescription.Utils.findRepositoryUrl(git.getRoot(), fileDescription.url());
		git.addAllAndCommit(repositoryUrl, message);
	}

	protected Map<String, Object> initialMap(IFileDescription fileDescription, List<String> listOfLines) {
		if (listOfLines.size() == 0)
			return Collections.emptyMap();
		String line = listOfLines.get(0);
		String decoded = Crypto.aesDecrypt(fileDescription.crypto(), line);
		Map<String, Object> data = Json.mapFromString(decoded);
		return data;
	}

	@Override
	public void addUser(String groupId, String groupCryptoKey, Map<String, Object> userDetails) {
		IFileDescription fileDescription = findFileDescription(groupId, groupCryptoKey);
		git.append(fileDescription, userDetails);
		addAllAndCommit(fileDescription, "addUser " + userDetails);
	}

	@Override
	public void setReport(String groupId, String groupCryptoKey, String month, Map<String, Object> report) {
		IFileDescription fileDescription = findReportFileDescription(groupId, groupCryptoKey, month);
		makeRepoIfNecessary(fileDescription);
		git.put(fileDescription, report);
	}

}