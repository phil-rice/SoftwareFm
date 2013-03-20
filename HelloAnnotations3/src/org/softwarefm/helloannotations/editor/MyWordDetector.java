package org.softwarefm.helloannotations.editor;

import org.eclipse.jface.text.rules.IWordDetector;

public class MyWordDetector implements IWordDetector {

	@Override
	public boolean isWordStart(char c) {
		return Character.isJavaIdentifierStart(c);
	}

	@Override
	public boolean isWordPart(char c) {
		return Character.isJavaIdentifierPart(c);
	}

}
