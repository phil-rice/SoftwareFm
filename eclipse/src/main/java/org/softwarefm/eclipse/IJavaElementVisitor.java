package org.softwarefm.eclipse;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.core.BinaryMember;
import org.eclipse.jdt.internal.core.BinaryType;
import org.eclipse.jdt.internal.core.ClassFile;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.JavaModel;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.internal.core.SourceMethod;
import org.eclipse.jdt.internal.core.SourceType;

@SuppressWarnings("restriction")
public interface IJavaElementVisitor<T> {

	T from(JavaModel model);

	T from(JavaProject project);

	T from(PackageFragmentRoot packageFragmentRoot);

	T from(PackageFragment packageFragment);

	T from(CompilationUnit compilationUnit);

	T from(SourceType sourceType);

	T from(SourceMethod sourceMethod);

	T other(IJavaElement element);

	T from(ClassFile element);

	T from(BinaryType element);

	T from(BinaryMember element);

}
