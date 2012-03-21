/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.internal;

import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.crowdsource.api.server.IUsage;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class Usage implements IUsage {

	private final JdbcTemplate template;

	public Usage(BasicDataSource dataSource) {
		template = new JdbcTemplate(dataSource);
	}

	@Override
	public void start() {
		try {
			template.queryForInt("select count(*) from `usage`");
		} catch (DataAccessException e) {
			template.execute("create table `usage` (ip varchar(100), url varchar(200), stamp varchar(100),duration integer(15))");
		}
	}

	@Override
	public void shutdown() {
		try {
			((BasicDataSource) template.getDataSource()).close();
		} catch (SQLException e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public void monitor(final String ip, final String url, long duration) {
		// template.execute("insert into `usage`(ip, url, stamp) values('"+ ip+"','" + url+"','" + new Date().toString()+"')");
		template.update("insert into `usage`(ip, url, stamp, duration) values(?,?,?,?)", ip, url, new Date().toString(), duration);
	}

//	public static void main(String[] args) {
//		IUsage usage = IUsage.Utils.defaultUsage();
//		usage.start();
//		usage.monitor("ip1", "url1", 10);
//		usage.monitor("ip2", "url2", 20);
//	}

}