package org.softwareFm.jdtBinding.api.impl;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.jdtBinding.api.IBindingRipper;
import org.softwareFm.jdtBinding.api.JavaProjects;
import org.softwareFm.jdtBinding.api.RippedResult;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.maps.Maps;

public class BindingRipper implements IBindingRipper {

	private final Map<IPath, String> cache = Maps.newMap();

	@Override
	public RippedResult rip(IBinding binding) {
		final BindingRipperResult ripped = rip(binding, Collections.<String, Object> emptyMap());
		String javadoc = JavaProjects.findJavadocFor(ripped.classpathEntry);
		String source = JavaProjects.findSourceFor(ripped.packageFragmentRoot);
		ICallback<String> sourceMutator = new ICallback<String>() {
			@Override
			public void process(String newValue) throws Exception {
				JavaProjects.setSourceAttachment(ripped.javaProject, ripped.classpathEntry, newValue);
			}
		};
		ICallback<String> javadocMutator = new ICallback<String>() {
			@Override
			public void process(String newValue) throws Exception {
				JavaProjects.setJavadoc(ripped.javaProject, ripped.classpathEntry, newValue);
			}
		};
		return new RippedResult(ripped.hexDigest, ripped.path.toOSString(),ripped.path.lastSegment().toString(), javadoc, source, javadocMutator, sourceMutator);
	}

	@Override
	public BindingRipperResult rip(IBinding from, Map<String, Object> cargo) {
		try {
			if (from != null) {
				IJavaElement javaElement = from.getJavaElement();
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

					if (javaElement instanceof IMethod) {
						String packageName = ((IMethodBinding) from).getDeclaringClass().getPackage().getName();
						String className = javaElement.getParent().getElementName();
						String methodName = javaElement.getElementName();
						return new BindingRipperResult(from, project, javaElement, root, path, digestAsHexString, attachmentPath, packageName, className, methodName, cargo);
					} else if (javaElement instanceof IClassFile) {
						String packageName = ((ITypeBinding) from).getPackage().getName();
						String className = javaElement.getElementName();
						return new BindingRipperResult(from, project, javaElement, root, path, digestAsHexString, attachmentPath, packageName, className, null, cargo);
					} else
						return new BindingRipperResult(from, project, javaElement, root, path, digestAsHexString, attachmentPath, "", "", "", cargo);
				}
			}
			return BindingRipperResult.empty;
		} catch (JavaModelException e) {
			throw WrappedException.wrap(e);
		}
	}

}
