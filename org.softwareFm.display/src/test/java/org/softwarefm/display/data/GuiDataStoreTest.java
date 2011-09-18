package org.softwarefm.display.data;

import java.util.Arrays;
import java.util.Map;

import junit.framework.TestCase;

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

	@SuppressWarnings("unchecked")
	public void testProcessDataCallsDependantEntities() {
		makeRosyView();

		store.processData("rawData", context1);
		assertEquals(Arrays.asList("entity1", "entityd1", "entityd2"), urlToData.entities);
		assertEquals(Arrays.asList(context1, context1, context1), urlToData.contexts);
		assertEquals(Arrays.asList("<Url entity1: rawData>", "<Url entityd1: one>", "<Url entityd2: two>"), urlToData.urls);
	}

	public void testProcessDataSetsLastUrlFor() {
		makeRosyView();
		store.processData("rawData", context1);
		assertEquals("<Url entity1: rawData>", store.lastUrlFor("entity1"));
		assertEquals("<Url entityd1: one>", store.lastUrlFor("entityd1"));
		assertEquals("<Url entityd2: two>", store.lastUrlFor("entityd2"));
	}

	@SuppressWarnings("unchecked")
	public void testProcessDataDoesntCallUrlToDataWhenUrlCached() {
		makeRosyView();

		store.processData("rawData", context1);
		store.processData("rawData", context1);
		assertEquals(Arrays.asList("entity1", "entityd1", "entityd2"), urlToData.entities);
		assertEquals(Arrays.asList(context1, context1, context1), urlToData.contexts);
		assertEquals(Arrays.asList("<Url entity1: rawData>", "<Url entityd1: one>", "<Url entityd2: two>"), urlToData.urls);
	}

	public void testListenersNotified() {
		makeRosyView();

		store.processData("rawData", context1);
		store.processData("rawData", context1);
		checkListener(listener1);
		checkListener(listener2);
	}

	@SuppressWarnings("unchecked")
	private void checkListener(GuiDataListenerMock listener) {
		assertEquals(Arrays.asList("entity1", "entityd1", "entityd2", "entity1", "entityd1", "entityd2"), listener.entities);
		assertEquals(Arrays.asList(context1, context1, context1, context1, context1, context1), listener.contexts);
		assertEquals(Arrays.asList("<Url entity1: rawData>", "<Url entityd1: one>", "<Url entityd2: two>", "<Url entity1: rawData>", "<Url entityd1: one>", "<Url entityd2: two>"), listener.urls);
		assertEquals(Arrays.asList(data1, datad1, datad2, data1, datad1, datad2), listener.datas);
	}

	public void testGettingData() {
		makeRosyView();
		store.processData("rawData", context1);

		assertEquals("one", store.getDataFor("entity1.linkData1"));
		assertEquals("two", store.getDataFor("entity1.linkData2"));
		assertEquals(1, store.getDataFor("entityd1.ent1"));
		assertEquals(11, store.getDataFor("entityd1.ent1.sub"));
		assertEquals(2, store.getDataFor("entityd2.ent2"));
		assertEquals(21, store.getDataFor("entityd2.ent2.sub1.sub2"));
	}

	private void makeRosyView() {
		store.urlGenerator("urlKey1", new UrlGeneratorMock("1"));
		store.urlGenerator("urlKey2", new UrlGeneratorMock("2"));
		store.urlGenerator("urlKey3", new UrlGeneratorMock("3"));
		store.entity("entity1", "urlKey1");
		store.dependant("entity1", "entityd1", "linkData1", "urlKey2");
		store.dependant("entity1", "entityd2", "linkData2", "urlKey3");
	}

	public void testThrowsExceptionIfReferencingUrlGeneratorThatDoesntExist() {
		store.urlGenerator("urlKey2", new UrlGeneratorMock("2"));
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				store.entity("entity1", "urlKey1");
			}
		});

		assertEquals("Unrecognised UrlGenerator urlKey1. Legal values are [urlKey2]", e.getMessage());
	}

	public void testThrowsExceptionIfDependantEntityReferencesNotExistantEntity() {
		store.urlGenerator("urlKey1", new UrlGeneratorMock("1"));
		store.entity("entity1", "urlKey1");
		IllegalArgumentException e2 = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				store.dependant("entity2", "entityd1", "asdy1", "asdjk");
			}
		});
		assertEquals("Unrecognised entity entity2. Legal values are [entity1]", e2.getMessage());

	}

	public void testThrowsExceptionWithDuplicateUrlGenerator() {
		store.urlGenerator("urlKey1", new UrlGeneratorMock("1"));
		store.urlGenerator("key1", new UrlGeneratorMock("1"));
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				store.urlGenerator("key1", new UrlGeneratorMock("2"));
			}
		});
		assertEquals("Duplicate UrlGenerator key1", e.getMessage());
	}

	public void testThrowsExceptionIfSameEntityRegisteredTwice() {
		store.urlGenerator("urlKey1", new UrlGeneratorMock("1"));
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
				store.dependant("entity1", "entity1", "entity1", "asdjk");
			}
		});
		assertEquals("Duplicate Entity entity1", e2.getMessage());
	}

	public void testThrowsExceptionIfDependantRegisteredTwice() {
		store.urlGenerator("urlKey1", new UrlGeneratorMock("1"));
		store.entity("entity1", "urlKey1");
		store.dependant("entity1", "entityd1", "link", "urlKey1");
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
				store.dependant("entity1", "entityd1", "asd", "asdjk");
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
		store = new GuiDataStore(urlToData, ICallback.Utils.rethrow());
		listener1 = new GuiDataListenerMock();
		listener2 = new GuiDataListenerMock();
		store.addGuiDataListener(listener1);
		store.addGuiDataListener(listener2);
	}
}
