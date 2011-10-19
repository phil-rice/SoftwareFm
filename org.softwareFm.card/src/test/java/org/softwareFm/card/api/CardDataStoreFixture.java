package org.softwareFm.card.api;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.softwareFm.utilities.maps.Maps;

public class CardDataStoreFixture {

	public final static String url1 = "prefix1/prefix2/org/softwareFm/url1";
	public final static String url2 = "prefix1/prefix2/org/softwareFm/url2";

	public final static Map<String, Object> data1a = Maps.makeLinkedMap("tag", "one", "value", "valuea");
	public final static Map<String, Object> data1b = Maps.makeLinkedMap("tag", "one", "value", "valueb");
	public final static Map<String, Object> data2a = Maps.makeLinkedMap("tag", "two", "value", "valuea");
	public final static Map<String, Object> data2b = Maps.makeLinkedMap("tag", "two", "value", "valueb");
	public final static Map<String, Object> data2c = Maps.makeLinkedMap("tag", "two", "value", "valuec");

	public final static List<KeyValue> kvFromData1a = Arrays.asList(new KeyValue("tag", "one"), new KeyValue("value", "valuea"));
	public final static List<KeyValue> p1q2 = Arrays.asList(new KeyValue("p", 1), new KeyValue("q", 2));
	public final static Map<String, Object> mapP1Q2 = Maps.makeLinkedMap("p", 1, "q", 2);
	public final static Map<String, Object> data1aWithP1Q2 = Maps.makeLinkedMap("tag", "one", "value", "valuea", "p", 1, "q", 2);

	public static final String url = "some/url";
	public static final String url1a = "some/url/largeName";
	public static final String url1b = "some/url/1b";

	public final static Map<String, Object> dataUrl1 = Maps.makeLinkedMap(//
			"name1", "value1", //
			"name2", "value2", //
			"data1a", data1a,//
			"data1b", data1b,//
			"data2a", data2a,//
			"data2b", data2b,//
			"data2c", data2c);

	private static final Object[] dataForMocks = new Object[] { "some", Maps.newMap(),//
			url, dataUrl1, //
			url1a, data1a, //
			url1b, data1b

	};

	public static final CardDataStoreMock rawCardStore() {
		return new CardDataStoreMock(dataForMocks);
	}

	public static final CardDataStoreAsyncMock rawAsyncCardStore() {
		return new CardDataStoreAsyncMock(dataForMocks);
	}

}
