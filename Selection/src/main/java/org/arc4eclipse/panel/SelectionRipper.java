package org.arc4eclipse.panel;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.utilities.collections.Files;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.maps.Maps;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.NodeFinder;

public class SelectionRipper {
	private final Map<IPath, String> cache = Maps.newMap();

	public Callable<BindingRipperResult> selectionChanged(final Callable<CompilationUnit> callable, final int offset, final int length) {
		return new Callable<BindingRipperResult>() {

			@Override
			public BindingRipperResult call() throws Exception {
				CompilationUnit root = callable.call();
				if (root != null) {
					ASTNode node = NodeFinder.perform(root, offset, length);
					if (node instanceof Expression) {
						Expression expression = (Expression) node;
						ITypeBinding binding = expression.resolveTypeBinding();
						BindingRipperResult ripperResult = ripBinding(binding);
						return ripperResult;
					}
				}

				return null;
			}
		};
	}

	public BindingRipperResult ripBinding(ITypeBinding binding) {
		try {
			IJavaElement javaElement = binding.getJavaElement();
			if (javaElement != null) {
				final IPath path = javaElement.getPath();
				IJavaProject project = javaElement.getJavaProject();
				IPackageFragmentRoot root = project.findPackageFragmentRoot(path);
				IPath attachmentPath = root == null ? null : root.getSourceAttachmentPath();
				String digestAsHexString = Maps.findOrCreate(cache, path, new Callable<String>() {
					@Override
					public String call() throws Exception {
						File file = path.toFile();
						String result = Files.digestAsHexString(file);
						return result;
					}
				});

				if (javaElement instanceof IMethod) {
					String packageName = ((IMethodBinding) binding).getDeclaringClass().getPackage().getName();
					String className = javaElement.getParent().getElementName();
					String methodName = javaElement.getElementName();
					return new BindingRipperResult(project, root, path, digestAsHexString, attachmentPath, packageName, className, methodName);
				} else if (javaElement instanceof IClassFile) {
					String packageName = binding.getPackage().getName();
					String className = javaElement.getElementName();
					return new BindingRipperResult(project, root, path, digestAsHexString, attachmentPath, packageName, className, null);
				} else
					return new BindingRipperResult(project, root, path, digestAsHexString, attachmentPath, "", "", "");
			}
			return null;
		} catch (JavaModelException e) {
			throw WrappedException.wrap(e);
		}
	}

}
