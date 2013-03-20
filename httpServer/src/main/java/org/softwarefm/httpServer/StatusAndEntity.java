package org.softwarefm.httpServer;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.strings.Strings;

public class StatusAndEntity {
	public final int status;
	public final    HttpEntity entity;
	public final boolean zipped;

	public static StatusAndEntity ok(String string, boolean zip) {
		if (!zip)
			return ok(string);
		else
			return new StatusAndEntity(HttpStatus.SC_OK, string == null ? null
					: new ByteArrayEntity(Strings.zip(string)), true);
	}  
 
	public static StatusAndEntity ok(String string) {
		try {
			return new StatusAndEntity(HttpStatus.SC_OK, string == null ? null
					: new StringEntity(string), false);
		} catch (UnsupportedEncodingException e) {
			throw WrappedException.wrap(e);
		} 
	}

	public static StatusAndEntity ok() {
		return new StatusAndEntity(HttpStatus.SC_OK, null, false);
	}

	public StatusAndEntity(int status, HttpEntity entity, boolean zipped) {
		this.status = status;
		this.entity = entity;
		this.zipped = zipped;
	}

	@Override
	public String toString() {
		return "StatusAndEntity [status=" + status + ", entity=" + entity
				+ ", zipped=" + zipped + "]";
	}

}
