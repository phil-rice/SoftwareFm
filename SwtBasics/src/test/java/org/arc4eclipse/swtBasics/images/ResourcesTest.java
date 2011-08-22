package org.arc4eclipse.swtBasics.images;

import junit.framework.TestCase;

import org.arc4eclipse.swtBasics.SwtBasicConstants;
import org.arc4eclipse.utilities.resources.IResourceGetter;

public class ResourcesTest extends TestCase {

	public void testBasic() {
		IResourceGetter basics = Resources.builderWithBasics().build();
		String expected = "Edit";
		assertEquals(expected, basics.getString(SwtBasicConstants.editKey + ".tooltip"));
		assertEquals(expected, Resources.getTooltip(basics, SwtBasicConstants.editKey));
	}

}
