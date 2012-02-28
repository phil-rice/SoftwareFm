/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.softwareFmServer;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.softwareFm.common.AbstractGroupReader;
import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IGroups;
import org.softwareFm.common.collections.Files;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;

public class GroupsForServer extends AbstractGroupReader<IGitOperations> implements IGroups {

	private final IFunction1<String, String> repoUrlGenerator;

	public GroupsForServer(IUrlGenerator urlGenerator, IGitOperations gitOperations, IFunction1<String, String> repoUrlGenerator) {
		super(urlGenerator, gitOperations);
		this.repoUrlGenerator = repoUrlGenerator;
	}

	@Override
	public void refresh(String groupId) {
		git.clearCaches();
	}

	@Override
	public void setGroupProperty(String groupId, String groupCryptoKey, String property, String value) {
		IFileDescription fileDescription = findFileDescription(groupId, groupCryptoKey);
		String lines = git.getFileAsString(fileDescription);
		List<String> listOfLines = Strings.splitIgnoreBlanks(lines, "\n");
		Map<String, Object> data = initialMap(fileDescription, listOfLines);
		Map<String, Object> newData = Maps.with(data, property, value);
		String newLine0 = Crypto.aesEncrypt(groupCryptoKey, Json.toString(newData));
		String newValue = Strings.join(Lists.addAtStart(Lists.tail(listOfLines), newLine0), "\n") +"\n";
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