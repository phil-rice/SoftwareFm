package org.softwareFm.mavenExtractor;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

public class MavenImporterConstants {

	public static String baseUrl = "http://repo1.maven.org/maven2/";

	public static DataSource dataSource;
	public static String slingResourceTypeKey = "sling:resourceType";


	public static final String artifactIdKey = "artifactId";
	public static final String groupIdKey = "groupId";
	public static final String versionKey = "version";
	public static final String issueManagementUrlKey = "issues";
	public static final String descriptionKey = "description";
	public static final String nameKey = "name";

	public static final String projectHomePageKey = "url";

	public static final String cannotDereferenceProperty = "Cannot deference property in {0}\nOriginal value was {1}";

	public static final String digestKey = "digest";

	public static final String jarUrl = "url";
	public static final String found = "found";

	public static final String collectionResourceType = "collection";
	public static final String jarResourceType = "jar";
	public static final String groupResourceType = "group";
	public static final String artifactResourceType = "artifact";
	public static final String versionResourceType = "version";
	public static final String versionJarResourceType = "jar";
	public static final String usesResourceType = "uses";
	public static final String usedByResourceType = "usedBy";

	public static final String mailingListNameKey = "name";
	public static final String mailingListArchiveKey = "archive";
	public static final String mailingListOtherArchivesKey = "otherArchives";
	public static final String mailingListPostKey = "post";
	public static final String mailingListSubscribeKey = "subscribe";
	public static final String mailingListUnsubscribeKey = "unsubscribe";




	static {
		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
		basicDataSource.setUrl("jdbc:mysql://localhost/spider");
		basicDataSource.setUsername("root");
		basicDataSource.setPassword("iwtbde");
		dataSource = basicDataSource;
	}
}
