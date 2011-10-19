package org.softwareFm.mavenImporter;

import java.util.List;

import javax.sql.DataSource;

import org.softwareFm.mavenExtractor.MavenImporterConstants;
import org.softwareFm.utilities.strings.Strings;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class MysqlNavigator implements IMavenNavigator {

	private final JdbcTemplate template;

	public MysqlNavigator(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);
	}

	public boolean shouldExploreTag(String tagName) {
		long finishedCount = template.queryForLong("select count(*) from tag where tagname='" + escape(tagName) + "' and finished>0");
		return finishedCount == 0;
	}

	public boolean shouldExploreProject(String projectName) {
		long finishedCount = template.queryForLong("select count(*) from project where name='" + escape(projectName) + "' and finished>0");
		return finishedCount == 0;
	}

	public void startingTag(String baseUrl, String tagName) {
		long startedCount = template.queryForLong("select count(*) from tag where tagname='" + escape(tagName) + "'");
		if (startedCount == 0)
			template.update("insert into tag (tagname, finished) values ('" + escape(tagName) + "',0)");
	}

	public void finishedTag(String baseUrl, String tagName) {
		template.update("update tag set finished = 1 where tagname='" + escape(tagName) + "'");
	}

	public void startingProject(String baseUrl, String tagName, String projectName, String projectUrl) {
		long startedCount = template.queryForLong("select count(*) from project where name='" + escape(projectName) + "'");
		if (startedCount == 0)
			template.update("insert into project (name, projecturl,tag,finished) values ('" + escape(projectName) + "','" + escape(projectUrl) + "','" + escape(tagName) + "',0)");
		template.update("delete from version where project='" + escape(projectName) + "'");
	}

	public void finishedProject(String baseUrl, String tagName, String projectName, String projectUrl) {
		template.update("update project set finished = 1 where name='" + escape(projectName) + "'");
	}

	public void errorProject(String baseUrl, String tagName, String projectName, String projectUrl, Throwable throwable) {
		template.update("update project set finished = 2 where name='" + escape(projectName) + "'");
	}

	public void version(String baseUrl, String projectName, String version, String versionUrl, String jarUrl, String pomUrl, String pom, List<String> packages) {
		try {
			template.update("insert into version ( project,version, pom,packages,jarurl, pomurl, versionurl) values ('" //
					+ escape(projectName) + "','" + escape(version) + "','" + escape(pom) + "','" + escape(packages.toString()) + "','" + escape(jarUrl) + "','" + escape(pomUrl) + "','" + escape(versionUrl) + "')");
		} catch (DataAccessException e) {
			template.update("insert into version ( project,version, pom,packages,jarurl, pomurl, versionurl) values ('" //
					+ escape(projectName) + "','" + escape(version) + "','','" + escape(packages.toString()) + "','" + escape(jarUrl) + "','" + escape(pomUrl) + "','" + escape(versionUrl) + "')");

		}
	}

	private String escape(String raw) {
		return Strings.sqlEscape(raw);
	}

	public static void main(String[] args) throws InterruptedException {
		DataSource dataSource = MavenImporterConstants.dataSource;
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		MysqlNavigator visitor = new MysqlNavigator(dataSource);
		for (int i = 0; i < 100000; i++) {
			System.out.println();
			System.out.println("tags: " + jdbcTemplate.queryForInt("select count(*) from tag"));
			System.out.println("projects: " + jdbcTemplate.queryForInt("select count(*) from project"));
			System.out.println("versions: " + jdbcTemplate.queryForInt("select count(*) from version"));
			System.out.println();
			new MavenRepoWalker(4).walkAllTags("http://mvnrepository.com", visitor);
			Thread.sleep(500);
		}
	}

}
