package org.softwarefm.eclipse.annotations.internal;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.softwarefm.eclipse.views.CodeView;
import org.softwarefm.utilities.exceptions.WrappedException;

public class MarkerHyperLink implements IHyperlink {

	private final IRegion wordRegion;
	private final IWorkbenchPage activePage;
	private final String url;
	private final String link;

	public MarkerHyperLink(IJavaElement element, IRegion wordRegion, IWorkbenchPage activePage, String url, String link) {
		this.wordRegion = wordRegion;
		this.activePage = activePage;
		this.url = url;
		this.link = link;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return wordRegion;
	}

	@Override
	public void open() {
		try {
			System.out.println("Opening: " + url);
			IViewPart view = activePage.showView("org.softwarefm.eclipse.views.ClassAndMethodView");
			if (url != null && view instanceof CodeView)
				((CodeView)view).setUrl(url);
			
			
		} catch (PartInitException e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public String getTypeLabel() {
		return null;
	}

	@Override
	public String getHyperlinkText() {
		return  link;
	}

}
