/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.jdtBinding;

import java.util.LinkedHashMap;

public class RippedResult extends LinkedHashMap<String, Object> {

	public RippedResult(String hexDigest, String project, String jarPath, String jarName, String javadoc, String source, IJavadocSourceMutator javadocMutator, IJavadocSourceMutator sourceMutator) {
		put(JdtConstants.jarNameKey, jarName);
		put(JdtConstants.javadocKey, javadoc);
		put(JdtConstants.sourceKey, source);
		put(JdtConstants.javaProjectKey, project);
		put(JdtConstants.jarPathKey, jarPath);
		put(JdtConstants.hexDigestKey, hexDigest);
		put(JdtConstants.javadocMutatorKey, javadocMutator);
		put(JdtConstants.sourceMutatorKey, sourceMutator);
	}

}