/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.mavenImporter;

import java.util.List;

public interface IMavenVisitor {

	void startingTag(String baseUrl, String tagName);

	void finishedTag(String baseUrl, String tagName);

	void startingProject(String baseUrl, String tagName, String projectName, String projectUrl);

	void finishedProject(String baseUrl, String tagName, String projectName, String projectUrl);

	void version(String baseUrl, String projectName, String version, String versionUrl, String jarUrl, String pomUrl, String pom, List<String> packages);

}