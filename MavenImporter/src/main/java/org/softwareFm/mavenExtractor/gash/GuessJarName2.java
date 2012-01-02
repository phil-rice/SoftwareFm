package org.softwareFm.mavenExtractor.gash;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.softwareFm.mavenExtractor.MavenImporterConstants;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.strings.Strings;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class GuessJarName2 {
	public static void main(String[] args) {
		JdbcTemplate template = new JdbcTemplate(MavenImporterConstants.dataSource);
		final Pattern stringsStart = Pattern.compile("^([\\.a-zA-Z_-]+)");
		final AtomicInteger found = new AtomicInteger();
		final AtomicInteger notFound = new AtomicInteger();
		final List<String> failed = Lists.newList();
		template.query("select * from jar limit 100000", new RowMapper<Void>() {
			public Void mapRow(ResultSet rs, int rowNum) throws SQLException {
				String name = rs.getString("name");
				Matcher matcher = stringsStart.matcher(name);
				if (matcher.find()) {
					found.incrementAndGet();
					String raw = matcher.group(1);
					String candidate = raw.substring(0, raw.length() - 1);
					String expected = rs.getString("artifactId");
					boolean matched = candidate.equals(expected);
					if (!matched)
						failed.add(name + " should be" + expected + " but was " + candidate);
					System.out.println(String.format("%5s %40s %20s %20s", Boolean.toString(matched), name, candidate, expected));
				} else {
					notFound.incrementAndGet();
				}
				return null;
			}
		});
		System.out.println("Found: " + found.get());
		System.out.println("NotFound: " + notFound.get());
		System.out.println("Failed: " + failed.size());
		System.out.println(Strings.join(failed, "\n"));
		System.out.println("Found: " + found.get());
		System.out.println("NotFound: " + notFound.get());
		System.out.println("Failed: " + failed.size());
	}
}
