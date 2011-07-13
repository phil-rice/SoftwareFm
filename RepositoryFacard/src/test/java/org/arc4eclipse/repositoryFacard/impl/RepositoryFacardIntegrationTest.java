package org.arc4eclipse.repositoryFacard.impl;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.arc4eclipse.repositoryClient.api.IRepositoryClient;
import org.arc4eclipse.repositoryFacard.IRepositoryFacard;
import org.arc4eclipse.repositoryFacard.IRepositoryFacardCallback;
import org.arc4eclipse.utilities.maps.Maps;
import org.junit.Test;

public class RepositoryFacardIntegrationTest extends TestCase {

	private final AtomicInteger count = new AtomicInteger();

	@Test
	public void test() {
		IRepositoryFacard<Object, Object, Object, Map<Object, Object>> facard = IRepositoryFacard.Utils.facard(IRepositoryClient.Utils.simplestClient("/repositoryFacardTest"));
		set(facard, "key1", "thing1", "aspect1", "a", 11, "b", 12);
		set(facard, "key2", "thing1", "aspect2", "a", 21, "b", 22);
		set(facard, "key3", "thing2", "aspect1", "a", 31, "b", 32);
		set(facard, "key4", "thing2", "aspect2", "a", 41, "b", 42);
		for (int i = 0; i < 2; i++) {
			get(facard, "key1", "thing1", "aspect1", "a", "11", "b", "12", "jcr:primaryType", "nt:unstructured");
			get(facard, "key2", "thing1", "aspect2", "a", "21", "b", "22", "jcr:primaryType", "nt:unstructured");
			get(facard, "key3", "thing2", "aspect1", "a", "31", "b", "32", "jcr:primaryType", "nt:unstructured");
			get(facard, "key4", "thing2", "aspect2", "a", "41", "b", "42", "jcr:primaryType", "nt:unstructured");
		}
	}

	private void set(IRepositoryFacard<Object, Object, Object, Map<Object, Object>> facard, String key, String thing, String aspect, Object... data) {
		facard.setDetails(key, thing, aspect, Maps.makeMap(data), new IRepositoryFacardCallback<Object, Object, Object, Map<Object, Object>>() {
			@Override
			public void process(Object key, Object thing, Object aspect, Map<Object, Object> data) {
				System.out.println("Set: " + data);
			}
		});
	}

	private void get(IRepositoryFacard<Object, Object, Object, Map<Object, Object>> facard, String key, String thing, String aspect, final Object... expected) {
		facard.getDetails(key, thing, aspect, new IRepositoryFacardCallback<Object, Object, Object, Map<Object, Object>>() {
			@Override
			public void process(Object key, Object thing, Object aspect, Map<Object, Object> data) {
				System.out.println("Get: " + data);
				assertEquals(Maps.makeMap(expected), data);
			}
		});

	}
}
