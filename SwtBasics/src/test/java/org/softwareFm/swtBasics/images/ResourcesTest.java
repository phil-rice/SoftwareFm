package org.softwareFm.swtBasics.images;

import junit.framework.TestCase;

import org.softwareFm.swtBasics.SwtBasicConstants;
import org.softwareFm.utilities.resources.IResourceGetter;

public class ResourcesTest extends TestCase {

	public void testBasic() {
		IResourceGetter basics = Resources.resourceGetterWithBasics();
		String expected = "Edit";
		assertEquals(expected, basics.getStringOrNull(SwtBasicConstants.editKey + ".tooltip"));
		assertEquals(expected, Resources.getTooltip(basics, SwtBasicConstants.editKey));
	}

}
