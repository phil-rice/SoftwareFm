package org.softwareFm.server.internal;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitOperations;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.CommonMessages;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.url.Urls;

public class GitOperations implements IGitOperations {

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
				File file = IFileDescription.Utils.findRepositoryUrl(root, url);
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
		File repositoryFile = fileDescription.findRepositoryUrl(root);
		String url = Files.offset(root, repositoryFile);
		addAllAndCommit(url, GitOperations.class.getSimpleName());
		
	}
	
	@Override
	public void pull(String url) {
		useFileRepository(url, new IFunction1<FileRepository, Void>() {
			@Override
			public Void apply(FileRepository fileRepository) throws Exception {
				PullCommand pull = new Git(fileRepository).pull();
				pull.call();
				return null;
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
		File directory = fileDescription.getDirectory(root);
		if (directory.exists()) {
			File file = fileDescription.getFile(root);
			if (file.exists()) {
				String text = Files.getText(file);
				Map<String, Object> map = fileDescription.decode(text);
				return map;
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

}
