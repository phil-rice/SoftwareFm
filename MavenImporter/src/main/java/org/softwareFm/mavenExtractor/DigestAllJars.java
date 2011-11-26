/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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