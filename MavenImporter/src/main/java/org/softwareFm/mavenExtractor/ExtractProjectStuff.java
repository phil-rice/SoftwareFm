package org.softwareFm.mavenExtractor;

import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
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
		template.query("select * from version", new ResultSetExtractor<Void>() {

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
