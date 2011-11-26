/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.tutorial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Store {
	public static final String helloWorld = "helloWorld";

	public static void store(String userId, String projectId) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/softwarefm?user=root&password=iwtbde");
			try {
				Statement statement = conn.createStatement();
				try {
					statement.execute("insert into `usage`(userid,url) values ('" + userId + "','" + projectId + "')");
				} finally {
					statement.close();
				}
			} finally {

				conn.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}