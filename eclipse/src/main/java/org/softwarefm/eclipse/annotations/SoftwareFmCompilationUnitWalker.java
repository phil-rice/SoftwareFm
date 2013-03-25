package org.softwarefm.eclipse.annotations;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.softwarefm.utilities.exceptions.WrappedException;

public class SoftwareFmCompilationUnitWalker {

	public static void visit(ICompilationUnit compilationUnit, ISoftwareFmCompilationUnitVistor visitor, IJavaElementToUrl javaElementToUrl) {
		try {
			for (IType type : compilationUnit.getAllTypes()) {
				String sfmTypeId = javaElementToUrl.findUrl(type);
				
				visitor.visitType(sfmTypeId, type);
				for (IMethod method : type.getMethods()) {
					String sfmMethodId =  javaElementToUrl.findUrl(method);
					visitor.visitMethod(sfmMethodId, type, method);
				}
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

}
