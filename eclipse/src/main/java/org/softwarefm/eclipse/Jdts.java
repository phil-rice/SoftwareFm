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
import org.eclipse.jdt.internal.core.BinaryType;
import org.eclipse.jdt.internal.core.ClassFile;
import org.eclipse.jdt.internal.core.JavaModel;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.internal.core.ResolvedBinaryMethod;
import org.eclipse.jdt.internal.core.ResolvedBinaryType;
import org.eclipse.jdt.internal.core.SourceMethod;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.text.ITextSelection;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.functions.IFoldFunction;

@SuppressWarnings("restriction")
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

	public static String foldParents(IJavaElement javaElement, IFoldFunction<IJavaElement, String> iFoldFunction) {
		try {
			String accumulator = "";
			IJavaElement element = javaElement;
			while (element != null) {
				accumulator = iFoldFunction.apply(element, accumulator);
				element = element.getParent();
			}
			return accumulator;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static <T> T visitJavaElement(IJavaElement element, IJavaElementVisitor<T> visitor) {
		if (element instanceof JavaModel)
			return visitor.from((JavaModel) element);
		else if (element instanceof PackageFragmentRoot)
			return visitor.from((PackageFragmentRoot) element);
		else if (element instanceof PackageFragment)
			return visitor.from((PackageFragment) element);
		else if (element instanceof org.eclipse.jdt.internal.core.CompilationUnit)
			return visitor.from((org.eclipse.jdt.internal.core.CompilationUnit) element);
		else if (element instanceof SourceType)
			return visitor.from((SourceType) element);
		else if (element instanceof SourceMethod)
			return visitor.from((SourceMethod) element);
		else if (element instanceof ClassFile)
			return visitor.from((ClassFile) element);
		else if (element instanceof ResolvedBinaryMethod)
			return visitor.from((ResolvedBinaryMethod) element);
		else if (element instanceof ResolvedBinaryType)
			return visitor.from((ResolvedBinaryType) element);
		else if (element instanceof BinaryType)
			return visitor.from((BinaryType) element);
		else
			return visitor.other(element);

	}

	public static IJavaElementVisitor<String> elementName() {
		return new JavaElementAdapter<String>() {
			@Override
			public String from(PackageFragment packageFragment) {
				return name(packageFragment);
			}

			@Override
			public String from(SourceType sourceType) {
				return name(sourceType);
			}

			@Override
			public String from(SourceMethod method) {
				return name(method);
			}

			@Override
			public String from(ResolvedBinaryMethod method) {
				return name(method);
			}

			@Override
			public String from(BinaryType type) {
				return name(type);
			}

			@Override
			public String from(ResolvedBinaryType type) {
				return name(type);
			}

			private String name(IJavaElement javaElement) {
				return javaElement.getElementName();
			}

			@Override
			public String other(IJavaElement element) {
				return "[" + element.getElementName() + "/" + element.getClass().getSimpleName() + "]";
			}
		};
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
