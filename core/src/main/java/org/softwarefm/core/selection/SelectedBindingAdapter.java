package org.softwarefm.core.selection;

import java.io.File;
import java.util.Map;

import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.utilities.events.IValid;

abstract public class SelectedBindingAdapter implements ISelectedBindingListener, IValid {

	public void codeSelectionOccured(int selectionCount, CodeData codeData) {
	}

	public void digestDetermined(int selectionCount, FileAndDigest fileAndDigest) {
	}

	public void notInAJar(int selectionCount, File file) {
	}

	public void artifactDetermined(int selectionCount, ArtifactData artifactData) {
	}

	public void unknownDigest(int selectionCount, FileAndDigest fileAndDigest) {
	}

	public void notJavaElement(int selectionCount) {
	}

	public void friendsArtifactUsage(ArtifactData artifactData, Map<FriendData, UsageStatData> friendsUsage) {
	}

	public void friendsCodeUsage(CodeData codeData, Map<FriendData, UsageStatData> friendsUsage) {
	}
}
