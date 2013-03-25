package org.softwarefm.eclipse.views;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.composite.CodeComposite;

public class MyCommentView extends SoftwareFmView<CodeComposite> {

	private CodeComposite codeComposite;

	@Override
	public CodeComposite makeSoftwareFmComposite(Composite parent, SoftwareFmContainer<?> container) {
		return codeComposite = new CodeComposite(parent, container);
	}

	public void setUrl(String url) {
		codeComposite.setUrl(url);
	}

}