package org.softwareFm.mavenExtractor;

import java.io.File;
import java.net.MalformedURLException;

import javax.sql.DataSource;

import org.apache.maven.model.Model;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Files;
import org.springframework.jdbc.core.JdbcTemplate;

public class DigestAllJars implements IExtractorCallback {

	private final File directory;
	private final JdbcTemplate template;

	public DigestAllJars(DataSource dataSource, File directory) {
		this.directory = directory;
		this.template = new JdbcTemplate(dataSource);
	}

	public void process(String project, String version, String jarUrl, Model model) throws Exception {
		if (jarUrl.equals(MavenImporterConstants.baseUrl) || jarUrl.length() == 0)
			return;
		String targetFile = jarUrl.substring(MavenImporterConstants.baseUrl.length());
		final File file = new File(directory, targetFile);
		System.out.println(file.exists() + " " + Files.digestAsHexString(file) + " " + file);
		template.update("update version set digest = '" + Files.digestAsHexString(file) + "' where project ='" + project + "' and version='" + version + "'");
	}

	public void finished() {

	}

	public static void main(String[] args) throws MalformedURLException {
		File directory = new File("c:/softwareFmRepository");
		DataSource dataSource = MavenImporterConstants.dataSource;
		new ExtractProjectStuff().walk(MavenImporterConstants.dataSource, new DigestAllJars(dataSource, directory), ICallback.Utils.sysErrCallback());
	}

}
