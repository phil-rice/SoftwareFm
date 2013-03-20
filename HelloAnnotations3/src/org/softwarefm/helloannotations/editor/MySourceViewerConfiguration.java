package org.softwarefm.helloannotations.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.softwarefm.helloannotations.MyPlugin;

public class MySourceViewerConfiguration extends SourceViewerConfiguration {

	private MyCodeScanner scanner;

	@Override
	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
		return MyPlugin.MY_PARTITIONING;
	}
	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		ITextHover textHover = super.getTextHover(sourceViewer, contentType);
		System.out.println("looking for text hover: " + textHover);
		return textHover;
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE, MyPartitionScanner.MULTILINE_COMMENT, MyPartitionScanner.SINGLELINE_COMMENT, MyPartitionScanner.STRING };
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		DefaultDamagerRepairer repairer = new DefaultDamagerRepairer(getTagScanner());
		reconciler.setDamager(repairer, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(repairer, IDocument.DEFAULT_CONTENT_TYPE);
		return reconciler;
	}

	private ITokenScanner getTagScanner() {
		if (scanner == null) {
			scanner = new MyCodeScanner(new MyColorProvider());
		}
		return scanner;
	}

	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		MonoReconciler reconciler = new MonoReconciler(new MyReconcilingStrategy(), true);
		reconciler.install(sourceViewer);
		return reconciler;

	}
}
