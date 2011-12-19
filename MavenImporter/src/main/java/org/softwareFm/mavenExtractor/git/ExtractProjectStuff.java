/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.mavenExtractor.git;

import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.softwareFm.mavenExtractor.IExtractorCallback;
import org.softwareFm.mavenExtractor.MavenImporterConstants;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public class ExtractProjectStuff {

	public ExtractProjectStuff() {
	}

	public void walk(DataSource dataSource, final IExtractorCallback callback, final ICallback<Throwable> exceptions) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		template.query("select * from version ", new ResultSetExtractor<Void>() {

			public Void extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					try {
						String project = rs.getString("project");
						String version = rs.getString("version");
						String pom = rs.getString("pom");
						String jarUrl = rs.getString("jarurl");
						Model model = new MavenXpp3Reader().read(new StringReader(pom));
						callback.process(project, version, jarUrl, model);
					} catch (Exception e) {
						try {
							exceptions.process(e);
						} catch (Exception e1) {
							throw WrappedException.wrap(e1);
						}
					}
				}
				callback.finished();
				return null;
			}
		});
	}

	public static void main(String[] args) {
		new ExtractProjectStuff().walk(MavenImporterConstants.dataSource, IExtractorCallback.Utils.summary(), ICallback.Utils.sysErrCallback());
	}
}