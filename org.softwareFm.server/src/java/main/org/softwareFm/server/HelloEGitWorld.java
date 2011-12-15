package org.softwareFm.server;

import java.io.File;

import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class HelloEGitWorld {
	public static void main(String[] args) throws Exception {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File("/my/git/directory")).readEnvironment() // scan environment GIT_* variables
				.findGitDir() // scan up the file system tree
				.build();
		Git git = new Git(repository);
		CommitCommand commit = git.commit();
		commit.setMessage("initial commit").call();
		RevCommit revComment = git.commit().setMessage("initial commit").call();
		RevTag tag = git.tag().setName("tag").call();
		PushCommand push = git.push();
	}
}
