/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jdtBinding.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.internal.core.ResolvedBinaryMethod;
import org.eclipse.jdt.internal.core.ResolvedBinaryType;
import org.eclipse.jdt.internal.core.ResolvedSourceMethod;
import org.eclipse.jdt.internal.core.ResolvedSourceType;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.jdtBinding.api.IBindingRipper;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.maps.Maps;

public class BindingRipper implements IBindingRipper {

	private final Map<IPath, String> cache = Maps.newMap();

	@SuppressWarnings("restriction")
	@Override
	public BindingRipperResult rip(Expression from, Map<String, Object> cargo) {
		try {
			if (from != null) {
				// ExpressionData expressionData = IExpressionCategoriser.Utils.categoriser().categorise(from);
				// System.out.println("BR: " + expressionData);

				IJavaElement javaElement = findJavaElement(from);
				if (javaElement != null) {
					IResource resource = javaElement.getResource();
					IPath javaElementPath = javaElement.getPath();
					final IPath path = resource == null ? javaElementPath : resource.getLocation();
					IJavaProject project = javaElement.getJavaProject();
					IPackageFragmentRoot root = project.findPackageFragmentRoot(javaElementPath);
					IPath attachmentPath = root == null ? null : root.getSourceAttachmentPath();
					// IJavaElement foundElement = project.findElement(javaElement.getPath());
					String digestAsHexString = path == null ? "" : Maps.findOrCreate(cache, path, new Callable<String>() {
						@Override
						public String call() throws Exception {
							File file = path.toFile();
							String result = Files.digestAsHexString(file);
							return result;
						}
					});
					IJavaElement parent = javaElement.getParent();
					if (from instanceof IMethodBinding) {// javaElement instanceof IMethod
						String packageName = ((IMethodBinding) from).getDeclaringClass().getPackage().getName();
						String className = javaElement.getParent().getElementName();
						String methodName = javaElement.getElementName();
						return new BindingRipperResult(from, project, javaElement, root, path, digestAsHexString, attachmentPath, packageName, className, methodName, cargo);
					} else if (from instanceof ITypeBinding) {// javaElement instanceof IClassFile
						String packageName = ((ITypeBinding) from).getPackage().getName();
						String className = javaElement.getElementName();
						return new BindingRipperResult(from, project, javaElement, root, path, digestAsHexString, attachmentPath, packageName, className, null, cargo);
					} else if (javaElement instanceof ResolvedSourceMethod) {
						ResolvedSourceMethod resolvedSourceMethod = (ResolvedSourceMethod) javaElement;
						IType classFile = resolvedSourceMethod.getDeclaringType();
						IPackageFragment packageFragment = classFile.getPackageFragment();
						String packageName = packageFragment.getElementName();
						String className = classFile.getElementName();
						String methodName = resolvedSourceMethod.getElementName();
						return new BindingRipperResult(from, project, javaElement, root, path, digestAsHexString, attachmentPath, packageName, className, methodName, cargo);

					} else if (javaElement instanceof ResolvedBinaryMethod) {
						ResolvedBinaryMethod resolvedBinaryMethod = (ResolvedBinaryMethod) javaElement;
						IType classFile = resolvedBinaryMethod.getDeclaringType();
						IPackageFragment packageFragment = classFile.getPackageFragment();
						String packageName = packageFragment.getElementName();
						String className = classFile.getElementName();
						String methodName = resolvedBinaryMethod.getElementName();
						return new BindingRipperResult(from, project, javaElement, root, path, digestAsHexString, attachmentPath, packageName, className, methodName, cargo);

					} else if (javaElement instanceof ResolvedBinaryType) {
						ResolvedBinaryType resolvedBinaryType = (ResolvedBinaryType) javaElement;
						IPackageFragment packageFragment = resolvedBinaryType.getPackageFragment();
						String packageName = packageFragment.getElementName();
						String className = resolvedBinaryType.getElementName();
						String methodName = null;
						return new BindingRipperResult(from, project, javaElement, root, path, digestAsHexString, attachmentPath, packageName, className, methodName, cargo);
					} else if (javaElement instanceof ResolvedSourceType) {
						ResolvedSourceType resolvedSourceType = (ResolvedSourceType) javaElement;
						IPackageFragment packageFragment = resolvedSourceType.getPackageFragment();
						String packageName = packageFragment.getElementName();
						String className = resolvedSourceType.getElementName();
						String methodName = null;
						return new BindingRipperResult(from, project, javaElement, root, path, digestAsHexString, attachmentPath, packageName, className, methodName, cargo);
					}
					return new BindingRipperResult(from, project, javaElement, root, path, digestAsHexString, attachmentPath, "", "", "", cargo);
				}
			}
			return new BindingRipperResult(from, null, null, null, null, null, null, "", "", "", cargo);
		} catch (JavaModelException e) {
			throw WrappedException.wrap(e);
		}
	}

	private IJavaElement findJavaElement(Expression from) {
		ASTNode parent = from.getParent();
		if (parent instanceof MethodInvocation) {
			IMethodBinding resolveMethodBinding = ((MethodInvocation) parent).resolveMethodBinding();
			IJavaElement result = resolveMethodBinding.getJavaElement();
			return result;
		} if (parent  instanceof MethodDeclaration){
			IMethodBinding methodBinding = ((MethodDeclaration) parent).resolveBinding();
			IJavaElement result = methodBinding.getJavaElement();
			return result;
		}
		ITypeBinding binding = from.resolveTypeBinding();
		IJavaElement javaElement = binding.getJavaElement();
		return javaElement;
	}

}