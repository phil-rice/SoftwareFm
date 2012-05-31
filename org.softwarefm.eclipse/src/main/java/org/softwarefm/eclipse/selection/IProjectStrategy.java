package org.softwarefm.eclipse.selection;

import org.softwarefm.eclipse.jdtBinding.ProjectData;

public interface IProjectStrategy {
	ProjectData findProject(FileNameAndDigest fileNameAndDigest, int selectionCount);
}
