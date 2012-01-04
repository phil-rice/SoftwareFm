package org.softwareFm.mavenExtractor.gash;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.collections.unrecognisedJar.GuessArtifactAndVersionDetails;
import org.softwareFm.mavenExtractor.MavenImporterConstants;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.strings.Strings;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class GuessJarName2 {
	public static void main(String[] args) {
		JdbcTemplate template = new JdbcTemplate(MavenImporterConstants.dataSource);
		final AtomicInteger found = new AtomicInteger();
		final List<String> failed = Lists.newList();
		template.query("select * from jar limit 100000", new RowMapper<Void>() {
			public Void mapRow(ResultSet rs, int rowNum) throws SQLException {
				GuessArtifactAndVersionDetails guesser = new GuessArtifactAndVersionDetails();
				String name = rs.getString("name");
				String guess = guesser.guessArtifactName(new File(name));
				String expected = rs.getString("artifactId");
				boolean matched = guess.equals(expected);
				if (matched)
					found.incrementAndGet();
				else
					failed.add(name + " should be " + expected + " but was " + guess);
				// System.out.println(String.format("%5s %40s %20s %20s", Boolean.toString(matched), name, guess, expected));
				return null;
			}
		});
		System.out.println("Found: " + found.get());
		System.out.println("Failed: " + failed.size());
		System.out.println(Strings.join(failed, "\n"));
		System.out.println("Found: " + found.get());
		System.out.println("Failed: " + failed.size());
	}
}
