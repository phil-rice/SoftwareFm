package org.arc4eclipse.utilities.collections;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

public class Files {

	public static String getText(Resource resource) {
		try {
			InputStream inputStream = resource.getInputStream();
			return getText(inputStream);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static String getText(URL url) {
		try {
			InputStream inputStream = new UrlResource(url).getInputStream();
			return getText(inputStream);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static String getText(InputStream inputStream) {
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		return getText(inputStreamReader);
	}

	public static String getText(Reader rawReader) {
		BufferedReader reader = new BufferedReader(rawReader);
		StringBuffer stringBuffer = new StringBuffer();
		try {
			try {
				while (reader.ready()) {
					String string = reader.readLine();
					stringBuffer.append(string);
					stringBuffer.append("\n");
				}
				return stringBuffer.toString();
			} finally {
				reader.close();
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static String getTextFromClassPath(Class clazz, String path) {
		return getText(new ClassPathResource(path, clazz));
	}
}
