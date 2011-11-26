/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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
					if (jarUrl.equals(MavenImporterConstants.baseUrl) || jarUrl.length() == 0)
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
					System.out.println("Count: " + count);
					System.out.println("mailingListCount: " + mailingListCount);
					System.out.println("issuesCount: " + issuesCount);
					System.out.println("urlCount: " + urlCount);
					System.out.println("blankJarCount: " + blankJarCount);
				}
			};
		}
	}

}