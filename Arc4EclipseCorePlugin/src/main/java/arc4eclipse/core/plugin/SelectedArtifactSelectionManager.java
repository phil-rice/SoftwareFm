package arc4eclipse.core.plugin;

import java.util.List;

import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.panel.ISelectedBindingListener;
import org.arc4eclipse.panel.ISelectedBindingManager;
import org.arc4eclipse.utilities.collections.Lists;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

public class SelectedArtifactSelectionManager implements ISelectedBindingManager, ISelectionListener {
	private final IFunction1<IBinding, BindingRipperResult> bindingRipper;

	private final List<ISelectedBindingListener> listeners = Lists.newList();

	public SelectedArtifactSelectionManager(IFunction1<IBinding, BindingRipperResult> bindingRipper) {
		this.bindingRipper = bindingRipper;
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		try {
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
						if (root == null)
							System.out.println("CompilationUnit was null");
						else {
							ASTNode node = NodeFinder.perform(root, textSelection.getOffset(), textSelection.getLength());
							if (node instanceof Expression) {
								Expression expression = (Expression) node;
								ITypeBinding binding = expression.resolveTypeBinding();
								BindingRipperResult ripperResult = bindingRipper.apply(binding);
								fireListeners(ripperResult);
							}
							System.out.println("Node: " + node + "/" + node.getClass());
						}
					} else
						System.out.println("type was " + typeRoot.getClass() + "/" + typeRoot);
				}
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	private void fireListeners(BindingRipperResult ripperResult) {
		for (ISelectedBindingListener listener : listeners)
			listener.selectionOccured(ripperResult);
	}

	ITypeRoot getJavaInput(IEditorPart part) {
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
	public void addSelectedArtifactSelectionListener(ISelectedBindingListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeSelectedArtifactSelectionListener(ISelectedBindingListener listener) {
		listeners.remove(listener);
	}

}