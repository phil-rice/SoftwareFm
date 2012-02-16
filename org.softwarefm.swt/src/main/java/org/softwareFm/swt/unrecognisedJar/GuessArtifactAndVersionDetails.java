/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.unrecognisedJar;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.softwareFm.common.collections.Files;
import org.softwareFm.common.strings.Strings;

public class GuessArtifactAndVersionDetails {

	public String guessArtifactName(File from) {
		if (from.getName().equals("rt.jar"))
			return "runtime";
		String name = Files.noExtension(from.getName());
		final Pattern versionNo = Pattern.compile("([-_][0-9]+\\.[0-9])");
		Matcher matcher = versionNo.matcher(name);
		if (matcher.find()) {
			int index = matcher.start();
			String jarName = name.substring(0, index);
			return jarName;
		}
		return name;
	}

	public boolean matches(String fileNameWithoutExtension, String artifactName) {
		if (fileNameWithoutExtension.equals("rt") && artifactName.equals("runtime"))
			return true;
		return fileNameWithoutExtension.equals(artifactName);
	}

	public String guessVersion(File file) {
		String fromFile = Strings.versionPartOf(file, "");
		if (fromFile.length() > 0)
			return fromFile;
		final Pattern threePartVersionNo = Pattern.compile("([0-9]+\\.[0-9]+\\.[0-9_-]+)");
		Matcher matcher = threePartVersionNo.matcher(file.getAbsolutePath());
		if (matcher.find())
			return matcher.group(0);
		return "";
	}

}