/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.collections;

import java.io.File;

import org.softwareFm.crowdsource.utilities.collections.Files;

public class EmbedLicense {
	public static void insertAtStartOfAllJavaFiles(File rootDirectory, String startMarker, String embed) {
		// int count = 0;
		for (File file : Files.walkChildrenOf(rootDirectory, Files.extensionFilter("java"))) {
			// if (count++ > 2)
			// System.exit(0);
			String text = Files.getText(file).trim();
			int index = text.indexOf(startMarker);
			if (index == -1)
				throw new IllegalStateException(file.toString());
			String clipped = text.substring(index);
			String newText = embed + clipped;
			if (!newText.equals(text)) {
				System.out.println("New: " + file);
				Files.setText(file, newText);
			}
		}
	}

	public static void main(String[] args) {
		insertAtStartOfAllJavaFiles(new File(".."), "package", //
				"/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/\n" + "/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */\n" + "/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */\n\n");
	}
}