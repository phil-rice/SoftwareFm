package org.softwareFm.swtBasics.images;

import junit.framework.TestCase;

import org.softwareFm.utilities.resources.IResourceGetter;

public class ResourcesTest extends TestCase {

	public void testBasic() {
		IResourceGetter basics = Resources.resourceGetterWithBasics();
		String expected = "Edit";
		assertEquals(expected, basics.getStringOrNull("edit.tooltip"));
		assertEquals(expected, Resources.getTooltip(basics, "edit"));
	}

}
