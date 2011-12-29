package org.softwareFm.mavenExtractor;

import java.io.File;
import java.util.Map;

import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.strings.Strings;

public class RemoveSourceJavadocFromJarData {

	public static void main(String[] args) {
		File home = new File(System.getProperty("user.home"));
		final File localRoot = new File(home, ".sfm_remote");
		final File jarRoot = new File(localRoot, "softwareFm/jars");
		for (File file : Files.walkChildrenOf(jarRoot, Files.exactNameFilter("data.json"))) {
			String text = Files.getText(file);
			Map<String, Object> data = Json.mapFromString(text);
			boolean changed = remove(data, "javadoc") || remove(data, "source");
			System.out.println(file +": "+ Strings.oneLine(text));
			if (changed)
				Files.setText(file, Json.toString(data));
		}
	}

	private static boolean remove(Map<String, Object> data, String key) {
		boolean result = data.containsKey(key);
		data.remove(key);
		return result;
	}
}
