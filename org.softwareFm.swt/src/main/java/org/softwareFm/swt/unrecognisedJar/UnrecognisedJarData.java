/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.unrecognisedJar;

import java.io.File;

public class UnrecognisedJarData {
	public static UnrecognisedJarData forTests(String projectName, File jarFile) {
		return new UnrecognisedJarData(projectName, jarFile);
	}

	public final String projectName;
	public final File jarFile;

	private UnrecognisedJarData(String projectName, File jarFile) {
		this.projectName = projectName;
		this.jarFile = jarFile;

	}

	@Override
	public String toString() {
		return "UnrecognisedJarData [projectName=" + projectName + ", jarFile=" + jarFile + "]";
	}

}