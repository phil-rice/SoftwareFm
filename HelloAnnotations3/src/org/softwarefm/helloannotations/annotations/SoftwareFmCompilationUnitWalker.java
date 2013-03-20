package org.softwarefm.helloannotations.annotations;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.softwarefm.utilities.exceptions.WrappedException;

public class SoftwareFmCompilationUnitWalker {

	public static void visit(ICompilationUnit compilationUnit, ISoftwareFmCompilationUnitVistor visitor) {
		try {
			for (IType type : compilationUnit.getAllTypes()) {
				String sfmTypeId = type.getPackageFragment().getElementName() + "/" + type.getElementName();
				
				visitor.visitType(sfmTypeId, type);
				for (IMethod method : type.getMethods()) {
					String sfmMethodId = sfmTypeId + "/" + method.getElementName();
					visitor.visitMethod(sfmMethodId, type, method);
				}
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

}
