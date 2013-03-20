package org.softwarefm.eclipse.resources;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.softwarefm.eclipse.Jdts;

public abstract class FileAsJavaElementChangedAdapter extends ResourceAsFileChangedAdapter {
	@Override
	protected void fileChanged(IFile file) {
		IJavaElement javaElement = Jdts.getJavaElement(file.getFullPath());
		if (javaElement != null)
			javaElementChanged(javaElement);
	}

	abstract protected void javaElementChanged(IJavaElement javaElement);

}