package org.softwareFm.mavenExtractor;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.maven.model.Model;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Files;

@SuppressWarnings("unused")
public class DownloadJarsExtractor implements IExtractorCallback {

	private final File directory;
	private final int maxCount;
	private int count;
	private int totalTime;
	private int totalDownload;
	private int totalBytes;
	private final ExecutorService service;

	public DownloadJarsExtractor(File directory, int maxCount, ExecutorService service) {
		this.directory = directory;
		this.maxCount = maxCount;
		this.service = service;
	}

	public void process(String project, String version, String jarUrl, Model model) throws MalformedURLException {
		if (jarUrl.equals(MavenImporterConstants.baseUrl) || jarUrl.length() == 0)
			return;
		String targetFile = jarUrl.substring(MavenImporterConstants.baseUrl.length());
		// downloadOneFile(jarUrl, targetFile, "");
		downloadOneFile(jarUrl, targetFile, "-sources");
		downloadOneFile(jarUrl, targetFile, "-javadoc");
	}

	public static String append(String targetFile, String string) {
		return targetFile.replace(".jar", string + ".jar");
	}

	private void downloadOneFile(String rawJarUrl, String rawTargetFile, String embed) {
		String targetFile = append(rawTargetFile, embed);
		final String jarUrl = append(rawJarUrl, embed);
		try {
			final File file = new File(directory, targetFile);
			if (count++ >= maxCount)
				System.exit(0);
			if (!file.exists()) {
				Files.makeDirectoryForFile(file);
				long time = System.currentTimeMillis();
				Future<Integer> submit = service.submit(new Callable<Integer>() {

					public Integer call() throws Exception {
						int byteCount = download(jarUrl, file);
						return byteCount;
					}
				});
				Integer byteCount = submit.get(30, TimeUnit.SECONDS);
				long duration = System.currentTimeMillis() - time;
				totalDownload++;
				totalTime += duration;
				totalBytes += byteCount;
				if (byteCount > 0)
					System.out.println(String.format("%6d %8d Took %4d %s ---> %s", count, byteCount, duration, jarUrl, targetFile));
			}
		} catch (Exception e) {
			System.err.println(jarUrl + " " + e.getMessage());
		}
	}

	private int download(String jarUrl, File file) {
		try {
			System.out.print(jarUrl);
			return Files.downLoadFile(new URL(jarUrl), file);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return 0;
		} finally {
			System.out.println("done");
		}
	}

	public void finished() {
	}

	public static void main(String[] args) throws MalformedURLException {
		File directory = new File("c:/softwareFmRepository");
		directory.mkdirs();
		ExecutorService service = new ThreadPoolExecutor(2, 10, 2, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
		new ExtractProjectStuff().walk(MavenImporterConstants.dataSource, new DownloadJarsExtractor(directory, 1000000, service), ICallback.Utils.sysErrCallback());
	}

}
