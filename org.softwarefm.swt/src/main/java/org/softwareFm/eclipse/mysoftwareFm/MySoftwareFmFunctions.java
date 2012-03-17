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
