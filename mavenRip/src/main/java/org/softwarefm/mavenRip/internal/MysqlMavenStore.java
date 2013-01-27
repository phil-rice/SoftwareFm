package org.softwarefm.mavenRip.internal;

import javax.sql.DataSource;

import org.apache.maven.model.Model;
import org.softwarefm.core.maven.IMaven;
import org.softwarefm.mavenRip.IMavenStore;
import org.springframework.jdbc.core.JdbcTemplate;

public class MysqlMavenStore implements IMavenStore {

	private final JdbcTemplate template;
	private final IMaven maven;
	private final boolean sysout;

	public MysqlMavenStore(IMaven maven, DataSource dataSource, boolean sysout) {
		this.sysout = sysout;
		this.template = new JdbcTemplate(dataSource);
		this.maven = maven;
	}

	public void store(String pomUrl) {
		Model model = maven.pomToModel(pomUrl);
		String groupId = maven.groupId(model);
		String artifactId = maven.artifactId(model);
		String version = maven.version(model);
		template.update("insert into maven (groupId, artifactId, version, pomUrl) values(?,?,?,?)", groupId, artifactId, version, pomUrl);
		if (sysout)
			System.out.println(pomUrl);
	}

}
