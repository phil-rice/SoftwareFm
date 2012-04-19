/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.mysoftwareFm;

import org.softwareFm.crowdsource.utilities.strings.PreAndPost;
import org.softwareFm.crowdsource.utilities.strings.Strings;

public class MySoftwareFmFunctions {

	public static String monthFileNameToPrettyName(String filename) {
		PreAndPost preAndPost = Strings.split(filename, '_');
		if (preAndPost.post == null || preAndPost.pre.length() < 3 || !Strings.isInteger(preAndPost.post))
			return filename;
		else
			return Strings.upperCaseFirstCharacter(preAndPost.pre.substring(0, 3)) + " 20" + preAndPost.post;

	}

}