package org.softwarefm.softwarefm.selection.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
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
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.eclipse.selection.IProjectStrategy;
import org.softwarefm.eclipse.selection.ISelectedBindingStrategy;
import org.softwarefm.utilities.collections.Files;
import org.softwarefm.utilities.maps.IHasCache;
import org.softwarefm.utilities.maps.Maps;

/** This file hopefully contains all the eclipse specific stuff. */

@SuppressWarnings("restriction")
public class EclipseSelectedBindingStrategy implements ISelectedBindingStrategy<ITextSelection, Expression>, IHasCache {

	private final IProjectStrategy<ITextSelection> projectStrategy;
	private final Map<IPath, String> cache = Maps.newMap();

	public EclipseSelectedBindingStrategy(IProjectStrategy<ITextSelection>  projectStrategy) {
		this.projectStrategy = projectStrategy;
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
				}
			}
		}
		return null;
	}

	@Override
	public ExpressionData findExpressionData(ITextSelection selection, Expression expression, int selectionCount) {
		final IJavaElement javaElement = findJavaElement(expression);
		if (javaElement == null)
			return null;
		ExpressionData expressionData = findSimplifiedExpressionData(javaElement, expression);
		return expressionData;
	}

	@Override
	public FileNameAndDigest findFileAndDigest(ITextSelection selection, Expression expression, int selectionCount) {
		final IJavaElement javaElement = findJavaElement(expression);
		if (javaElement == null)
			return new FileNameAndDigest(null, null);
		IResource resource = javaElement.getResource();
		IPath javaElementPath = javaElement.getPath();
		final IPath path = resource == null ? javaElementPath : resource.getLocation();
		File file = path.toFile();
		if (path != null && "jar".equals(path.getFileExtension())) {
			String digestAsHexString = Maps.findOrCreate(cache, path, new Callable<String>() {
				@Override
				public String call() throws Exception {
					File file = path.toFile();
					String result = Files.digestAsHexString(file);
					return result;
				}
			});
			return new FileNameAndDigest(file, digestAsHexString);
		}
		return new FileNameAndDigest(file, null);
	}

	// At this point we sigh in sadness about Java's weak class system, and regret the inability to modify Expression in anyway. Clojure multimethods where are you know
	public ExpressionData findSimplifiedExpressionData(IJavaElement javaElement, Expression expression) {
		if (expression instanceof IMethodBinding) {// javaElement instanceof IMethod
			String packageName = ((IMethodBinding) expression).getDeclaringClass().getPackage().getName();
			String className = javaElement.getParent().getElementName();
			String methodName = javaElement.getElementName();
			return new ExpressionData(packageName, className, methodName);
		} else if (expression instanceof ITypeBinding) {// javaElement instanceof IClassFile
			String packageName = ((ITypeBinding) expression).getPackage().getName();
			String className = javaElement.getElementName();
			return new ExpressionData(packageName, className);
		} else if (javaElement instanceof ResolvedSourceMethod) {
			ResolvedSourceMethod resolvedSourceMethod = (ResolvedSourceMethod) javaElement;
			IType classFile = resolvedSourceMethod.getDeclaringType();
			IPackageFragment packageFragment = classFile.getPackageFragment();
			String packageName = packageFragment.getElementName();
			String className = classFile.getElementName();
			String methodName = resolvedSourceMethod.getElementName();
			return new ExpressionData(packageName, className, methodName);

		} else if (javaElement instanceof ResolvedBinaryMethod) {
			ResolvedBinaryMethod resolvedBinaryMethod = (ResolvedBinaryMethod) javaElement;
			IType classFile = resolvedBinaryMethod.getDeclaringType();
			IPackageFragment packageFragment = classFile.getPackageFragment();
			String packageName = packageFragment.getElementName();
			String className = classFile.getElementName();
			String methodName = resolvedBinaryMethod.getElementName();
			return new ExpressionData(packageName, className, methodName);

		} else if (javaElement instanceof ResolvedBinaryType) {
			ResolvedBinaryType resolvedBinaryType = (ResolvedBinaryType) javaElement;
			IPackageFragment packageFragment = resolvedBinaryType.getPackageFragment();
			String packageName = packageFragment.getElementName();
			String className = resolvedBinaryType.getElementName();
			return new ExpressionData(packageName, className);
		} else if (javaElement instanceof ResolvedSourceType) {
			ResolvedSourceType resolvedSourceType = (ResolvedSourceType) javaElement;
			IPackageFragment packageFragment = resolvedSourceType.getPackageFragment();
			String packageName = packageFragment.getElementName();
			String className = resolvedSourceType.getElementName();
			return new ExpressionData(packageName, className);
		}
		return null;

	}

	@Override
	public ProjectData findProject(ITextSelection selection, FileNameAndDigest fileNameAndDigest, int selectionCount) {
		return projectStrategy.findProject(selection, fileNameAndDigest, selectionCount);
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
	}

	@Override
	public void clearCaches() {
		cache.clear();
	}

}
