/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.eclipse.selection;

import java.io.File;

import org.softwarefm.eclipse.jdtBinding.ArtifactData;
import org.softwarefm.eclipse.jdtBinding.CodeData;

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
	
	/** If returns true, should be removed from the listener list */
	boolean invalid();

	void codeSelectionOccured(CodeData codeData, int selectionCount);

	void notJavaElement(int selectionCount);

	void digestDetermined(FileAndDigest fileAndDigest, int selectionCount);

	void notInAJar(File file, int selectionCount);

	void artifactDetermined(ArtifactData artifactData, int selectionCount);

	void unknownDigest(FileAndDigest fileAndDigest, int selectionCount);

}