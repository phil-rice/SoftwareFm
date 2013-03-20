package org.softwarefm.eclipse;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.text.ITextSelection;

public class Jdts {
	public static ICompilationUnit getCompilationUnit(IPath path) {
		IJavaElement javaElement = getJavaElement(path);
		if (!(javaElement instanceof ICompilationUnit))
			return null;
		return (ICompilationUnit) javaElement;
	}

	public static IJavaElement getJavaElement(IPath path) {
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		if (resource == null)
			return null;

		IJavaElement javaElement = JavaCore.create(resource);
		if (javaElement == null)
			return null;
		return javaElement;
	}

	public static CompilationUnit findCompilationUnitFrom(ITypeRoot compilationUnit) {
		CompilationUnit root = SharedASTProvider.getAST(compilationUnit, SharedASTProvider.WAIT_NO, null);
		return root;
	}

	public static ASTNode findAstNodeFor(ASTNode root, int offset, int length) {
		ASTNode node = NodeFinder.perform(root, offset, length);
		return node;
	}

	public static ASTNode findAstNodeFor(ITextSelection textSelection, CompilationUnit root) {
		ASTNode node = Jdts.findAstNodeFor(root, textSelection.getOffset(), textSelection.getLength());
		return node;
	}

}
