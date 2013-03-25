package org.softwarefm.eclipse.views;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.composite.MyCodeComposite;

public class MyCodeView extends SoftwareFmView<MyCodeComposite> {

	private MyCodeComposite codeComposite;

	@Override
	public MyCodeComposite makeSoftwareFmComposite(Composite parent, SoftwareFmContainer<?> container) {
		return codeComposite = new MyCodeComposite(parent, container);
	}

	public void setUrl(String url) {
		codeComposite.setUrl(url);

	}

}
