package org.arc4eclipse.selectedartefactplugin.views;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.displayCore.api.IDisplayManager;
import org.arc4eclipse.panel.SelectedArtefactPanel;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import selectedartefactplugin.Activator;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained from the model. The sample creates a dummy model on the fly, but a real implementation would connect to the model available either in this or another plug-in (e.g. the workspace). The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each view can present the same model objects using different labels and icons, if needed. Alternatively, a single label provider can be shared between views in order to ensure that objects of the same type are presented in the same way everywhere.
 * <p>
 */

public class SampleView extends ViewPart {

	public static final String ID = "org.arc4eclipse.selectedartefactplugin.views.SampleView";
	private ISelectionListener listener;

	public SampleView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		IArc4EclipseRepository repository = Activator.getDefault().getRepository();
		final SelectedArtefactPanel artefactPanel = new SelectedArtefactPanel(parent, SWT.NULL, IDisplayManager.Utils.displayManager(), repository, Activator.getDefault().getBindingRipper());
		listener = new ISelectionListener() {
			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				System.out.println(selection.getClass() + " " + selection);
				if (selection instanceof ITextSelection) {
					ITextSelection textSelection = (ITextSelection) selection;
					IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					if (activePage != null) {
						IEditorPart editor = activePage.getActiveEditor();
						ITypeRoot typeRoot = getJavaInput(editor);
						if (typeRoot instanceof ICompilationUnit) {
							ICompilationUnit cu = (ICompilationUnit) typeRoot;
							CompilationUnit root = SharedASTProvider.getAST(cu, SharedASTProvider.WAIT_NO, null);
							ASTNode node = NodeFinder.perform(root, textSelection.getOffset(), textSelection.getLength());

							if (node instanceof Expression) {
								Expression expression = (Expression) node;
								ITypeBinding binding = expression.resolveTypeBinding();
								artefactPanel.setData(binding);
							}
							System.out.println("Node: " + node + "/" + node.getClass());
						} else
							System.out.println("typeWas " + typeRoot.getClass() + "/" + typeRoot);
					}
				}
			}
		};
		getSite().getPage().addSelectionListener(listener);
	}

	private ITypeRoot getJavaInput(IEditorPart part) {
		IEditorInput editorInput = part.getEditorInput();
		if (editorInput != null) {
			IJavaElement input = JavaUI.getEditorInputJavaElement(editorInput);
			if (input instanceof ITypeRoot) {
				return (ITypeRoot) input;
			}
		}
		return null;
	}

	@Override
	public void dispose() {
		super.dispose();
		getSite().getPage().removeSelectionListener(listener);
	}

	@Override
	public void setFocus() {

	}

}