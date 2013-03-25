package org.softwarefm.eclipse.annotations;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.internal.core.ClassFile;
import org.softwarefm.utilities.exceptions.WrappedException;

@SuppressWarnings("restriction")
public class TypeRootWalker {

	public static void visit(ITypeRoot typeRoot, ITypeRootVistor visitor, IJavaElementToUrl javaElementToUrl) {
		if (typeRoot instanceof ICompilationUnit)
			visit((ICompilationUnit) typeRoot, visitor, javaElementToUrl);
		else if (typeRoot instanceof ClassFile)
			visit((ClassFile) typeRoot, visitor, javaElementToUrl);

	}

	public static void visit(ICompilationUnit compilationUnit, ITypeRootVistor visitor, IJavaElementToUrl javaElementToUrl) {
		try {
			for (IType type : compilationUnit.getAllTypes()) {
				String sfmTypeId = javaElementToUrl.findUrl(type);

				visitor.visitType(sfmTypeId, type);
				for (IMethod method : type.getMethods()) {
					String sfmMethodId = javaElementToUrl.findUrl(method);
					visitor.visitMethod(sfmMethodId, type, method);
				}
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static void visit(ClassFile classFile, ITypeRootVistor visitor, IJavaElementToUrl javaElementToUrl) {
		try {
			IJavaElement primaryElement = classFile.getType();
			IResource resource1 = primaryElement.getResource();
			IResource resource2 = classFile.getResource();
			String sfmTypeId = javaElementToUrl.findUrl(primaryElement);
			if (primaryElement instanceof IType) {
				IType type = (IType) primaryElement;
				visitor.visitType(sfmTypeId, type);
				for (IMethod method : type.getMethods()) {
					String sfmMethodId = javaElementToUrl.findUrl(method);
					visitor.visitMethod(sfmMethodId, type, method);
				}
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}
}
