package org.softwarefm.helloannotations;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.softwarefm.eclipse.Jdts;

public abstract class CompilationUnitChangedAdapter extends ResourceAsFileChangedAdapter {
	@Override
	protected void fileChanged(IFile file) {
		ICompilationUnit compilationUnit = Jdts.getCompilationUnit(file.getFullPath());
		if (compilationUnit != null)
			compilationUnitChanged(file, compilationUnit);
	}

	abstract protected void compilationUnitChanged(IFile file, ICompilationUnit compilationUnit);
}