package org.softwarefm.eclipse.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.UrlConstants;
import org.softwarefm.eclipse.jdtBinding.CodeData;
import org.softwarefm.eclipse.selection.SelectedBindingAdapter;

public class CodeComposite extends SoftwareFmComposite {

	private final Browser browser;
	private String url;

	public CodeComposite(Composite parent, final SoftwareFmContainer<?> container) {
		super(parent, container);
		getComposite().setLayout(new FillLayout());
		browser = new Browser(getComposite(), SWT.NULL);
		addListener(new SelectedBindingAdapter() {
			@Override
			public void codeSelectionOccured(CodeData codeData, int selectionCount) {
				url = container.urlStrategy.classAndMethodUrl(codeData).getHostAndUrl();
				System.out.println("ClassAndMethod: " + url);
				browser.setUrl(url);
			}

			public boolean invalid() {
				return getComposite().isDisposed();
			}
			
			@Override
			public String toString() {
				CodeComposite codeComposite = CodeComposite.this;
				return codeComposite.getClass().getSimpleName() +"@" + System.identityHashCode(codeComposite) +" Browser: " + browser.isDisposed() + " Url: " + url;
			}

		});
		browser.setUrl(UrlConstants.aboutCodeComposite);
	}

	@Override
	public void dispose() {
		System.out.println("CodeComposite dispose called");
		super.dispose();
	}

	public String getUrl() {
		return url;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() +" (" + getUrl() + ") " + browser;
	}
}
