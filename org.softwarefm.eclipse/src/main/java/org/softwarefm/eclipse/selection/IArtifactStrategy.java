package org.softwarefm.eclipse.selection;

import org.softwarefm.eclipse.jdtBinding.ArtifactData;

public interface IArtifactStrategy<S> {
	ArtifactData findArtifact(S selection, FileAndDigest fileAndDigest, int selectionCount);
}
