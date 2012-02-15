/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.LineItem;
import org.softwareFm.swt.card.dataStore.CardDataStoreAsyncMock;
import org.softwareFm.swt.card.dataStore.CardDataStoreMock;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.title.TitleSpec;

/** Hello ALyson */
public class CardDataStoreFixture {

	public final static String url1 = "prefix1/prefix2/org/softwareFm/url1";
	public final static String url2 = "prefix1/prefix2/org/softwareFm/url2";

	public final static Map<String, Object> data1a = Maps.makeLinkedMap("tag", "one", "value", "1a");
	public final static Map<String, Object> data1b = Maps.makeLinkedMap("tag", "one", "value", "1b");
	public final static Map<String, Object> data2a = Maps.makeLinkedMap("tag", "two", "value", "2a");
	public final static Map<String, Object> data2b = Maps.makeLinkedMap("tag", "two", "value", "2b");
	public final static Map<String, Object> data2c = Maps.makeLinkedMap("tag", "two", "value", "2c");

	public final static Map<String, Object> data1ap = Maps.makeLinkedMap("tag", "one", "value", "1a", "p", 1);
	public final static Map<String, Object> data1bp = Maps.makeLinkedMap("tag", "one", "value", "1b", "p", 2);
	public final static Map<String, Object> data2ap = Maps.makeLinkedMap("tag", "two", "value", "2a", "q", 1);
	public final static Map<String, Object> data2bp = Maps.makeLinkedMap("tag", "two", "value", "2b", "q", 2);
	public final static Map<String, Object> data2cp = Maps.makeLinkedMap("tag", "two", "value", "2c", "q", 3);

	public final static List<LineItem> kvFromData1a = Arrays.asList(new LineItem(null, "tag", "one"), new LineItem(null, "value", "valuea"));
	public final static List<LineItem> p1q2 = Arrays.asList(new LineItem(null, "p", 1), new LineItem(null, "q", 2));
	public final static Map<String, Object> mapP1Q2 = Maps.makeLinkedMap("p", 1, "q", 2);
	public final static Map<String, Object> data1aWithP1Q2 = Maps.makeLinkedMap("tag", "one", "value", "valuea", "p", 1, "q", 2);

	public static final String url = "some/url";
	public static final List<String> urlAsList = Arrays.asList(url);
	public static final String url1a = "some/url/1a";
	public static final String url1b = "some/url/1b";
	public static final String url2a = "some/url/2a";
	public static final String url2b = "some/url/2b";
	public static final String url2c = "some/url/2c";

	public final static Map<String, Object> dataUrl1 = Maps.makeLinkedMap(//
			"name1", "value1", //
			"name2", "value2", //
			"data1a", data1a,//
			"data1b", data1b,//
			"data2a", data2a,//
			"data2b", data2b,//
			"data2c", data2c);

	public static final Object[] dataForMocks = new Object[] { "some", Maps.newMap(),//
			"/" + url, dataUrl1, //
			"/" + url1a, data1ap, //
			"/" + url1b, data1bp,//
			"/" + url2a, data2ap,//
			"/" + url2b, data2bp,//
			"/" + url2c, data2cp,//
	};
	public static final Object[] dataIndexedByUrlFragment = new Object[] { "some", Maps.newMap(),//
			"1a", data1ap, //
			"1b", data1bp,//
			"2a", data2ap,//
			"2b", data2bp,//
			"2c", data2cp,//
	};

	public static CardConfig syncCardConfig(Device device) {
		return new CardConfig(ICardFactory.Utils.cardFactory(), rawCardStore()).withTitleSpecFn(Functions.<ICardData, TitleSpec> constant(TitleSpec.noTitleSpec(device.getSystemColor(SWT.COLOR_WHITE))));
	}

	public static CardConfig asyncCardConfig(Device device) {
		return new CardConfig(ICardFactory.Utils.cardFactory(), rawAsyncCardStore()).withTitleSpecFn(Functions.<ICardData, TitleSpec> constant(TitleSpec.noTitleSpec(device.getSystemColor(SWT.COLOR_WHITE))));
	}

	public static final CardDataStoreMock rawCardStore() {
		return new CardDataStoreMock(dataForMocks);
	}

	public static final CardDataStoreAsyncMock rawAsyncCardStore() {
		return new CardDataStoreAsyncMock(dataForMocks);
	}

}