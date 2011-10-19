package org.softwareFm.mavenExtractor;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.softwareFm.mavenImporter.MavenRepoWalker;
import org.softwareFm.mavenImporter.MysqlNavigator;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.strings.Strings;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;

@SuppressWarnings("unused")
public class DependanciesToMysql implements IExtractorCallback {

	private final int maxCount;
	private int count;
	private final JdbcTemplate jdbcTemplate;

	public DependanciesToMysql(JdbcTemplate jdbcTemplate, int maxCount) {
		this.jdbcTemplate = jdbcTemplate;
		this.maxCount = maxCount;
		jdbcTemplate.update("truncate table dependancy");
	}

	public void process(String junk1, String junk2, String junk3, Model model) throws MalformedURLException {
		final String groupId = getGroupId(model);
		final String artifactId = model.getArtifactId();
		final String version = getVersion(model);
		System.out.println(String.format("%6d",count)+toString(groupId, artifactId, version));
		for (Dependency dependency : model.getDependencies()) {
			final String childGroupId = dependency.getGroupId();
			final String childArtifactId = dependency.getArtifactId();
			final String childVersionId = dependency.getVersion();
			System.out.println("     " + toString(childGroupId, childArtifactId, childVersionId));
			String sql = "insert into dependancy(groupid,artifactid,version,childgroupid,childartifactid,childversion) values(?,?,?,?,?,?)";
			jdbcTemplate.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setObject(1, groupId);
					ps.setObject(2, artifactId);
					ps.setObject(3, version);
					ps.setObject(4, childGroupId);
					ps.setObject(5, childArtifactId);
					ps.setObject(6, Strings.nullSafeToString(childVersionId));
				}
			});

		}
		if (count++ > maxCount)
			System.exit(1);
	}

	private String toString(String groupId, String artifactId, String version) {
		return groupId + "/" + artifactId + "/" + version;
	}

	public void finished() {
	}

	private String getGroupId(Model model) {
		String groupId = model.getGroupId();
		if (groupId != null)
			return groupId;
		Parent parent = model.getParent();
		return parent.getGroupId();
	}

	private String getVersion(Model model) {
		String version = model.getVersion();
		if (version != null)
			return version;
		Parent parent = model.getParent();
		return parent.getVersion();
	}

	public static void main(String[] args) throws MalformedURLException {
		DataSource dataSource = MavenImporterConstants.dataSource;
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		new ExtractProjectStuff().walk(MavenImporterConstants.dataSource, new DependanciesToMysql(jdbcTemplate, 1000000), ICallback.Utils.sysErrCallback());
	}

}
