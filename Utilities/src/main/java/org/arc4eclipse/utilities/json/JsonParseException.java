package org.arc4eclipse.utilities.json;

import org.json.simple.parser.ParseException;

public class JsonParseException extends RuntimeException {

	public JsonParseException(String message, ParseException e) {
		super(message, e);
	}

}
