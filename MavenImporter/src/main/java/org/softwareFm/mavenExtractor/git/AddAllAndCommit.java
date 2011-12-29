package org.softwareFm.mavenExtractor.git;

import java.io.File;

import org.softwareFm.server.IGitFacard;
import org.softwareFm.server.ServerConstants;

public class AddAllAndCommit {
	public static void main(String[] args) {
		File home = new File(System.getProperty("user.home"));
		final File localRoot = new File(home, ".sfm_remote");
		final File jarRoot = new File(localRoot, "softwareFm/jars");
		IGitFacard facard = IGitFacard.Utils.makeFacard();
		for (File firstTwo : jarRoot.listFiles())
			for (File secondTwo : firstTwo.listFiles()) {
				File dotGit = new File(secondTwo, ServerConstants.DOT_GIT);
				if (dotGit.exists()) {
					System.out.println(secondTwo);
					facard.addAllAndCommit(firstTwo, secondTwo.getName(), "Auto");
				}
			}
	}
}
