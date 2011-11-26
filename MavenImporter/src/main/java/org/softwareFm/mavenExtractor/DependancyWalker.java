/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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