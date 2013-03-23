package org.softwarefm.eclipse.annotations.internal;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.text.JavaWordFinder;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.texteditor.ITextEditor;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.url.HostOffsetAndUrl;
import org.softwarefm.core.url.IUrlStrategy;
import org.softwarefm.eclipse.SoftwareFmPlugin;
import org.softwarefm.eclipse.annotations.IJavaElementToUrl;
import org.softwarefm.utilities.exceptions.WrappedException;

@SuppressWarnings("restriction")
public class MarkerHyperlinkDetector extends AbstractHyperlinkDetector {

	private IUrlStrategy urlStrategy;

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		try {
			ITextEditor textEditor = (ITextEditor) getAdapter(ITextEditor.class);
			IJavaElement element = getSelectedJavaElement(region, textEditor);
			IJavaElementToUrl javaElementToUrl = SoftwareFmPlugin.getDefault().getJavaElementToUrl();
			String sfmId = javaElementToUrl.findUrl(element);
			CodeData codeData = new CodeData(sfmId);
			HostOffsetAndUrl classAndMethodUrl = getUlStrategy().classAndMethodUrl(codeData);
			if (classAndMethodUrl.url.equals("code:"))// TODO Bodge...code explicitly coded in
				return null;
			String url = classAndMethodUrl.getHttpHostAndUrl();
			IRegion wordRegion = JavaWordFinder.findWord(textViewer.getDocument(), region.getOffset());
			IWorkbenchPage activePage = textEditor.getSite().getWorkbenchWindow().getActivePage();
			return new IHyperlink[] { new MarkerHyperLink(element, wordRegion, activePage, url) };
		} catch (JavaModelException e) {
			throw WrappedException.wrap(e);
		}
	}

	private IUrlStrategy getUlStrategy() {
		return urlStrategy == null ? urlStrategy = SoftwareFmPlugin.getDefault().getContainer().urlStrategy : urlStrategy;

	}

	private IJavaElement getSelectedJavaElement(IRegion region, ITextEditor textEditor) throws JavaModelException {
		if (textEditor == null) {
			return null;
		}
		ITypeRoot typeRoot = JavaUI.getEditorInputTypeRoot(textEditor.getEditorInput());
		IJavaElement[] select = typeRoot.codeSelect(region.getOffset(), region.getLength());
		IJavaElement element;
		if ((select != null) && (select.length == 1)) {
			element = select[0];
		} else {
			element = null;
		}
		return element;
	}
}
