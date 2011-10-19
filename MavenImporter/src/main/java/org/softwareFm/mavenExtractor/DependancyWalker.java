package org.softwareFm.mavenExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

public class DependancyWalker {

	public DependancyWalker() {

	}

	public void walk(DataSource dataSource, final IArtifactDependancyVisitor visitor, ICallback<Throwable> sysErrCallback) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		template.update("drop table if exists latestversion");
		template.update("create table latestversion as select  groupid, artifactid,max(version)as version from dependancy p group by groupid,artifactid,p.version");

		template.query("SELECT d.* FROM latestversion l , dependancy d where l.groupid=d.groupid and l.artifactid = d.artifactid and l.version = d.version  limit 100", new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				try {
					String groupid = rs.getString("groupid");
					String artifactid = rs.getString("artifactid");
					String childgroupid = rs.getString("childgroupid");
					String childartifactid = rs.getString("childartifactid");
					visitor.vist(groupid, artifactid, childgroupid, childartifactid);
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		});
	}

}
