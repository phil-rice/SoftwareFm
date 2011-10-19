package org.softwareFm.mavenExtractor;

public interface IArtifactDependancyVisitor {

	void vist(String groupid, String artifactid, String childgroupid, String childartifactid) throws Exception;

}
