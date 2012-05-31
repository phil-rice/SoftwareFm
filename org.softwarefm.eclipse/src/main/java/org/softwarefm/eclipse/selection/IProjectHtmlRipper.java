package org.softwarefm.eclipse.selection;

import org.softwarefm.eclipse.jdtBinding.ProjectData;

public interface IProjectHtmlRipper {

	ProjectData rip(FileNameAndDigest fileNameAndDigest, String html);
}
