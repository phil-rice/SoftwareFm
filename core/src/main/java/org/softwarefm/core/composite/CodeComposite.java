package org.softwarefm.core.composite;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.constants.UrlConstants;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.url.HostOffsetAndUrl;

public class CodeComposite extends AbstractCodeComposite {

	public CodeComposite(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container,UrlConstants.aboutCodeComposite);
	}

	@Override
	protected HostOffsetAndUrl getClassAndMethodUrl(SoftwareFmContainer<?> container, CodeData codeData) {
		return container.urlStrategy.classAndMethodUrl(codeData);
	}
}
