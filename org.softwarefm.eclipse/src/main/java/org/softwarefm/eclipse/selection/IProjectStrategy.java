package org.softwarefm.eclipse.selection;

import org.softwarefm.eclipse.jdtBinding.ProjectData;

public interface IProjectStrategy<S> {
	ProjectData findProject(S selection, FileAndDigest fileAndDigest, int selectionCount);
}
