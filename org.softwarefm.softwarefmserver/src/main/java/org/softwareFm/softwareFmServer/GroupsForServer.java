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

public class GroupsForServer extends AbstractGroupReader implements IGroups {

	private final IGitOperations gitOperations;
	private final IFunction1<String, String> repoUrlGenerator;

	public GroupsForServer(IUrlGenerator urlGenerator, IGitOperations gitOperations, IFunction1<String, String> repoUrlGenerator) {
		super(urlGenerator);
		this.gitOperations = gitOperations;
		this.repoUrlGenerator = repoUrlGenerator;
	}

	@Override
	public void refresh(String groupId) {
		gitOperations.clearCaches();
	}

	@Override
	public void setGroupProperty(String groupId, String groupCryptoKey, String property, String value) {
		IFileDescription fileDescription = findFileDescription(groupId, groupCryptoKey);
		String lines = getFileAsString(fileDescription);
		List<String> listOfLines = Strings.splitIgnoreBlanks(lines, "\n");
		Map<String, Object> data = initialMap(fileDescription, listOfLines);
		Map<String, Object> newData = Maps.with(data, property, value);
		String newLine0 = Crypto.aesEncrypt(groupCryptoKey, Json.toString(newData));
		String newValue = Strings.join(Lists.addAtStart(Lists.tail(listOfLines), newLine0), "\n");
		File file = fileDescription.getFile(gitOperations.getRoot());
		makeRepoIfNecessary(fileDescription);
		Files.setText(file, newValue);
		String message = "setGroupProperty " + property + "," + value;
		addAllAndCommit(fileDescription, message);
	}

	protected void makeRepoIfNecessary(IFileDescription fileDescription) {
		File file = fileDescription.getFile(gitOperations.getRoot());
		String url = fileDescription.url();
		if (!file.exists()) {
			Files.makeDirectoryForFile(file);
			File repositoryUrl = fileDescription.findRepositoryUrl(gitOperations.getRoot());
			if (repositoryUrl == null) {
				gitOperations.init(Functions.call(repoUrlGenerator, url));
			}
		}
	}

	protected void addAllAndCommit(IFileDescription fileDescription, String message) {
		String repositoryUrl = IFileDescription.Utils.findRepositoryUrl(gitOperations.getRoot(), fileDescription.url());
		gitOperations.addAllAndCommit(repositoryUrl, message);
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
		String lines = getFileAsString(fileDescription);
		if (lines == null || lines.length() == 0)
			throw new IllegalStateException();
		String newLines = lines + "\n" + Crypto.aesEncrypt(groupCryptoKey, Json.toString(userDetails));
		File file = fileDescription.getFile(gitOperations.getRoot());
		Files.setText(file, newLines);
		addAllAndCommit(fileDescription, "addUser " + userDetails);
	}

	@Override
	protected String getFileAsString(IFileDescription groupFileDescription) {
		return gitOperations.getFileAsString(groupFileDescription);
	}

	@Override
	public void setReport(String groupId, String groupCryptoKey, String month, Map<String, Object> report) {
		IFileDescription fileDescription = findReportFileDescription(groupId, groupCryptoKey, month);
		makeRepoIfNecessary(fileDescription);
		gitOperations.put(fileDescription, report);
	}

}
