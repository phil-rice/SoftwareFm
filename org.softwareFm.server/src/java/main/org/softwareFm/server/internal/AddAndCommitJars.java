package org.softwareFm.server.internal;

import java.io.File;

import org.softwareFm.server.IGitFacard;
import org.softwareFm.utilities.collections.Files;

public class AddAndCommitJars {

	public static void main(String[] args) {
		File root = new File(System.getProperty("user.home"));
		File sfmRoot = new File(root, ".sfm_remote");
		File jarRoot = new File(sfmRoot, "softwareFm/jars");
		IGitFacard git = IGitFacard.Utils.makeFacard();
		for (File firstTwo : Files.listChildDirectories(jarRoot)) {
			for (File secondTwo : Files.listChildDirectories(firstTwo)) {
				String url = firstTwo.getName() + "/" + secondTwo.getName();
				git.addAllAndCommit(jarRoot, url, "Fixing error with initial create");
				System.out.println(url);
			}
		}
	}
}
