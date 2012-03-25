/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.git.internal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.CommonMessages;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.crowdsource.utilities.url.Urls;

public class GitOperations implements IGitOperations {

	@Override
	public String toString() {
		return "GitOperations [root=" + root + "]";
	}

	public final File root;

	public GitOperations(File root) {
		this.root = root;
	}

	@Override
	public void init(final String url) {
		useFileRepository(url, new IFunction1<FileRepository, Void>() {
			@Override
			public Void apply(FileRepository from) throws Exception {
				File fullRoot = new File(root, url);
				File file = IFileDescription.Utils.findRepositoryFile(root, url);
				if (fullRoot.equals(file))
					return null; // already exists
				if (file != null)
					throw new IllegalArgumentException(MessageFormat.format(CommonMessages.cannotCreateGitUnderSecondRepository, url));
				Git git = Git.init().setDirectory(fullRoot).call();
				git.getRepository().close();
				return null;
			}
		});
	}

	@Override
	public void put(IFileDescription fileDescription, Map<String, Object> data) {
		IFileDescription.Utils.save(root, fileDescription, data);
		addAllAndCommit(fileDescription, "put " + fileDescription + " " + data);

	}

	protected void addAllAndCommit(IFileDescription fileDescription, String commitMessage) {
		File repositoryFile = fileDescription.findRepositoryUrl(root);
		if (repositoryFile == null)
			throw new NullPointerException(MessageFormat.format(CommonMessages.cannotFindRepositoryUrl, fileDescription));
		String url = Files.offset(root, repositoryFile);
		addAllAndCommit(url, commitMessage);
	}

	@Override
	public int countOfFileAsListsOfMap(IFileDescription fileDescription) {
		String lines = getFileAsString(fileDescription);
		List<String> listOfLines = Strings.splitIgnoreBlanks(lines, "\n");
		return listOfLines.size() - 1;
	}

	@Override
	public void pull(final String url) {
		useFileRepository(url, new IFunction1<FileRepository, Void>() {
			@Override
			public Void apply(FileRepository fileRepository) throws Exception {
				try {
					PullCommand pull = new Git(fileRepository).pull();
					pull.call();
					return null;
				} catch (Exception e) {
					throw new RuntimeException("Pull error with url: " + url, e);
				}
			}
		});
	}

	@Override
	public void gc(final String url) {
		useFileRepository(url, new IFunction1<FileRepository, Void>() {
			@Override
			public Void apply(FileRepository from) throws Exception {
				File file = new File(root, url);
				Runtime.getRuntime().exec("git", new String[] { "gc" }, file);
				return null;
			}
		});
	}

	@Override
	public void addAllAndCommit(String url, final String message) {
		useFileRepository(url, new IFunction1<FileRepository, Void>() {
			@Override
			public Void apply(FileRepository fileRepository) throws Exception {
				Git git = new Git(fileRepository);
				new AddCommand(fileRepository).addFilepattern(".").call();
				CommitCommand commit = git.commit();
				commit.setAll(true).setMessage(message);
				commit.call();
				return null;
			}
		});

	}

	@Override
	public void setConfigForRemotePull(final String url, final String remotePrefix) {
		useFileRepository(url, new IFunction1<FileRepository, Void>() {
			@Override
			public Void apply(FileRepository fileRepository) throws Exception {
				FileBasedConfig config = fileRepository.getConfig();
				config.setString("remote", "origin", "url", Urls.compose(remotePrefix, url));
				config.setString("remote", "origin", "fetch", "+refs/heads/*:refs/remotes/origin/*");
				config.setString("branch", "master", "remote", "origin");
				config.setString("branch", "master", "merge", "refs/heads/master");
				config.save();
				return null;
			}
		});
	}

	@Override
	public String getConfig(String url, final String section, final String subsection, final String name) {
		String result = useFileRepository(url, new IFunction1<FileRepository, String>() {
			@Override
			public String apply(FileRepository fileRepository) throws Exception {
				FileBasedConfig gitConfig = fileRepository.getConfig();
				String result = gitConfig.getString(section, subsection, name);
				return result;
			}
		});
		return result;
	}

	@Override
	public String getBranch(String url) {
		return useFileRepository(url, new IFunction1<FileRepository, String>() {
			@Override
			public String apply(FileRepository fileRepository) throws Exception {
				return fileRepository.getBranch();
			}
		});
	}

	public <T> T useFileRepository(final String url, final IFunction1<FileRepository, T> function) {
		try {
			File dir = new File(root, url);
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			FileRepository repository = builder.//
					setGitDir(new File(dir, CommonConstants.DOT_GIT))//
					.readEnvironment() // scan environment GIT_* variables
					.build(); // scan up the file system tree
			try {
				T result = function.apply(repository);
				return result;
			} finally {
				repository.close();
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public Map<String, Object> getFile(IFileDescription fileDescription) {
		String text = getFileAsString(fileDescription);
		if (text != null) {
			Map<String, Object> map = fileDescription.decode(text.trim());
			return map;
		}
		return null;
	}

	@Override
	public String getFileAsString(IFileDescription fileDescription) {
		File directory = fileDescription.getDirectory(root);
		if (directory.exists()) {
			File file = fileDescription.getFile(root);
			if (file.exists()) {
				String text = Files.getText(file);
				return text;
			}
		}
		return null;
	}

	@Override
	public Map<String, Object> getFileAndDescendants(IFileDescription fileDescription) {
		Map<String, Object> map = getFile(fileDescription);
		if (map == null)
			map = Maps.newMap();
		File directory = fileDescription.getDirectory(root);
		for (File child : Files.listChildDirectoriesIgnoringDot(directory)) {
			File childFile = fileDescription.getFileInSubdirectory(child);
			Map<String, Object> collectionResults = Maps.newMap();
			if (childFile.exists())
				collectionResults.putAll(fileDescription.decode(Files.getText(childFile)));
			for (File grandChild : Files.listChildDirectoriesIgnoringDot(child))
				addDataFromFileIfExists(fileDescription, collectionResults, grandChild);
			map.put(child.getName(), collectionResults);
		}
		return map;
	}

	private void addDataFromFileIfExists(IFileDescription fileDescription, Map<String, Object> collectionResults, File directory) {
		File file = fileDescription.getFileInSubdirectory(directory);
		if (file.exists())
			collectionResults.put(directory.getName(), fileDescription.decode(Files.getText(file)));
	}

	@Override
	public File getRoot() {
		return root;
	}

	@Override
	public void clearCaches() {
	}

	@Override
	public void append(IFileDescription fileDescription, Map<String, Object> data) {
		try {
			File file = fileDescription.getFile(getRoot());
			file.getParentFile().mkdirs();
			FileWriter fileWriter = new FileWriter(file, true);
			try {
				fileWriter.append(fileDescription.encode(data) + "\n");
			} finally {
				fileWriter.close();
			}
			addAllAndCommit(fileDescription, "append " + fileDescription + " " + data);
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}

	}

	@Override
	public List<Map<String, Object>> getFileAsListOfMaps(IFileDescription fileDescription) {
		File file = fileDescription.getFile(getRoot());
		if (!file.exists())
			return Collections.emptyList();
		String text = Files.getText(file);
		List<String> lines = Strings.splitIgnoreBlanks(text, "\n");
		return Lists.map(lines, Json.decryptAndMapMakeFn(fileDescription.crypto()));
	}

	@Override
	public int removeLine(IFileDescription fileDescription, IFunction1<Map<String, Object>, Boolean> acceptor, String commitMessage) {
		String text = getFileAsString(fileDescription);
		List<String> lines = Strings.splitIgnoreBlanks(text, "\n");
		int count = 0;
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			Map<String, Object> map = fileDescription.decode(line);
			if (Functions.call(acceptor, map))
				count++;
			else
				builder.append(line + "\n");
		}
		if (count > 0) {
			File file = fileDescription.getFile(getRoot());
			Files.setText(file, builder.toString());
			File findRepositoryFile = fileDescription.findRepositoryUrl(getRoot());
			String repositoryUrl = Files.offset(getRoot(), findRepositoryFile);
			if (repositoryUrl == null)
				throw new IllegalStateException(file.toString());
			addAllAndCommit(repositoryUrl, commitMessage);
		}
		return count;
	}

	@Override
	public int map(IFileDescription fileDescription, IFunction1<Map<String, Object>, Boolean> acceptor, IFunction1<Map<String, Object>, Map<String, Object>> transform, String commitMessage) {
		String text = getFileAsString(fileDescription);
		List<String> lines = Strings.splitIgnoreBlanks(text, "\n");
		int count = 0;
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			Map<String, Object> map = fileDescription.decode(line);
			if (Functions.call(acceptor, map)) {
				Map<String, Object> newMap = Functions.call(transform, map);
				builder.append(Crypto.aesEncrypt(fileDescription.crypto(), Json.toString(newMap)) + "\n");
				count++;
			} else
				builder.append(line + "\n");
		}
		if (count > 0) {
			File file = fileDescription.getFile(getRoot());
			Files.setText(file, builder.toString());
			File findRepositoryFile = fileDescription.findRepositoryUrl(getRoot());
			String repositoryUrl = Files.offset(getRoot(), findRepositoryFile);
			if (repositoryUrl == null)
				throw new IllegalStateException(file.toString());
			addAllAndCommit(repositoryUrl, commitMessage);
		}
		return count;
	}

	@Override
	public void delete(IFileDescription fileDescription) {
		File file = fileDescription.getFile(root);
		if (file.exists()) {
			file.delete();
			File repositoryFile = fileDescription.findRepositoryUrl(root);
			String repositoryUrl = Files.offset(root, repositoryFile);
		}
	}

}