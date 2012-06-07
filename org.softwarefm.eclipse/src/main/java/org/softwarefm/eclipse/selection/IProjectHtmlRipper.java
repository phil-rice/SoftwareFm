package org.softwarefm.eclipse.selection;

import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.internal.SoftwareFmProjectHtmlRipper;

public interface IProjectHtmlRipper {

	ProjectData rip(FileNameAndDigest fileNameAndDigest, String html);

	public static class Utils {
		public static IProjectHtmlRipper ripper() {
			return new SoftwareFmProjectHtmlRipper();
		}
	}
}
