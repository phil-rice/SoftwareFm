package org.softwarefm.helloannotations.editor;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class MyWhitespaceDetector implements IWhitespaceDetector {

	@Override
	public boolean isWhitespace(char c) {
		return Character.isWhitespace(c);
	}

}
