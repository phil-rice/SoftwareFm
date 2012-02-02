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
