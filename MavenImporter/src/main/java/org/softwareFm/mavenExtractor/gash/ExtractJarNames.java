package org.softwareFm.mavenExtractor.gash;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.softwareFm.mavenExtractor.IExtractorCallback;
import org.softwareFm.mavenExtractor.MavenImporterConstants;
import org.softwareFm.mavenExtractor.git.ExtractProjectStuff;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.strings.Strings;
import org.springframework.jdbc.core.JdbcTemplate;

public class ExtractJarNames {
	private static String getGroupId(Model model) {
		String groupId = model.getGroupId();
		if (groupId != null)
			return groupId;
		Parent parent = model.getParent();
		return parent.getGroupId();
	}

	private static String getVersion(Model model) {
		String version = model.getVersion();
		if (version != null)
			return version;
		Parent parent = model.getParent();
		return parent.getVersion();
	}

	public static void main(String[] args) {
		final JdbcTemplate jdbcTemplate = new JdbcTemplate(MavenImporterConstants.dataSource);
		new ExtractProjectStuff().walk(MavenImporterConstants.dataSource, new IExtractorCallback() {

			public void process(String project, String version, String jarUrl, Model model) throws Exception {
				String lastSegment = Strings.lastSegment(jarUrl, "/");
				if (lastSegment.length() > 0) {
					System.out.println(lastSegment);
					jdbcTemplate.update("insert into jar (name,groupId,artifactId,version) value(?,?,?,?)", lastSegment, getGroupId(model), model.getArtifactId(), getVersion(model));
				}
 
			}

			public void finished() {

			}
		}, ICallback.Utils.<Throwable>noCallback());
	}
}
