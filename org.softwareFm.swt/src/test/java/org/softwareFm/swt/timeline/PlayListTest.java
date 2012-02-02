/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.timeline;

import java.util.Arrays;

import junit.framework.TestCase;

public abstract class PlayListTest extends TestCase {
	public final static PlayItem playItem1_1 = new PlayItem("ft1_1", "url1");
	public final static PlayItem playItem1_2 = new PlayItem("ft1_2", "url2");
	public final static PlayItem playItem1_3 = new PlayItem("ft1_3", "url3");
	public final static PlayItem playItem2_1 = new PlayItem("ft2_1", "url1");
	public final static PlayItem playItem2_2 = new PlayItem("ft2_2", "url2");

	public IPlayList list1;
	public IPlayList list2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		list1 = IPlayList.Utils.make("list1", Arrays.asList(playItem1_1, playItem1_2, playItem1_3));
		list2 = IPlayList.Utils.make("list2", Arrays.asList(playItem2_1, playItem2_2));

	}
}