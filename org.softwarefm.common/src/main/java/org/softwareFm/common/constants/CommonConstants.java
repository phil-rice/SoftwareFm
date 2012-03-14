/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.constants;

import java.util.Set;

import org.softwareFm.common.collections.Sets;

public class CommonConstants {

	public final static String dataFileName = "data.json";
	public static final String DOT_GIT = ".git";
	public static final String lockFileName = "lock.lck";

	public static final String typeTag = "sling:resourceType";
	public static final String collectionType = "collection";
	public static final String dataParameterName = "data";
	public static final String repoUrlKey = "repoUrl";
 
	public static final long testTimeOutMs = 400000;

	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String DELETE = "DELETE";
	public static final String HEAD = "HEAD";

	public static final String makeRootPrefix = "command/makeRoot";
	public static final String emailSaltPrefix = "command/emailSalt";

	public static final int okStatusCode = 200;
	public static final int notFoundStatusCode = 404;
	public static final int serverErrorCode = 500;

	public static final int testPort = 8080;
	public static final String dataKey = "data";
	public static final Set<Integer> okStatusCodes = Sets.makeSet(okStatusCode);
	public static final int staleCachePeriodForTest = 1000;
	public static final int staleCachePeriod = 5*60*1000;
	public static final long clientTimeOut = 2000;
	public static final int serverPort = 8080;
	public static final Object errorKey = "error";
	public static final String debugCardType = "debug";

}