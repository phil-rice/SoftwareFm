package org.softwarefm.eclipse.selection;

import org.softwarefm.eclipse.jdtBinding.ArtifactData;
import org.softwarefm.eclipse.selection.internal.SoftwareFmArtifactHtmlRipper;

public interface IArtifactHtmlRipper {

	ArtifactData rip(FileAndDigest fileAndDigest, String html);

	public static class Utils {
		public static IArtifactHtmlRipper ripper() {
			return new SoftwareFmArtifactHtmlRipper();
		}
	}
}
