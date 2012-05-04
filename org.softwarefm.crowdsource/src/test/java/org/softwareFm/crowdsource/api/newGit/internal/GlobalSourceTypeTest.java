package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.Arrays;

public class GlobalSourceTypeTest extends AbstractSourceTypeTest{

	public void testMakesRawSingleSource() {
		GlobalSourceType globalSourceType = new GlobalSourceType();
		assertEquals(Arrays.asList(new RawSingleSource("rl/file")), globalSourceType.makeSourcesFor(linkedRepoData, "rl", "file", "who", "care", "unused"));
	}

}
