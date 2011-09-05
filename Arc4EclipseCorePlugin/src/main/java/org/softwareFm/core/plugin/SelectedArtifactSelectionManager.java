package org.softwareFm.core.plugin;

import java.util.List;
import java.util.Map;

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
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.jdtBinding.api.IBindingRipper;
import org.softwareFm.panel.ISelectedBindingListener;
import org.softwareFm.panel.ISelectedBindingManager;
import org.softwareFm.panel.SelectionConstants;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.maps.Maps;

public class SelectedArtifactSelectionManager implements ISelectedBindingManager, ISelectionListener {
	private final IBindingRipper bindingRipper;

	private final List<ISelectedBindingListener> listeners = Lists.newList();

	public SelectedArtifactSelectionManager(IBindingRipper bindingRipper) {
		this.bindingRipper = bindingRipper;
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		System.out.println(selection.getClass() + " " + selection);
		if (selection instanceof ITextSelection) {
			ITextSelection textSelection = (ITextSelection) selection;
			BindingRipperResult ripperResult = selectToBindingResult(bindingRipper, textSelection);
			if (ripperResult != null)
				fireListeners(ripperResult);
		}
	}

	public static BindingRipperResult reRip(BindingRipperResult result) {
		if (result == null)
			return null;
		Map<String, Object> cargo = result.cargo;
		TextSelection selection = (TextSelection) cargo.get(SelectionConstants.selectionKey);
		IBindingRipper ripper = (IBindingRipper) cargo.get(SelectionConstants.ripperKey);
		return selectToBindingResult(ripper, selection);
	}

	public static BindingRipperResult selectToBindingResult(IBindingRipper ripper, ITextSelection textSelection) {
		try {
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (activePage != null) {
				IEditorPart editor = activePage.getActiveEditor();
				ITypeRoot typeRoot = getJavaInput(editor);
				if (typeRoot instanceof ICompilationUnit) {
					ICompilationUnit cu = (ICompilationUnit) typeRoot;
					CompilationUnit root = SharedASTProvider.getAST(cu, SharedASTProvider.WAIT_NO, null);
					if (root != null) {
						ASTNode node = NodeFinder.perform(root, textSelection.getOffset(), textSelection.getLength());
						if (node instanceof Expression) {
							Expression expression = (Expression) node;
							ITypeBinding binding = expression.resolveTypeBinding();
							BindingRipperResult ripperResult = ripper.rip(binding, Maps.<String, Object> makeMap(//
									SelectionConstants.selectionKey, textSelection,//
									SelectionConstants.ripperKey, ripper));
							return ripperResult;
						}
					}
				}
			}
			return null;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	private void fireListeners(BindingRipperResult ripperResult) {
		for (ISelectedBindingListener listener : listeners)
			listener.selectionOccured(ripperResult);
	}

	static ITypeRoot getJavaInput(IEditorPart part) {
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