package org.softwarefm.loadtest;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.softwareFm.server.IGitServer;

public class GitPullTimedTest {
	public static void main(String[] args) {
		File root = new File(System.getProperty("user.home"));
		File sfmLocalRoot = new File(root, ".sfm");
		final IGitServer localServer = IGitServer.Utils.gitServer(sfmLocalRoot, "git://www.softwarefm.com/");
		final List<String> urls = Arrays.asList(//
				"/softwareFm/data/org/apache/maven/archetype/org.apache.maven.archetype/artifact/archetype-catalog",//
				"/softwareFm/data/org/apache/maven/archetype/org.apache.maven.archetype/artifact/archetype-common",//
				"/softwareFm/data/org/apache/maven/archetype/org.apache.maven.archetype/artifact/archetype-descriptor",//
				"/softwareFm/data/org/apache/maven/archetype/org.apache.maven.archetype/artifact/archetype-packaging",//
				"/softwareFm/data/org/apache/maven/archetype/org.apache.maven.archetype/artifact/archetype-proxy",//
				"/softwareFm/data/org/apache/maven/archetype/org.apache.maven.archetype/artifact/archetype-registry",//
				"/softwareFm/data/org/apache/maven/archetype/org.apache.maven.archetype/artifact/archetype-repository",//
				"/softwareFm/data/org/apache/cocoon/org.apache.cocoon/artifact/cocoon-22-archetype-block",//
				"/softwareFm/data/org/apache/cocoon/org.apache.cocoon/artifact/cocoon-22-archetype-block-plain",//
				"/softwareFm/data/org/apache/cocoon/org.apache.cocoon/artifact/cocoon-22-archetype-webapp",//
				"/softwareFm/data/org/acegisecurity/org.acegisecurity/artifact/acegi-security");

		MultiThreadedTimeTester<Object> tester = new MultiThreadedTimeTester<Object>(1, 10, new ITimable<Object>() {
			@Override
			public Object start(int thread) {
//				try {
//					localServer.clone(urls.get(thread));
//				} catch (Exception e) {
//				}
				return null;
			}

			@Override
			public void execute(Object context, int thread, int index) throws Exception {
				localServer.pull(urls.get(thread));
			}

			@Override
			public void finished(Object context, int thread) {
			}
		});
		System.out.println("Starting");
		tester.testMe();
		tester.dumpStats();

	}
}
