package org.softwarefm.core.selection;

import org.softwarefm.core.jdtBinding.ArtifactData;

public interface IArtifactStrategy<S> {
	ArtifactData findArtifact(S selection, FileAndDigest fileAndDigest, int selectionCount);
}
