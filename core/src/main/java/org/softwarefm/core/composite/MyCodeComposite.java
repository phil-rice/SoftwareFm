package org.softwarefm.core.composite;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.constants.UrlConstants;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.url.HostOffsetAndUrl;

public class MyCodeComposite extends AbstractCodeComposite {

	public MyCodeComposite(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container,UrlConstants.aboutMyCodeComposite);
	}

	@Override
	protected HostOffsetAndUrl getClassAndMethodUrl(SoftwareFmContainer<?> container, CodeData codeData) {
		String myName = container.socialManager.myName();
		return container.urlStrategy.myCodeUrl(myName, codeData);
	}
}
