package org.softwareFm.mavenExtractor;

public interface IDependancyVisitor {

	void vist(String groupid, String artifactid, String childgroupid, String childartifactid);

}
