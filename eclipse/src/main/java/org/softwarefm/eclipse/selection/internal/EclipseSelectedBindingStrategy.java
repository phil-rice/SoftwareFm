package org.softwarefm.eclipse.selection.internal;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.internal.core.ResolvedBinaryMethod;
import org.eclipse.jdt.internal.core.ResolvedBinaryType;
import org.eclipse.jdt.internal.core.ResolvedSourceMethod;
import org.eclipse.jdt.internal.core.ResolvedSourceType;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.SharedASTProvider;
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
import org.softwarefm.utilities.collections.Files;
import org.softwarefm.utilities.exceptions.WrappedException;

/** This file hopefully contains all the eclipse specific stuff. */

@SuppressWarnings("restriction")
public class EclipseSelectedBindingStrategy implements ISelectedBindingStrategy<ITextSelection, Expression> {

	private final IArtifactStrategy<ITextSelection> artifactStrategy;

	public EclipseSelectedBindingStrategy(IArtifactStrategy<ITextSelection> artifactStrategy) {
		this.artifactStrategy = artifactStrategy;
	}

	@Override
	public Expression findNode(ITextSelection textSelection, int selectionCount) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		System.out.println("IWorkbench: " + workbench);
		IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
		System.out.println("IWorkbenchWindow: " + activeWorkbenchWindow);
		if (activeWorkbenchWindow != null) {
			IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
			System.out.println("IWorkbenchPage: " + activePage);
			if (activePage != null) {
				IEditorPart editor = activePage.getActiveEditor();
				ITypeRoot typeRoot = getJavaInput(editor);
				if (typeRoot instanceof ICompilationUnit) {
					ICompilationUnit cu = (ICompilationUnit) typeRoot;
					CompilationUnit root = SharedASTProvider.getAST(cu, SharedASTProvider.WAIT_NO, null);
					if (root != null) {
						ASTNode node = NodeFinder.perform(root, textSelection.getOffset(), textSelection.getLength());
						if (node instanceof Expression)
							return (Expression) node;
					}
				} else if (typeRoot instanceof IClassFile) {
					IClassFile cf = (IClassFile) typeRoot;
					try {
						IJavaElement elementAt = cf.getElementAt(textSelection.getOffset());
						System.out.println(elementAt);
					} catch (JavaModelException e) {
						throw WrappedException.wrap(e);
					}
					CompilationUnit root = SharedASTProvider.getAST(cf, SharedASTProvider.WAIT_NO, null);
					if (root != null) {
						ASTNode node = NodeFinder.perform(root, textSelection.getOffset(), textSelection.getLength());
						if (node instanceof Expression)
							return (Expression) node;
					}
				}
			}
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

	// At this point we sigh in sadness about Java's weak class system, and regret the inability to modify Expression in anyway. Clojure multimethods where are you know
	public CodeData findSimplifiedCodeData(IJavaElement javaElement, Expression expression) {
		if (expression instanceof IMethodBinding) {// javaElement instanceof IMethod
			String packageName = ((IMethodBinding) expression).getDeclaringClass().getPackage().getName();
			String className = javaElement.getParent().getElementName();
			String methodName = javaElement.getElementName();
			return new CodeData(packageName, className, methodName);
		} else if (expression instanceof ITypeBinding) {// javaElement instanceof IClassFile
			String packageName = ((ITypeBinding) expression).getPackage().getName();
			String className = javaElement.getElementName();
			return new CodeData(packageName, className);
		} else if (javaElement instanceof ResolvedSourceMethod) {
			ResolvedSourceMethod resolvedSourceMethod = (ResolvedSourceMethod) javaElement;
			IType classFile = resolvedSourceMethod.getDeclaringType();
			IPackageFragment packageFragment = classFile.getPackageFragment();
			String packageName = packageFragment.getElementName();
			String className = classFile.getElementName();
			String methodName = resolvedSourceMethod.getElementName();
			return new CodeData(packageName, className, methodName);

		} else if (javaElement instanceof ResolvedBinaryMethod) {
			ResolvedBinaryMethod resolvedBinaryMethod = (ResolvedBinaryMethod) javaElement;
			IType classFile = resolvedBinaryMethod.getDeclaringType();
			IPackageFragment packageFragment = classFile.getPackageFragment();
			String packageName = packageFragment.getElementName();
			String className = classFile.getElementName();
			String methodName = resolvedBinaryMethod.getElementName();
			return new CodeData(packageName, className, methodName);

		} else if (javaElement instanceof ResolvedBinaryType) {
			ResolvedBinaryType resolvedBinaryType = (ResolvedBinaryType) javaElement;
			IPackageFragment packageFragment = resolvedBinaryType.getPackageFragment();
			String packageName = packageFragment.getElementName();
			String className = resolvedBinaryType.getElementName();
			return new CodeData(packageName, className);
		} else if (javaElement instanceof ResolvedSourceType) {
			ResolvedSourceType resolvedSourceType = (ResolvedSourceType) javaElement;
			IPackageFragment packageFragment = resolvedSourceType.getPackageFragment();
			String packageName = packageFragment.getElementName();
			String className = resolvedSourceType.getElementName();
			return new CodeData(packageName, className);
		}
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
