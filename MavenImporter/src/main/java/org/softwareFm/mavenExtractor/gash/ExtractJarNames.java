package org.softwareFm.mavenExtractor.gash;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.softwareFm.mavenExtractor.MavenImporterConstants;
import org.softwareFm.utilities.strings.Strings;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

public class ExtractJarNames {
	public static void main(String[] args) {
		final JdbcTemplate jdbcTemplate = new JdbcTemplate(MavenImporterConstants.dataSource);

		jdbcTemplate.query("select jarurl from version", new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				String string = rs.getString("jarurl");
				String lastSegment = Strings.lastSegment(string, "/");
				if (lastSegment.length() > 0) {
					System.out.println(lastSegment);
					jdbcTemplate.update("insert into jar (name) value(?)", lastSegment);
				}
			}
		});
	}
}
