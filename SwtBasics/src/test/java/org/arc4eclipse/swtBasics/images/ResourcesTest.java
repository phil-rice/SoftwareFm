package org.arc4eclipse.swtBasics.images;

import junit.framework.TestCase;

import org.arc4eclipse.swtBasics.SwtBasicConstants;
import org.arc4eclipse.utilities.resources.IResourceGetter;

public class ResourcesTest extends TestCase {

	public void testBasic() {
		IResourceGetter basics = Resources.resourceGetterWithBasics();
		String expected = "Edit";
		assertEquals(expected, basics.getStringOrNull(SwtBasicConstants.key + ".tooltip"));
		assertEquals(expected, Resources.getTooltip(basics, SwtBasicConstants.key));
	}

}
