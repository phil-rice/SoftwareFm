package org.softwarefm.helloannotations.annotations;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

public interface ISoftwareFmCompilationUnitVistor {

	void visitType(String sfmTypeId, IType type) throws Exception;

	void visitMethod(String sfmMethodId, IType type, IMethod method)throws Exception;

}
