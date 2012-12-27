package org.softwarefm.core.selection;

import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.selection.internal.SoftwareFmArtifactHtmlRipper;

public interface IArtifactHtmlRipper {

	ArtifactData rip(FileAndDigest fileAndDigest, String html);

	public static class Utils {
		public static IArtifactHtmlRipper ripper() {
			return new SoftwareFmArtifactHtmlRipper();
		}
	}
}
