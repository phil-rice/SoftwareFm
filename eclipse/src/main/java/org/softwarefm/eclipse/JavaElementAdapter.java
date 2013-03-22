package org.softwarefm.eclipse;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.core.BinaryType;
import org.eclipse.jdt.internal.core.ClassFile;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.JavaModel;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.internal.core.ResolvedBinaryMethod;
import org.eclipse.jdt.internal.core.ResolvedBinaryType;
import org.eclipse.jdt.internal.core.SourceMethod;
import org.eclipse.jdt.internal.core.SourceType;

@SuppressWarnings("restriction")
public class JavaElementAdapter<T> implements IJavaElementVisitor<T> {

	@Override
	public T from(JavaModel model) {
		return null;
	}

	@Override
	public T from(JavaProject project) {
		return null;
	}

	@Override
	public T from(PackageFragment packageFragment) {
		return null;
	}

	@Override
	public T from(CompilationUnit compilationUnit) {
		return null;
	}

	@Override
	public T from(SourceType sourceType) {
		return null;
	}

	@Override
	public T from(SourceMethod sourceMethod) {
		return null;
	}

	@Override
	public T other(IJavaElement element) {
		return null;
	}

	@Override
	public T from(PackageFragmentRoot packageFragmentRoot) {
		return null;
	}

	@Override
	public T from(ClassFile element) {
		return null;
	}

	@Override
	public T from(ResolvedBinaryMethod method) {
		return null;
	}

	@Override
	public T from(ResolvedBinaryType type) {
		return null;
	}

	@Override
	public T from(BinaryType element) {
		return null;
	}

}
