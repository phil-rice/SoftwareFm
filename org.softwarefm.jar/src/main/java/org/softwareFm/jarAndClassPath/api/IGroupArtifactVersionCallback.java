/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.api;

public interface IGroupArtifactVersionCallback {

	void process(String groupId, String artifactId, String version);

	void noData();
public static class Utils{
	public static IGroupArtifactVersionCallback sysout(){
		return new IGroupArtifactVersionCallback() {
			@Override
			public void process(String groupId, String artifactId, String version) {
				System.out.println(groupId +", " + artifactId +", " + version);
			}

			@Override
			public void noData() {
				System.out.println("No data");
			}
		};
	}
}

}