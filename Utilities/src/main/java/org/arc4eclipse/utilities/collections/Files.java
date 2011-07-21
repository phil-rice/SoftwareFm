package org.arc4eclipse.utilities.collections;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;

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

	public static String digestAsHexString(File file) {
		try {
			DigestInputStream inputStream = new DigestInputStream(new FileInputStream(file), MessageDigest.getInstance("SHA-1"));
			byte[] buffer = new byte[8192];
			while (inputStream.read(buffer) != -1)
				;
			byte[] rawDigest = inputStream.getMessageDigest().digest();
			return new BigInteger(rawDigest).abs().toString(16);
		} catch (Exception e) {
			System.out.println(file == null ? "File is null" : file.getAbsolutePath());
			throw WrappedException.wrap(e);
		}
	}

	public static byte[] digest(File file) {
		try {
			return digest(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw WrappedException.wrap(e);
		}
	}

	public static byte[] digest(InputStream inputStream) {
		try {
			DigestInputStream digestStream = new DigestInputStream(inputStream, MessageDigest.getInstance("SHA-1"));
			try {
				byte[] buffer = new byte[8192];
				while (digestStream.read(buffer) != -1)
					;
				byte[] rawDigest = digestStream.getMessageDigest().digest();
				return rawDigest;
			} finally {
				digestStream.close();
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);

		}
	}

	public static FilenameFilter extensionFilter(final String string) {
		return new FilenameFilter() {
			private final String extension = "." + string;

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(extension);
			}
		};
	}

	public static String justName(File file) {
		String name = file.getName();
		int index = name.lastIndexOf('.');
		if (index == -1)
			return name;
		else
			return name.substring(0, index);
	}
}
