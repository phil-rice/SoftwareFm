/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.core.selection;

import java.io.File;
import java.util.Map;

import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.usage.UsageStatData;

/**
 * When the user clicks on things in eclipse, these listeners are told about it.
 * <ul>
 * <li>The class / method will occur first.
 * <li>If the file was from a jar (for example java.lang.String is probably in rt.jar, a spring file may be in spring-core-3.0.5-RELEASE.jar), the digest of that jar will be found next.
 * <li>If the file was not in a jar, the notInAJarMethod will be called
 * <li>If softwareFm recognises the digest, the project determined method will be called
 * <li>If softwareFm didn't recognise the digest, the unknown digest method will be called
 * </ul>
 * 
 * <p>
 * There is no guarantee that the later methods will be called. (for example if the user makes another selection, the selection manager may decided to cancel this thread of execution
 * 
 * <p>
 * All of these methods will be called on the gui dispatch thread.
 * 
 * <p>
 * The selection count is so that execution can be aborted if a newer selection has been seen
 */
public interface ISelectedBindingListener {

	void codeSelectionOccured(int selectionCount, CodeData codeData);

	void notJavaElement(int selectionCount);

	void digestDetermined(int selectionCount, FileAndDigest fileAndDigest);

	void notInAJar(int selectionCount, File file);

	void artifactDetermined(int selectionCount, ArtifactData artifactData);

	void unknownDigest(int selectionCount, FileAndDigest fileAndDigest);

	void friendsArtifactUsage(ArtifactData artifactData, Map<FriendData, UsageStatData> friendsUsage);

	void friendsCodeUsage(CodeData codeData, Map<FriendData, UsageStatData> friendsUsage);

}