/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.collections;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.constants.UtilityConstants;
import org.softwareFm.utilities.constants.UtilityMessages;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

public class Files {
	private static final Object lock = new Object();

	private static final Map<String, String> extensionToMime = Maps.makeMap(//
			"jpg", "image/jpg", "gif", "image/gif", "html", "text/html", "png", "image/png", "css", "text/css", "xml", "text/xml", "jar", "application/java-archive");

	public static String defaultMimeType(String fileName) {
		String result = Maps.getOrDefault(extensionToMime, Files.extension(fileName), "text/plain");
		return result;
	}

	public static String offset(File root, File leaf) {
		String rootString = root.getAbsolutePath();
		String leafString = leaf.getAbsolutePath();
		if (leafString.startsWith(rootString))
			return leafString.substring(rootString.length() + 1).replace('\\', '/');
		throw new IllegalArgumentException(MessageFormat.format(UtilityMessages.cannotFindOffset, root, leaf));
	}

	/** The directory is locked, and the operation begins! */
	public static void doOperationInLock(File dir, String lockFileName, ICallback<File> callback) {
		File file = new File(dir, lockFileName);
		try {
			if (!file.exists()) {
				dir.mkdirs();
				try {
					file.createNewFile();
				} catch (Exception e) {
					if (!file.exists())// A likely scenario is that the file has been made between the file exists and the createNewFile, which could end up causing "access is denied"
						file.createNewFile();
				}
			}
			synchronized (lock) {
				FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
				// Get an exclusive lock on the whole file
				FileLock lock = channel.lock();
				try {
					callback.process(file);
				} finally {
					lock.release();
					channel.close();
				}
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static int downLoadFile(String url, File target) {
		try {
			return downLoadFile(new URL(url), target);
		} catch (MalformedURLException e) {
			throw WrappedException.wrap(e);
		}

	}

	public static int downLoadFile(URL url, File target) {
		try {
			File tempFile = new File(target.getCanonicalFile().toString() + "_temp");
			target.delete();
			tempFile.delete();
			// URLConnection urlC = url.openConnection();
			// Copy resource to local file, use remote file
			// if no local file name specified
			InputStream is = url.openStream();
			// Print info about resource
			FileOutputStream fos = new FileOutputStream(tempFile);
			int oneChar, count = 0;
			while ((oneChar = is.read()) != -1) {
				fos.write(oneChar);
				count++;
			}
			is.close();
			fos.close();
			tempFile.renameTo(target);
			return count;
		} catch (Exception e) {
			try {
				target.delete();
			} catch (Exception e1) {
			}
			throw WrappedException.wrap(e);

		}
	}

	public static Iterable<File> walkChildrenOf(final File root, final FilenameFilter filter) {
		return new AbstractFindNextIterable<File, Stack<Iterator<File>>>() {

			@Override
			protected File findNext(Stack<Iterator<File>> context) throws Exception {
				while (!context.isEmpty()) {
					Iterator<File> iterator = context.peek();
					if (iterator.hasNext()) {
						File next = iterator.next();
						if (next.isDirectory())
							push(context, next);
						else if (filter.accept(next.getParentFile(), next.getName()))
							return next;

					} else
						context.pop();
				}
				return null;
			}

			@Override
			protected Stack<Iterator<File>> reset() throws Exception {
				Stack<Iterator<File>> stack = new Stack<Iterator<File>>();
				push(stack, root);
				return stack;
			}

			private void push(Stack<Iterator<File>> stack, final File file) {
				File[] listFiles = file.listFiles();
				if (listFiles == null)
					throw new NullPointerException(MessageFormat.format(UtilityConstants.directoryNotFound, file.getAbsoluteFile()));
				List<File> files = Arrays.asList(listFiles);
				stack.push(files.iterator());
			}
		};
	}

	public static String getText(Resource resource) {
		try {
			InputStream inputStream = resource.getInputStream();
			return getText(inputStream);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static String getText(File file) {
		try {
			FileReader reader = new FileReader(file);
			try {
				return getText(reader);
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			} finally {
				reader.close();
			}
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
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			try {
				return getText(inputStreamReader);
			} finally {
				inputStreamReader.close();
				inputStream.close();
			}
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}

	public static String getText(Reader rawReader) {
		try {
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			Reader reader = new BufferedReader(rawReader);
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
			return writer.toString();
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static String getTextFromClassPath(Class clazz, String path) {
		return getText(new ClassPathResource(path, clazz));
	}

	public static String digestAsHexString(Resource resource) {
		try {
			return digestAsHexString(resource.getInputStream());
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}

	public static String digestAsHexString(File file) {
		try {
			if (file.exists())
				return digestAsHexString(new FileInputStream(file));
			else
				return "";
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	private static String digestAsHexString(InputStream inputStream) {
		try {
			DigestInputStream digestInputStream = new DigestInputStream(inputStream, MessageDigest.getInstance("SHA-1"));
			byte[] buffer = new byte[8192];
			while (digestInputStream.read(buffer) != -1)
				doNothing();
			byte[] rawDigest = digestInputStream.getMessageDigest().digest();
			return new BigInteger(rawDigest).abs().toString(16);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	private static void doNothing() {
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
					doNothing();
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

	public static String nameWithoutExtension(File file) {
		String name = file.getName();
		int index = name.indexOf('.');
		if (index == -1)
			return name;
		else
			return name.substring(0, index);
	}

	public static String noExtension(String raw) {
		int index = raw.lastIndexOf('.');
		if (index == -1)
			return raw;
		else
			return raw.substring(0, index);

	}

	public static String extension(String name) {
		int index = name.lastIndexOf('.');
		if (index == -1)
			return "";
		else
			return name.substring(index + 1);
	}

	public static String justName(File file) {
		String name = file.getName();
		return noExtension(name);
	}

	public static IFunction1<String, String> noExtension() {
		return new IFunction1<String, String>() {
			@Override
			public String apply(String from) throws Exception {
				return noExtension(from);
			}
		};
	}

	public static IFunction1<File, String> toFileName() {
		return new IFunction1<File, String>() {
			@Override
			public String apply(File from) throws Exception {
				String result = from.getName();
				return result;
			}
		};
	}

	public static void makeDirectoryForFile(File file) {
		file.getParentFile().mkdirs();

	}

	public static void setText(File file, String text) {
		try {
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fileWriter);
			try {
				out.write(text);
			} finally {
				out.close();
				fileWriter.close();
			}
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}

	public static File downloadAndPutIndirectory(String url, File directory) {
		directory.mkdirs();
		int index = url.lastIndexOf('/');
		if (index == -1)
			throw new IllegalArgumentException(MessageFormat.format(UtilityConstants.cannotParseUrl, url));
		String lastSegment = url.substring(index);
		File file = new File(directory, lastSegment);
		downLoadFile(url, file);
		return file;
	}

	public static boolean deleteDirectory(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDirectory(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	public static FileFilter directoryFilter() {
		return new FileFilter() {
			@Override
			public boolean accept(File arg0) {
				return arg0.isDirectory();
			}
		};
	}

	public static FileFilter directoryIgnoringDotFilter() {
		return new FileFilter() {
			@Override
			public boolean accept(File arg0) {
				return arg0.isDirectory() && !arg0.getName().startsWith(".");
			}
		};
	}

	public static File[] listChildDirectoriesIgnoringDot(File file) {
		File[] result = file.listFiles(Files.directoryIgnoringDotFilter());
		return result == null ? new File[0] : result;
	}

	public static File[] listChildDirectories(File file) {
		File[] result = file.listFiles(Files.directoryFilter());
		return result == null ? new File[0] : result;
	}

	public static List<File> listParentsUntil(File root, File leaf) {
		List<File> result = Lists.newList();
		listParentsUntil(result, root, leaf);
		return result;

	}

	private static void listParentsUntil(List<File> result, File root, File leaf) {
		if (leaf == null)
			return;
		result.add(leaf);
		if (leaf.equals(root))
			return;
		listParentsUntil(result, root, leaf.getParentFile());
	}

	public static FilenameFilter exactNameFilter(final String name) {
		return new FilenameFilter() {
			@Override
			public boolean accept(File file, String actualName) {
				boolean result = actualName.equals(name);
				return result;
			}
		};
	}

	public static IFunction1<File, Boolean> fileNameEquals(final String name) {
		return new IFunction1<File, Boolean>() {
			@Override
			public Boolean apply(File from) throws Exception {
				return from.getName().equals(name);
			}
		};
	}

	public static void setText(OutputStream outputStream, String text) {
		setText(outputStream, text, "UTF-8");
	}

	public static void setText(OutputStream outputStream, String text, String encoding) {
		try {
			outputStream.write(text.getBytes(encoding));
			outputStream.flush();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				throw WrappedException.wrap(e);
			}
		}
	}

	static class ByteArrayAndLength {
		byte[] bytes;
		int length;

		public ByteArrayAndLength(byte[] bytes, int length) {
			super();
			this.bytes = bytes;
			this.length = length;
		}

		static int length(List<ByteArrayAndLength> list) {
			int result = 0;
			for (ByteArrayAndLength byteArrayAndLength : list) {
				result += byteArrayAndLength.length;
			}
			return result;
		}
	}

	public static byte[] getBytes(InputStream inputStream, int bufferSize) {
		try {
			List<ByteArrayAndLength> list = Lists.newList();
			while (true) {
				byte[] buffer = new byte[bufferSize];
				int count = inputStream.read(buffer);
				if (count == -1) {
					byte[] result = new byte[ByteArrayAndLength.length(list)];
					int index = 0;
					for (ByteArrayAndLength byteArrayAndLength : list) {
						System.arraycopy(byteArrayAndLength.bytes, 0,  result, index, byteArrayAndLength.length);
						index += byteArrayAndLength.length;
					}
					assert index == result.length;
					return result;
				}
				list.add(new ByteArrayAndLength(buffer, count));
			}
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				throw WrappedException.wrap(e);
			}
		}
	}
}