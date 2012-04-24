package org.softwareFm.crowdsource.api.newGit.internal;

import java.io.File;
import java.util.Collections;

public class RepoDataPrimTest extends RepoTest {

	private final String repoa = "repoa";
	private final String repob = "repob";

	private final String url1 = "url1";
	private final String url2 = "url2";

	private File filea;
	private File fileb;
	private File filea_1;
	private File filea_2;
	private File fileb_1;
	private File fileb_2;

	public void testConstructor() {
		RepoPrim repoPrim = new RepoPrim(remoteRoot);
		assertEquals(remoteRoot, repoPrim.root);
		assertEquals(Collections.emptyMap(), repoPrim.repositories);
	}
	
	public void test

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		filea = new File(remoteRoot, repoa);
		fileb = new File(remoteRoot, repob);

		filea_1 = new File(filea, url1);
		filea_2 = new File(filea, url2);

		fileb_1 = new File(fileb, url1);
		fileb_2 = new File(fileb, url2);
	}

}
