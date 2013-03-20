package org.softwarefm.helloannotations;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.softwarefm.utilities.exceptions.WrappedException;


public abstract class ResourceAsFileChangedAdapter implements IResourceChangeListener {
	
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta rootDelta = event.getDelta();
		try {
			rootDelta.accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta) {
					// only interested in changed resources (not added or removed)
					if (delta.getKind() != IResourceDelta.CHANGED)
						return true;
					// only interested in content changes
					if ((delta.getFlags() & IResourceDelta.CONTENT) == 0)
						return true;
					final IResource resource = delta.getResource();
					// only interested in files with the "txt" extension
					if (resource instanceof IFile) 
						fileChanged((IFile) resource);
					return true;
				}

			});
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}
	abstract protected void fileChanged(IFile file);
}