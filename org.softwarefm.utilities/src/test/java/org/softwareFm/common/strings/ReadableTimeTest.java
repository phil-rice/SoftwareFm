/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.strings;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import junit.framework.TestCase;

import org.junit.Test;
import org.softwareFm.crowdsource.utilities.strings.ReadableTime;

public class ReadableTimeTest extends TestCase {
	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
	private final ReadableTime readableTime = new ReadableTime();

	@Test
	public void test() throws Exception {
		check("20/2/2012 10:00:01", "in the future!");
		check("20/2/2012 09:59:59", "just now");
		check("20/2/2012 09:59:01", "just now");
		check("20/2/2012 09:58:01", "1 minute ago");
		check("20/2/2012 09:55:01", "4 minutes ago");
		check("20/2/2012 09:01:01", "58 minutes ago");
		check("20/2/2012 08:59:01", "1 hour ago");
		check("19/2/2012 10:00:01", "23 hours ago");
		check("19/2/2012 09:59:01", "1 day ago");
		check("18/2/2012 09:59:01", "2 days ago");
		check("19/1/2012 09:59:01", "1 month ago");
		check("19/12/2011 09:59:01", "2 months ago");
		check("19/3/2011 09:59:01", "11 months ago");
		check("19/2/2011 09:59:01", "1 year ago");
		check("19/2/2010 09:59:01", "2 years ago");
	}

	private void check(String thenString, String expected) throws ParseException {
		long now = simpleDateFormat.parse("20/2/2012 10:00:00").getTime();
		long then = simpleDateFormat.parse(thenString).getTime();
		assertEquals(expected, readableTime.readableNameFor(now, then));
	}

}