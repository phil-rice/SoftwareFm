package org.softwarefm.mavenRip;

import java.util.Map;

public interface IGroupArtifactVisitor {

	void visit(String groupId, String artifactId, Map<String, String> versionToPomUrl);
}
