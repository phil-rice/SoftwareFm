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
