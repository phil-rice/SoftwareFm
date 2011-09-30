package org.softwareFm.display.timeline;

import java.util.Arrays;

import junit.framework.TestCase;

public class PlayListTest extends TestCase {
	public final static PlayItem playItem1_1 = new PlayItem("ft1_1", "url1");
	public final static PlayItem playItem1_2 = new PlayItem("ft1_2", "url2");
	public final static PlayItem playItem1_3 = new PlayItem("ft1_3", "url3");
	public final static PlayItem playItem2_1 = new PlayItem("ft2_1", "url1");
	public final static PlayItem playItem2_2 = new PlayItem("ft2_2", "url2");

	public  IPlayList list1 ;
	public  IPlayList list2 ;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		 list1 = IPlayList.Utils.make("list1", Arrays.asList(playItem1_1, playItem1_2, playItem1_3));
		 list2 = IPlayList.Utils.make("list2", Arrays.asList(playItem2_1, playItem2_2));
		
	}
}
