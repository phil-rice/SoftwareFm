package org.softwarefm.mavenRip.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.softwarefm.mavenRip.IGroupArtifactVisitor;
import org.softwarefm.mavenRip.IMavenWalker;
import org.softwarefm.mavenRip.MavenRip;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.strings.Strings;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

public class MavenWalker implements IMavenWalker {

	private final JdbcTemplate template;
	private final ICallback<Throwable> errorCallback;

	public MavenWalker(DataSource dataSource, ICallback<Throwable> errorCallback) {
		this.errorCallback = errorCallback;
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	public void walk(final IGroupArtifactVisitor visitor) {
		template.query("select distinct groupId, artifactId from maven", new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				try {
					String groupId = rs.getString("groupId");
					String artifactId = rs.getString("artifactId");
					final Map<String, String> map = new HashMap<String, String>();
					template.query("select version,pomUrl from maven where groupId =? and artifactId = ?", new RowCallbackHandler() {
						@Override
						public void processRow(ResultSet rs) throws SQLException {
							map.put(rs.getString("version"), rs.getString("pomUrl"));
						}
					}, groupId, artifactId);
					visitor.visit(groupId, artifactId, map);
				} catch (Exception e) {
					ICallback.Utils.call(errorCallback, e);
				}
			}
		});
	}

	public static void main(String[] args) {
		new MavenWalker(MavenRip.dataSource, ICallback.Utils.sysErrCallback()).walk(new IGroupArtifactVisitor() {
			@Override
			public void visit(String groupId, String artifactId, Map<String, String> versionToPomUrl) {
				System.out.println(groupId + " " + artifactId);
				List<String> keys = new ArrayList<String>(versionToPomUrl.keySet());
				Collections.sort(keys, Strings.invertedVersionComparator);
				for (String version : keys) {
					String pomUrl = versionToPomUrl.get(version);
					System.out.println("  " + version + " -> " + pomUrl);
				}
			}
		});
	}

}
