/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.git;

import java.util.Map;

import org.softwareFm.crowdsource.utilities.functions.IFunction1;

public interface IGitWriter extends IGitReader {
	void init(String url, String commitMessage);

	/** creates/overwrites a file at this file description */
	void put(IFileDescription fileDescription, Map<String, Object> data, String commitMessage);

	void delete(IFileDescription fileDescription, String commitMessage);

	/** Assumes the file is a list of maps, each map is on a separate line (separated by \n). If encrypted, each line is separately encrypted */
	void append(IFileDescription fileDescription, Map<String, Object> data, String commitMessage);

	/**
	 * Assumes the file is a list of maps, each map is on a separate line (separated by \n). If encrypted, each line is separately encrypted <br />
	 * Each line that is accepted deleted<br />
	 * 
	 * @return count of lines deleted
	 */

	int removeLine(IFileDescription fileDescription, IFunction1<Map<String, Object>, Boolean> acceptor, String commitMessage);

	public static class Utils {

	}

}