/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.collections;

import java.io.File;
import java.util.Collection;

public class EmbedLicense {
	public static void insertAtStartOfAllJavaFiles(File rootDirectory, String startMarker, String endMarker, String embed) {
		Collection<File> duff = Lists.newList();
		// int count = 0;
		for (File file : Files.walkChildrenOf(rootDirectory, Files.extensionFilter("java"))) {
			// if (count++ > 2)
			// System.exit(0);
			String text = Files.getText(file).trim();
			if (text.startsWith(startMarker)) {
				int index = text.indexOf(endMarker);
				if (index == -1)
					duff.add(file);
				else {
					String clipped = text.substring(index + endMarker.length());
					String newText = startMarker + embed + endMarker + clipped;
					if (!newText.equals(text)) {
						System.out.println("Old: " + file);
						System.out.println();
						Files.setText(file, newText);
					}
				}
			} else {
				String newText = startMarker + embed + endMarker + text;
				System.out.println("New: " + file);
				Files.setText(file, newText);
			}

		}
	}

	public static void main(String[] args) {
		insertAtStartOfAllJavaFiles(new File(".."), "/* This file is part of SoftwareFm\n", "If not, see <http://www.gnu.org/licenses/> */\n\n", //
				"/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/\n" + "/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */\n" + "/* You should have received a copy of the GNU General Public License along with SoftwareFm. ");
	}
}