package org.softwarefm.views;

public abstract class TextAndBrowserPanelTest<V extends SoftwareFmView<P>, P extends TextAndBrowserPanel> extends AbstractSoftwareFmPanelTest<V, P> {

	public void testTextAccessors() {
		panel.setText("someText\n");
		assertEquals("someText\n", panel.getText());
		panel.setText("someMoreText\n");
		assertEquals("someMoreText\n", panel.getText());
		panel.appendText("again");
		assertEquals("someMoreText\nagain", panel.getText());
		panel.killLastLineAndappendText("last");
		assertEquals("someMoreText\nlast", panel.getText());
	}

	public void testNotJavaElementClearsUrl(){
		panel.setUrl("someValue");
		panel.notJavaElement(1);
		assertEquals("", panel.getUrl());
	}
	
}
