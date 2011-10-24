package org.softwareFm.display.data;

import java.util.Arrays;
import java.util.Map;

import junit.framework.TestCase;

import org.softwareFm.display.urlGenerator.UrlGenerator;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.tests.Tests;

public class GuiDataStoreTest extends TestCase {

	private GuiDataStore store;
	private UrlToDataMock urlToData;
	private final Map<String, Object> context1 = Maps.makeMap("a", 1);
	private GuiDataListenerMock listener2;
	private GuiDataListenerMock listener1;
	private Map<String, Object> data1;
	private Map<String, Object> datad1;
	private Map<String, Object> datad2;
	private final ResourceGetterMock resourceGetterMock = new ResourceGetterMock("a.1", "x", "b.2", "y");
	private final Map<String, Object> rawData1 = Maps.makeImmutableMap("rawDataKey", "rawDataValue", "a", 1, "b", 2);

	public void testNoneDataRequestsGoToResourceGetter() {
		assertEquals("x", store.getDataFor("a.1"));
		assertEquals("y", store.getDataFor("b.2"));
	}

	public void testGettingData() {
		makeRosyView();
		store.processData("entity1", rawData1, context1);

		assertEquals("one", store.getDataFor("data.entity1.linkData1"));
		assertEquals("two", store.getDataFor("data.entity1.linkData2"));
		assertEquals(1, store.getDataFor("data.entityd1.ent1"));
		assertEquals(11, store.getDataFor("data.entityd1.ent1.sub"));
		assertEquals(2, store.getDataFor("data.entityd2.ent2"));
		assertEquals(21, store.getDataFor("data.entityd2.ent2.sub1.sub2"));
	}

	@SuppressWarnings("unchecked")
	public void testProcessDataCallsDependantEntities() {
		makeRosyView();

		store.processData("entity1", rawData1, context1);
		assertEquals(Arrays.asList("entity1", "entityd1", "entityd2"), urlToData.entities);
		assertEquals(Arrays.asList(context1, context1, context1), urlToData.contexts);
		assertEquals(Arrays.asList("original/rawdatavalue", "depOne/one", "depTwo/two"), urlToData.urls);
	}

	public void testProcessDataSetsLastUrlFor() {
		makeRosyView();

		store.processData("entity1", rawData1, context1);
		assertEquals("original/rawdatavalue", store.lastUrlFor("entity1"));
		assertEquals("depOne/one", store.lastUrlFor("entityd1"));
		assertEquals("depTwo/two", store.lastUrlFor("entityd2"));
	}

	@SuppressWarnings("unchecked")
	public void testProcessDataDoesntCallUrlToDataWhenUrlCached() {
		makeRosyView();

		store.processData("entity1", rawData1, context1);
		store.processData("entity1", rawData1, context1);
		assertEquals(Arrays.asList("entity1", "entityd1", "entityd2"), urlToData.entities);
		assertEquals(Arrays.asList(context1, context1, context1), urlToData.contexts);
		assertEquals(Arrays.asList("original/rawdatavalue", "depOne/one", "depTwo/two"), urlToData.urls);
	}

	public void testRawData() {
		makeRosyView();

		store.processData("entity1", rawData1, context1);
		assertEquals(rawData1, store.getLastRawData("entity1"));
		assertEquals(1, store.getDataFor("data.raw.entity1.a"));
		assertEquals(2, store.getDataFor("data.raw.entity1.b"));

	}

	public void testListenersNotified() {
		makeRosyView();

		store.processData("entity1", rawData1, context1);
		store.processData("entity1", rawData1, context1);
		checkListener(listener1);
		checkListener(listener2);
	}

	private void checkListener(GuiDataListenerMock listener) {
		assertEquals(Arrays.asList("entity1", "entityd1", "entityd2", "entity1", "entityd1", "entityd2"), listener.entities);
		assertEquals(Arrays.asList("original/rawdatavalue", "depOne/one", "depTwo/two", "original/rawdatavalue", "depOne/one", "depTwo/two"), listener.urls);
	}

	private void makeRosyView() {
		store.urlGenerator("urlKey1", new UrlGenerator("original/{2}", "rawDataKey"));
		store.urlGenerator("urlKey2", new UrlGenerator("depOne/{2}", "linkData1"));
		store.urlGenerator("urlKey3", new UrlGenerator("depTwo/{2}", "linkData2"));
		store.entity("entity1", "urlKey1");
		store.dependant("entity1", "entityd1", "urlKey2");
		store.dependant("entity1", "entityd2", "urlKey3");
	}

	public void testThrowsExceptionIfReferencingUrlGeneratorThatDoesntExist() {
		store.urlGenerator("urlKey2", new UrlGenerator("two/{2}", "rawDataKey"));
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				store.entity("entity1", "urlKey1");
			}
		});

		assertEquals("Unrecognised UrlGenerator urlKey1. Legal values are [urlKey2]", e.getMessage());
	}

	public void testThrowsExceptionIfDependantEntityReferencesNotExistantEntity() {
		store.urlGenerator("urlKey1", new UrlGenerator("two/{2}", "rawDataKey"));
		store.entity("entity1", "urlKey1");
		IllegalArgumentException e2 = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				store.dependant("entity2", "entityd1", "asdjk");
			}
		});
		assertEquals("Unrecognised entity entity2. Legal values are [entity1]", e2.getMessage());

	}

	public void testThrowsExceptionWithDuplicateUrlGenerator() {
		store.urlGenerator("urlKey1", new UrlGenerator("one/{2}", "rawDataKey"));
		store.urlGenerator("key1", new UrlGenerator("two/{2}", "rawDataKey"));
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				store.urlGenerator("key1", new UrlGenerator("two/{2}", "rawDataKey"));
			}
		});
		assertEquals("Duplicate UrlGenerator key1", e.getMessage());
	}

	public void testThrowsExceptionIfSameEntityRegisteredTwice() {
		store.urlGenerator("urlKey1", new UrlGenerator("one/{2}", "rawDataKey"));
		store.entity("entity1", "urlKey1");
		IllegalArgumentException e1 = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				store.entity("entity1", "urlKey1");
			}
		});
		assertEquals("Duplicate Entity entity1", e1.getMessage());
		IllegalArgumentException e2 = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				store.dependant("entity1", "entity1", "asdjk");
			}
		});
		assertEquals("Duplicate Entity entity1", e2.getMessage());
	}

	public void testThrowsExceptionIfDependantRegisteredTwice() {
		store.urlGenerator("urlKey1", new UrlGenerator("one/{2}", "rawDataKey"));
		store.entity("entity1", "urlKey1");
		store.dependant("entity1", "entityd1", "urlKey1");
		IllegalArgumentException e1 = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				store.entity("entityd1", "urlKey1");
			}
		});
		assertEquals("Duplicate Entity entityd1", e1.getMessage());
		IllegalArgumentException e2 = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				store.dependant("entity1", "entityd1", "asdjk");
			}
		});
		assertEquals("Duplicate Entity entity1", e2.getMessage());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		data1 = Maps.<String, Object> makeLinkedMap("linkData1", "one", "linkData2", "two");
		datad1 = Maps.<String, Object> makeLinkedMap("ent1", 1, "ent1.sub", 11);
		datad2 = Maps.<String, Object> makeLinkedMap("ent2", 2, "ent2.sub1.sub2", 21);
		urlToData = new UrlToDataMock(//
				"entity1", Arrays.asList(data1),//
				"entityd1", Arrays.asList(datad1),//
				"entityd2", Arrays.asList(datad2));
		store = new GuiDataStore(urlToData, resourceGetterMock, ICallback.Utils.rethrow());
		listener1 = new GuiDataListenerMock();
		listener2 = new GuiDataListenerMock();
		store.addGuiDataListener(listener1);
		store.addGuiDataListener(listener2);
	}
}
