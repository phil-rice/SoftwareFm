package org.softwarefm.eclipse.selection.internal;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.selection.FileAndDigest;
import org.softwarefm.core.selection.IArtifactStrategy;
import org.softwarefm.core.selection.ISelectedBindingStrategy;
import org.softwarefm.eclipse.Jdts;
import org.softwarefm.eclipse.annotations.IJavaElementToUrl;
import org.softwarefm.utilities.collections.Files;

/** This file hopefully contains all the eclipse specific stuff. */

public class EclipseSelectedBindingStrategy implements ISelectedBindingStrategy<ITextSelection, Expression> {

	private final IArtifactStrategy<ITextSelection> artifactStrategy;
	private final IJavaElementToUrl javaElementToUrl;

	public EclipseSelectedBindingStrategy(IArtifactStrategy<ITextSelection> artifactStrategy, IJavaElementToUrl javaElementToUrl) {
		this.artifactStrategy = artifactStrategy;
		this.javaElementToUrl = javaElementToUrl;
	}

	@Override
	public Expression findNode(ITextSelection textSelection, int selectionCount) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
		if (activeWorkbenchWindow != null) {
			IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
			if (activePage != null) {
				IEditorPart editor = activePage.getActiveEditor();
				ITypeRoot typeRoot = getJavaInput(editor);
				ICompilationUnit cu = (ICompilationUnit) typeRoot;
				return findExpressionFor(cu, textSelection);
			}
		}
		return null;
	}

	private Expression findExpressionFor(ITypeRoot typeRoot, ITextSelection textSelection) {
		CompilationUnit root = Jdts.findCompilationUnitFrom(typeRoot);
		if (root != null) {
			ASTNode node = Jdts.findAstNodeFor(textSelection, root);
			if (node instanceof Expression)
				return (Expression) node;
		}
		return null;
	}

	@Override
	public CodeData findExpressionData(ITextSelection selection, Expression expression, int selectionCount) {
		final IJavaElement javaElement = findJavaElement(expression);
		if (javaElement == null)
			return null;
		CodeData expressionData = findSimplifiedCodeData(javaElement, expression);
		return expressionData;
	}

	@Override
	public File findFile(ITextSelection selection, Expression expression, int selectionCount) {
		final IJavaElement javaElement = findJavaElement(expression);
		if (javaElement == null)
			return null;
		IResource resource = javaElement.getResource();
		IPath javaElementPath = javaElement.getPath();
		final IPath path = resource == null ? javaElementPath : resource.getLocation();
		File file = path.toFile();
		return file;
	}

	@Override
	public FileAndDigest findDigest(ITextSelection selection, Expression node, File file, int selectionCount) {
		String extension = Files.extension(file.getName());
		if ("jar".equals(extension)) {
			String digestAsHexString = Files.digestAsHexString(file);
			return new FileAndDigest(file, digestAsHexString);
		}
		return null;
	}

	// At this point we sigh in sadness about Java's weak class system, and regret the inability to modify Expression in anyway. "Clojure multimethods where are you now?"
	public CodeData findSimplifiedCodeData(IJavaElement javaElement, Expression expression) {
		String url = javaElementToUrl.findUrl(javaElement);
		if (url.length()>0)
			return new CodeData(url);
		return null;
	}

	@Override
	public ArtifactData findArtifact(ITextSelection selection, FileAndDigest fileAndDigest, int selectionCount) {
		return artifactStrategy.findArtifact(selection, fileAndDigest, selectionCount);
	}

	static ITypeRoot getJavaInput(IEditorPart part) {
		if (part != null) {
			IEditorInput editorInput = part.getEditorInput();
			if (editorInput != null) {
				IJavaElement input = JavaUI.getEditorInputJavaElement(editorInput);
				if (input instanceof ITypeRoot) {
					return (ITypeRoot) input;
				}
			}
		}
		return null;
	}

	private IJavaElement findJavaElement(Expression from) {
		try {
			ASTNode parent = from.getParent();
			if (parent instanceof MethodInvocation) {
				IMethodBinding resolveMethodBinding = ((MethodInvocation) parent).resolveMethodBinding();
				IJavaElement result = resolveMethodBinding.getJavaElement();
				return result;
			}
			if (parent instanceof MethodDeclaration) {
				IMethodBinding methodBinding = ((MethodDeclaration) parent).resolveBinding();
				IJavaElement result = methodBinding.getJavaElement();
				return result;
			}
			ITypeBinding binding = from.resolveTypeBinding();
			if (binding == null)
				return null;
			IJavaElement javaElement = binding.getJavaElement();
			return javaElement;
		} catch (Exception e) {
			// TODO Swalloes exception. Probably want to have a message sent
			e.printStackTrace();
			return null;
		}
	}

}
