package org.softwareFm.mavenExtractor;

import java.util.List;

import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.Model;

public interface IExtractorCallback {

	void process(String project, String version, String jarUrl, Model model) throws Exception;

	void finished();
	public static class Utils {
		public static IExtractorCallback summary() {
			return new IExtractorCallback() {
				int count = 0;
				int mailingListCount = 0;
				int issuesCount = 0;
				int urlCount = 0;
				int blankJarCount = 0;

				public void process(String project, String version, String jarUrl, Model model) {
					if (jarUrl.equals(MavenImporterConstants.baseUrl)||jarUrl.length()==0)
						blankJarCount++;
					if (model.getUrl() != null) {
						urlCount++;
					}
					List<MailingList> mailingLists = model.getMailingLists();
					if (mailingLists.size() > 0) {
						mailingListCount++;
					}
					IssueManagement issueManagement = model.getIssueManagement();
					if (issueManagement != null) {
						issuesCount++;
					}
				}

				public void finished() {
					System.out.println("Count: "+ count);
					System.out.println("mailingListCount: "+ mailingListCount);
					System.out.println("issuesCount: "+ issuesCount);
					System.out.println("urlCount: "+ urlCount);
					System.out.println("blankJarCount: "+ blankJarCount);
				}
			};
		}
	}

}
