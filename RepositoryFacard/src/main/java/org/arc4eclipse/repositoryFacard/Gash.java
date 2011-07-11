package org.arc4eclipse.repositoryFacard;

import java.util.Arrays;
import java.util.Map;

import org.arc4eclipse.utilities.maps.Maps;
import org.json.simple.JSONValue;

public class Gash {
	public static void main(String[] args) {
		Map<Object, Object> map = Maps.makeMap("Name", "Sling", //
				"comment", "is a web framework that uses a Java Content Repository, such as Apache Jackrabbit, to store and manage content",//
				"website", "sling.apache.org/",//
				"releases", Arrays.asList("6", "5", "4"),//
				"mailingLists",//
				Arrays.asList(//
						"Sling Users List ", //
						Maps.makeMap("subscribe", "users-subscribe@sling.apache.org"),//
						Maps.makeMap("unsubscribe", "users-unsubscribe@sling.apache.org"),//
						Maps.makeMap("archive", "http://mail-archives.apache.org/mod_mbox/sling-users/"),//
						"Sling Developers List ", //
						Maps.makeMap("subscribe", "dev-subscribe@sling.apache.org"),//
						Maps.makeMap("unsubscribe", "dev-unsubscribe@sling.apache.org"),//
						Maps.makeMap("archive", "http://mail-archives.apache.org/mod_mbox/sling-dev/"))//
				);
		System.out.println(JSONValue.toJSONString(map));
	}
}
