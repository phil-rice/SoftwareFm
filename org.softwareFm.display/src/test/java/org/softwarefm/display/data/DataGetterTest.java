package org.softwarefm.display.data;

import junit.framework.TestCase;

public class DataGetterTest extends TestCase {

	public void testDataPrefixesGoToNestedDataGetterOthersGoToResourceGetter() {
		DataGetter dataGetter = new DataGetter(new DataGetterMock("a.1", 1, "b.2", 2), new ResourceGetterMock("a.1", "x", "b.2", "y"));
		assertEquals("x", dataGetter.getDataFor("a.1"));
		assertEquals("y", dataGetter.getDataFor("b.2"));
		assertEquals(1, dataGetter.getDataFor("data.a.1"));
		assertEquals(2, dataGetter.getDataFor("data.b.2"));
	}

	public void testSetAndGetRawData(){
		DataGetter dataGetter = new DataGetter(null, null);
		dataGetter.setRawData("rawData1");
		assertEquals("rawData1", dataGetter.getLastRawData());
		dataGetter.setRawData("rawData2");
		assertEquals("rawData2", dataGetter.getLastRawData());
	}
}
