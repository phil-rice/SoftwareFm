package org.softwareFm.card.internal;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.display.swt.SwtIntegrationTest;

public class NameValueTest extends SwtIntegrationTest {

	public void testConstructor() {
		NameValue nameValue = new NameValue(shell, new CardConfig(), "name", "value");
		Control[] children = nameValue.content.getChildren();
		assertEquals(2, children.length);
		assertEquals("name", ((Label) children[0]).getText());
		assertEquals("value", ((Text) children[1]).getText());
	}
}
